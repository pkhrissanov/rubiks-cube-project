import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MoveTester {

    public static void main(String[] args) {
        try {
            Cube cube = new Cube("scramble03.txt");

            System.out.println("========= START CUBE =========");
            cube.printNet();
            System.out.println();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("Enter moves (or 'quit'): ");
                String line = br.readLine().trim();

                if (line.equalsIgnoreCase("quit"))
                    break;
                if (line.isEmpty())
                    continue;

                String[] moves = parseMoves(line);
                System.out.println("\nApplying: " + String.join(" ", moves) + "\n");

                for (String m : moves) {
                    System.out.println("Move: " + m);
                    cube.move(m);  // your real move code
                    cube.printNet();
                    System.out.println();
                }
            }

            System.out.println("========= FINAL CUBE =========");
            cube.printNet();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse moves that consist ONLY of:
     *   U, U'
     *   R, R'
     *   L, L'
     *   F, F'
     *   B, B'
     *   D, D'
     *
     * Examples:
     *   URUURR -> U R U U R R
     *   U'R'L' -> U' R' L'
     */
    private static String[] parseMoves(String s) {
        s = s.replaceAll("\\s+", ""); // remove all whitespace

        java.util.List<String> moves = new java.util.ArrayList<>();

        for (int i = 0; i < s.length();) {
            char c = s.charAt(i);

            // Must be a valid face letter
            if ("ULFRBD".indexOf(c) == -1) {
                i++;
                continue;
            }

            String move = "" + c;

            // Check for optional prime (')
            if (i + 1 < s.length() && s.charAt(i + 1) == '\'') {
                move = move + "'";
                i += 2;
            } else {
                i += 1;
            }

            moves.add(move);
        }

        return moves.toArray(new String[0]);
    }
}
