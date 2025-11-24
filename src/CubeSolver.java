import java.io.*;
import java.util.*;

public class CubeSolver {

    public static void main(String[] args) {
        try {
            // === 1. Load your scramble from file ===
            Cube cube = new Cube("scramble02.txt");

            System.out.println("=== INPUT SCRAMBLE ===");
            cube.printNet();
            System.out.println();

            // === 2. Solve using IDA* ===
            int maxDepth = 9; // you can increase this as needed
            System.out.println("Solving with IDA* up to depth " + maxDepth + "...");
            List<String> solution = IDAStarCube.solve(cube, maxDepth);

            // === 3. Print result ===
            if (solution == null) {
                System.out.println("No solution found within depth " + maxDepth);
                return;
            }

            System.out.println("Solution (" + solution.size() + " moves): " + solution);
            System.out.println();

            // === 4. Verify by applying moves to the cube ===
            Cube verify = cube.clone();
            for (String mv : solution) {
                Cube.applyMoveByName(verify, mv);
            }

            System.out.println("=== STATE AFTER APPLYING SOLUTION ===");
            verify.printNet();

            if (verify.isSolved()) {
                System.out.println("✔ Cube successfully solved!");
            } else {
                System.out.println("✘ Solver returned moves but cube is NOT solved.");
            }
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error:");
            e.printStackTrace();
        }
    }
}
