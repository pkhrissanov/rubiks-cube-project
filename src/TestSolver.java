public class TestSolver {

    public static void main(String[] args) {
        try {

            Cube start = new Cube("testCases/scramble37.txt");

            System.out.println("========= START CUBE =========");
            start.printNet();
            System.out.println();

            Solver solver = new Solver(7);

            System.out.println("======== Running solveHuman() ========\n");
            long startTime = System.currentTimeMillis();



            CubeGraph.Node solution = solver.solveHuman(start);

            if (solution == null) {
                System.out.println("❌ Solver failed.");
                return;
            }

            System.out.println("\n========== SOLVER RESULT ==========");

            // Check if final cube has the white cross
            System.out.println("Checking whether White Cross was solved...");
            Cube replay = start.clone();

            for (int i = 0; i < solution.path.size(); i++) {
                replay.move(solution.path.get(i));

                if (solver.isWhiteCrossSolved(replay)) {
                    System.out.println("\n⚪ White Cross achieved at move #" + (i + 1));
                    System.out.println("Moves so far: " + solution.path.subList(0, i + 1));
                    replay.printNet();
                    break;
                }
            }

            System.out.println("\n===== FINAL STATE =====");
            solution.currentState.printNet();
            long endTime = System.currentTimeMillis();
            System.out.println("That took " + (endTime - startTime) + " milliseconds");

        } catch (Exception e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }
    }
}
