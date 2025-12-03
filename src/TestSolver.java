
import java.io.PrintWriter;

public class TestSolver {

    public static void main(String[] args) {

        // CHANGE THIS PATH TO YOUR SCRAMBLE FILE
        String input = "testCases/scramble40.txt";

        try {
            System.out.println("=== Loading cube from file ===");
            Cube start = new Cube(input);

            System.out.println("\n=== INITIAL CUBE ===");
            start.printNet();

            // Build heuristic DB once
            System.out.println("\n=== Building heuristic DB (depth 5) ===");
            Solver.heuristicDB = HeuristicDB.build(10);
            System.out.println("Heuristic DB size: " + Solver.heuristicDB.size());

            // Solve
            System.out.println("\n=== Solving using IDA* ===");
            long t0 = System.currentTimeMillis();
            String solution = Solver.solve(start);
            long t1 = System.currentTimeMillis();

            System.out.println("\n=== SOLUTION FOUND ===");
            System.out.println("Moves: " + solution);
            System.out.println("Time: " + (t1 - t0) + " ms");

            // Write result
            try (PrintWriter pw = new PrintWriter("output.txt")) {
                pw.println(solution);
            }
            System.out.println("Solution saved to output.txt");

            // Apply the solution to show final solved cube
            Cube solved = start.clone();
            solved.applyMoves(solution);

            System.out.println("\n=== FINAL CUBE AFTER APPLYING SOLUTION ===");
            solved.printNet();

            // Check if solved
            System.out.println("\nCube solved? " + solved.isSolved());

        } catch (Exception e) {
            System.out.println("❌ Error running TestSolver:");
            e.printStackTrace();
        }
    }
}
