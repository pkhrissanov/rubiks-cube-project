public class TestSolver {

    public static void main(String[] args) {

        // -------------------------------------------------------------
        // CHANGE THIS PATH to whichever test file you want to solve
        // -------------------------------------------------------------
        String input = "testCases/scramble16.txt";
        // -------------------------------------------------------------

        try {
            // Load cube from file
            Cube start = new Cube(input);

            System.out.println("=== INPUT CUBE ===");
            start.printNet();

            // Build heuristic DB
            System.out.println("\nBuilding heuristic DB (depth 5)...");
            Solver.heuristicDB = HeuristicDB.build(5);
            System.out.println("Heuristic DB size: " + Solver.heuristicDB.size());

            // Solve
            System.out.println("\nSolving with IDA*...");
            long t0 = System.currentTimeMillis();
            String solution = Solver.solve(start);
            long t1 = System.currentTimeMillis();

            System.out.println("\n=== SOLUTION FOUND ===");
            System.out.println("Moves: " + solution);
            System.out.println("Time: " + (t1 - t0) + " ms");

            // Save to output.txt
            try (java.io.PrintWriter pw = new java.io.PrintWriter("output.txt")) {
                pw.println(solution);
            }
            System.out.println("Solution saved to output.txt");

            // Apply solution and print final cube
            if (!solution.equals("NO SOLUTION") && !solution.isEmpty()) {
                Cube solved = start.clone();
                solved.applyMoves(solution);

                System.out.println("\n=== CUBE AFTER APPLYING SOLUTION ===");
                solved.printNet();

                System.out.println("\nCube solved? " + solved.isSolved());
            }

        } catch (Exception e) {
            System.out.println("Error running TestSolver:");
            e.printStackTrace();
        }
    }
}
