

import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;

public class Solver {

    // Moves we allow in search and heuristic DB
    private static final String[] MOVES = {"U", "D", "L", "R", "F", "B"};

    // Hard limits
    private static final int MAX_IDA_DEPTH = 30;    // Max search depth
    private static final int HEURISTIC_DB_DEPTH = 5; // BFS depth for heuristic DB
    private static final int DEFAULT_H = 20;        // fallback heuristic (God's number)

    // Heuristic DB: state string -> distance from solved
    static HashMap<String, Integer> heuristicDB;

    // IDA* state
    private static int threshold;       // current f-limit
    private static int nextThreshold;   // minimal f that exceeded current threshold
    private static String bestSolution; // solution path (as string of letters)

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("File names are not specified");
            System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
            return;
        }

        try {
            // Build heuristic DB once
            System.out.println("Building heuristic DB (depth " + HEURISTIC_DB_DEPTH + ")...");
            heuristicDB = HeuristicDB.build(HEURISTIC_DB_DEPTH);
            System.out.println("Heuristic DB size: " + heuristicDB.size());

            // Load start cube from file
            Cube start = new Cube(args[0]);

            // Solve
            String answer = solve(start);

            // Write answer
            try (PrintWriter out = new PrintWriter(args[1])) {
                out.println(answer);
            }

            System.out.println("Solved. Moves: " + answer);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Public solve method (can be used without main)
    public static String solve(Cube start) {
        if (start.isSolved()) return "";

        if (heuristicDB == null) {
            heuristicDB = HeuristicDB.build(HEURISTIC_DB_DEPTH);
        }

        String startState = start.toString();
        threshold = h(startState);
        if (threshold < 0) threshold = 0;

        while (true) {
            nextThreshold = Integer.MAX_VALUE;
            bestSolution = null;

            StringBuilder path = new StringBuilder();
            boolean found = dfs(start, 0, path);

            if (found) return bestSolution;
            if (nextThreshold == Integer.MAX_VALUE || nextThreshold > MAX_IDA_DEPTH) {
                return "NO SOLUTION";
            }

            threshold = nextThreshold;
        }
    }

    // Depth-first search for IDA*
    // No visited set (as per your requirement)
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

        for (String move : MOVES) {
            Cube next = cube.clone();
            next.move(move);

            // record move (single letter)
            path.append(move);

            if (dfs(next, g + 1, path)) return true;

            // undo recorded move
            path.deleteCharAt(path.length() - 1);
        }

        return false;
    }

    // Heuristic lookup
    private static int h(String state) {
        Integer v = heuristicDB.get(state);
        return (v != null) ? v : DEFAULT_H;
    }
}
