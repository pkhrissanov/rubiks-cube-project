import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;

public class Solver {

    // Allowed moves (clockwise only)
    private static final String[] MOVES = {"U", "D", "L", "R", "F", "B"};

    // Heuristic DB (filled at startup)
    public static HashMap<String, Integer> heuristicDB;

    private static final int MAX_IDA_DEPTH = 30;
    private static final int HEURISTIC_DB_DEPTH = 5;
    private static final int DEFAULT_H = 20;

    private static int threshold;
    private static int nextThreshold;
    private static String bestSolution;

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("File names are not specified");
            System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName()
                    + " input_file output_file");
            return;
        }

        try {
            System.out.println("Building heuristic DB...");
            heuristicDB = HeuristicDB.build(HEURISTIC_DB_DEPTH);
            System.out.println("Heuristic DB size: " + heuristicDB.size());

            Cube start = new Cube(args[0]);
            String answer = solve(start);

            try (PrintWriter out = new PrintWriter(args[1])) {
                out.println(answer);
            }

            System.out.println("Solved: " + answer);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------
    // PUBLIC SOLVER API
    // -----------------------------------------------------------------------------------
    public static String solve(Cube start) {

        String startState = start.toString();
        if (start.isSolved()) return "";

        if (heuristicDB == null) {
            heuristicDB = HeuristicDB.build(HEURISTIC_DB_DEPTH);
        }

        threshold = h(startState);

        while (true) {
            nextThreshold = Integer.MAX_VALUE;
            bestSolution = null;

            StringBuilder path = new StringBuilder();

            if (dfs(start, 0, path)) {
                return bestSolution;
            }

            if (nextThreshold > MAX_IDA_DEPTH || nextThreshold == Integer.MAX_VALUE) {
                return "NO SOLUTION";
            }

            threshold = nextThreshold;
        }
    }

    // -----------------------------------------------------------------------------------
    // IDA* DFS WITH MOVE PRUNING (no visited set)
    // -----------------------------------------------------------------------------------
    private static boolean dfs(Cube cube, int g, StringBuilder path) {

        String state = cube.toString();
        int h = h(state);
        int f = g + h;

        if (f > threshold) {
            if (f < nextThreshold) nextThreshold = f;
            return false;
        }

        if (cube.isSolved()) {
            bestSolution = path.toString();
            return true;
        }

        if (g >= MAX_IDA_DEPTH) return false;

        // ----- MOVE PRUNING -----
        String lastMove = (path.length() == 0 ? null : String.valueOf(path.charAt(path.length() - 1)));

        for (String move : MOVES) {

            // PRUNE: Same face twice ("U" followed by "U")
            // In your system, U' is implemented as U U U, so avoiding repeated face
            // already avoids immediate inverses / redundancies.
            if (lastMove != null && sameFace(lastMove, move)) {
                continue;
            }

            Cube next = cube.clone();
            next.move(move);

            path.append(move);

            if (dfs(next, g + 1, path)) {
                return true;
            }

            path.deleteCharAt(path.length() - 1);
        }

        return false;
    }

    // -----------------------------------------------------------------------------------
    // HELPERS
    // -----------------------------------------------------------------------------------
    private static int h(String state) {
        Integer v = heuristicDB.get(state);
        return (v != null ? v : DEFAULT_H);
    }

    private static boolean sameFace(String a, String b) {
        return a.charAt(0) == b.charAt(0);
    }
}
