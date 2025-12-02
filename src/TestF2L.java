import java.io.File;

public class TestF2L {

    public static void main(String[] args) {

        try {
            System.out.println("==== F2L Heuristic Test ====\n");

            // --------------------------
            // 1. Load solved cube
            // --------------------------
            Cube solved = new Cube("src/test_solved_cube.txt");
            Solver solver = new Solver(7);  // depth doesn't matter here

            System.out.println(">>> Loaded solved cube:");
            solved.printNet();
            System.out.println();

            // check solved F2L status
            int solvedPairs = solver.countSolvedF2LPairs(solved);
            float heuristic = solver.heuristicF2L(solved);

            System.out.println("Solved cube:");
            System.out.println("countSolvedF2LPairs = " + solvedPairs);
            System.out.println("heuristicF2L = " + heuristic);

            if (solvedPairs == 4 && heuristic == 0.0f) {
                System.out.println("✔ PASS: Solved cube correctly gives 4 solved pairs & heuristic 0.\n");
            } else {
                System.out.println("❌ FAIL: Solved cube mapping is incorrect.\n");
            }


            // --------------------------
            // 2. Load scrambled cube
            // --------------------------
            Cube scrambled = new Cube("testCases/scramble01.txt");

            System.out.println(">>> Loaded scrambled cube:");
            scrambled.printNet();
            System.out.println();

            int scrPairs = solver.countSolvedF2LPairs(scrambled);
            float scrH = solver.heuristicF2L(scrambled);

            System.out.println("Scrambled cube test:");
            System.out.println("countSolvedF2LPairs = " + scrPairs);
            System.out.println("heuristicF2L = " + scrH);

            if (scrPairs >= 0 && scrPairs <= 4) {
                System.out.println("✔ PASS: Scrambled cube has valid pair count range.\n");
            } else {
                System.out.println("❌ FAIL: Scrambled cube pair count is invalid.\n");
            }


            // --------------------------
            // 3. Artificial test: cube after white cross
            // --------------------------


        } catch (Exception e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }

    }

}
