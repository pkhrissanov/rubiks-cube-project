import java.io.*;
import java.util.*;

public class CubeTester {

    // Convert internal cube → expected net layout for comparison
    private static String cubeToNet(Cube c) {
        ArrayList<Character> x = c.cube;

        StringBuilder sb = new StringBuilder();

        // ----------------------------
        // U face: rows 0–2, cols 3–5
        // ----------------------------
        for (int r = 0; r < 3; r++) {
            sb.append("   "); // cols 0–2 empty
            for (int c0 = 0; c0 < 3; c0++)
                sb.append(x.get(r * 3 + c0));   // U = 0..8
            sb.append("\n");
        }

        for (int r = 0; r < 3; r++) {
            int baseL = 9 + r * 3;
            int baseF = 18 + r * 3;
            int baseR = 27 + r * 3;
            int baseB = 36 + r * 3;

            for (int i = 0; i < 3; i++) sb.append(x.get(baseL + i));
            for (int i = 0; i < 3; i++) sb.append(x.get(baseF + i));
            for (int i = 0; i < 3; i++) sb.append(x.get(baseR + i));
            for (int i = 0; i < 3; i++) sb.append(x.get(baseB + i));

            sb.append("\n");
        }

        for (int r = 0; r < 3; r++) {
            sb.append("   ");
            int baseD = 45 + r * 3;
            for (int i = 0; i < 3; i++)
                sb.append(x.get(baseD + i));
            sb.append("\n");
        }

        return sb.toString();
    }


    public static void main(String[] args) {
        String[] files = {
                "testCases/scramble01.txt",
                "testCases/scramble02.txt",
                "testCases/scramble03.txt",
                "testCases/scramble04.txt",
                "testCases/scramble05.txt",
                "testCases/scramble06.txt",
                "testCases/scramble07.txt",
                "testCases/scramble08.txt",
                "testCases/scramble09.txt",
                "testCases/scramble10.txt"
        };

        for (String file : files) {
            System.out.println("Testing: " + file);

            try {
                // 1. Load cube
                Cube c = new Cube(file);
                System.out.println("✔ SUCCESS: Cube loaded");

                // 2. Convert cube back into net format
                String reconstructed = cubeToNet(c);

                // 3. Load original file as raw text (normalized)
                String original = readFileAsNet(file);

                // 4. Compare
                if (original.equals(reconstructed)) {
                    System.out.println("✔ MATCH: Parsed cube matches original layout");
                } else {
                    System.out.println("❌ MISMATCH!");
                    System.out.println("Original:");
                    System.out.println(original);

                    System.out.println("Reconstructed:");
                    System.out.println(reconstructed);
                }

            } catch (Exception e) {
                System.out.println("❌ ERROR: " + e.getClass().getSimpleName());
                System.out.println("Message: " + e.getMessage());
            }

            System.out.println("-----------------------------------------");
        }
    }

    // Helper: read file into same unified 9-line net format
    private static String readFileAsNet(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
