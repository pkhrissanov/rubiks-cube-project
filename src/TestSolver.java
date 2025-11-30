public class TestSolver {

    public static void main(String[] args) {
        try {

            // Load scrambled cube
            Cube start = new Cube("testCases/scramble16.txt");

            System.out.println("========= START CUBE =========");
            start.printNet();
            System.out.println();

            // Create solver with depth limit = 5
            Solver solver = new Solver(7);

            System.out.println("Running DFS with depth limit 6...\n");

            // Solve the cube using BFS
            CubeGraph.Node solution = solver.solve(start);

            // Check result
            if (solution == null) {
                System.out.println("❌ No solution found within depth limit 6.");
                return;
            }

            // Solution found
            System.out.println("✅ Solution found!");
            System.out.println("Moves used (" + solution.path.size() + "):");
            System.out.println(solution.path + "\n");

            System.out.println("========= SOLVED CUBE =========");
            solution.currentState.printNet();

        } catch (Exception e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }
    }
}
