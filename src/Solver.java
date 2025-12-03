import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solver {

    // ===========================================================
    // 12 MOVES — MUST MATCH CUBE & HEURISTIC DB
    // ===========================================================
    private static final String[] MOVES = {
            "U", "U'",
            "D", "D'",
            "L", "L'",
            "R", "R'",
            "F", "F'",
            "B", "B'"
    };

    // inverse moves
    private static final HashMap<String, String> INVERSE = new HashMap<>();
    static {
        INVERSE.put("U", "U'");     INVERSE.put("U'", "U");
        INVERSE.put("D", "D'");     INVERSE.put("D'", "D");
        INVERSE.put("L", "L'");     INVERSE.put("L'", "L");
        INVERSE.put("R", "R'");     INVERSE.put("R'", "R");
        INVERSE.put("F", "F'");     INVERSE.put("F'", "F");
        INVERSE.put("B", "B'");     INVERSE.put("B'", "B");
    }

    // ===========================================================
    // HEURISTIC SETTINGS
    // ===========================================================
    public static HashMap<String, Integer> heuristicDB;
    private static final int HEURISTIC_DB_DEPTH = 5;
    private static final int DEFAULT_H = HEURISTIC_DB_DEPTH + 1;

    private static final int MAX_IDA_DEPTH = 30;

    private static int threshold;
    private static int nextThreshold;
    private static List<String> bestPath;

    // ===========================================================
    // MAIN
    // ===========================================================
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("usage: java Solver input.txt output.txt");
            return;
        }

        try {
            System.out.println("Building heuristic DB...");
            heuristicDB = HeuristicDB.build(HEURISTIC_DB_DEPTH);
            System.out.println("DB states: " + heuristicDB.size());

            Cube start = new Cube(args[0]);
            String solution = solve(start);

            try (PrintWriter out = new PrintWriter(args[1])) {
                out.println(solution);
            }

            System.out.println("Solution: " + solution);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
    // PUBLIC: solve a cube
    // ===========================================================
    public static String solve(Cube start) {

        if (start.isSolved()) return "";

        if (heuristicDB == null) {
            heuristicDB = HeuristicDB.build(HEURISTIC_DB_DEPTH);
        }

        String startState = start.toString();
        threshold = h(startState);

        while (true) {
            nextThreshold = Integer.MAX_VALUE;
            bestPath = null;

            ArrayList<String> path = new ArrayList<>();

            if (dfs(start, 0, null, path)) {
                return convertOutput(bestPath);
            }

            if (nextThreshold > MAX_IDA_DEPTH ||
                    nextThreshold == Integer.MAX_VALUE) {
                return "NO SOLUTION";
            }

            threshold = nextThreshold;
        }
    }

    // ===========================================================
    // IDA* DFS WITH PRUNING
    // ===========================================================
    private static boolean dfs(Cube cube, int g, String lastMove, List<String> path) {

        // Current state string + heuristic
        String state = cube.toString();
        int h = h(state);
        int f = g + h;

        // === MAIN DFS DEBUG HEADER ===
        System.out.println(
                "-----------------------------------------------------\n" +
                        "[DFS] depth g=" + g +
                        " | h=" + h +
                        " | f=" + f +
                        " | threshold=" + threshold +
                        " | lastMove=" + lastMove +
                        " | stateHash=" + state.hashCode()
        );

        // IDA* threshold check
        if (f > threshold) {
            System.out.println("[DFS] f > threshold (" + f + " > " + threshold + ")  -> cutoff, nextThreshold=" + nextThreshold);
            nextThreshold = Math.min(nextThreshold, f);
            return false;
        }

        // Goal test
        if (cube.isSolved()) {
            System.out.println("[DFS] SOLVED at depth " + g);
            bestPath = new ArrayList<>(path);
            return true;
        }

        // Depth limit
        if (g >= MAX_IDA_DEPTH) {
            System.out.println("[DFS] Reached MAX_IDA_DEPTH, abort");
            return false;
        }

        // Iterate over all moves
        for (String move : MOVES) {

            System.out.println("Trying move=" + move + " after lastMove=" + lastMove);

            // === INVERSE PRUNING ===
            if (lastMove != null && INVERSE.get(move).equals(lastMove)) {
                System.out.println("   PRUNE: inverse move detected -> " + lastMove + " then " + move);
                continue;
            }

            // === SAME-FACE PRUNING ===
            if (lastMove != null && move.charAt(0) == lastMove.charAt(0)) {
                System.out.println("   PRUNE: same face -> " + lastMove + " then " + move);
                continue;
            }

            // ==== APPLY MOVE ====
            String before = state; // cube before clone
            Cube next = cube.clone();
            next.move(move);
            String after = next.toString();

            System.out.println("   MOVE APPLIED: " + move +
                    " | beforeHash=" + before.hashCode() +
                    " | afterHash=" + after.hashCode());

            // ==== DESCEND ====
            path.add(move);

            if (dfs(next, g + 1, move, path)) {
                return true;
            }

            // ==== BACKTRACK ====
            System.out.println("   BACKTRACK from move=" + move + " returning to depth " + g);
            path.remove(path.size() - 1);
        }

        // No solution on this branch
        System.out.println("[DFS] No moves worked at depth g=" + g + ", returning");
        return false;
    }



    private static Cube cubeFromState(String s) {
        if (s.length() != 54) {
            throw new IllegalArgumentException("State must be length 54, got " + s.length());
        }

        Cube c = new Cube(); // makes ['X','X','X',...]
        for (int i = 0; i < 54; i++) {
            c.cube.set(i, s.charAt(i));
        }
        return c;
    }
    // ===========================================================
    // HEURISTIC
    // ===========================================================
    private static int h(String state) {
        // First try the pattern DB (exact values up to HEURISTIC_DB_DEPTH)
        if (heuristicDB != null) {
            Integer v = heuristicDB.get(state);
            if (v != null) return v;
        }

        // Otherwise compute a fallback heuristic from sticker mismatches
        Cube c = cubeFromState(state);
        return c.heuristicDistance();
    }


    // ===========================================================
    // OUTPUT FORMAT CONVERSION
    // ===========================================================
    private static String convertOutput(List<String> path) {
        StringBuilder out = new StringBuilder();

        for (String m : path) {
            char face = m.charAt(0);

            if (m.length() == 1) {
                out.append(face);
            } else {
                // X' = X X X (three clockwise turns)
                out.append(face).append(face).append(face);
            }
        }
        return out.toString();
    }
}
