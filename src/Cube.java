import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Cube implements Cloneable {

    public ArrayList<Character> cube;
    public String parentEdge;
    public float hscore;
    public int stage;

    // Center colors: U, L, F, R, B, D (indices 4, 13, 22, 31, 40, 49)
    public char[] getCenterColors() {
        return new char[]{
                cube.get(4),  // U (index 4)
                cube.get(13), // L (index 13)
                cube.get(22), // F (index 22)
                cube.get(31), // R (index 31)
                cube.get(40), // B (index 40)
                cube.get(49)  // D (index 49)
        };
    }

    public Cube(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists() || !file.canRead()) {
            throw new IOException("Cannot read: " + file.getAbsolutePath());
        }

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() < 12)
                    line = String.format("%-12s", line);
                lines.add(line);
            }
        }

        cube = new ArrayList<>(54);
        for (int i = 0; i < 54; i++) cube.add('X');
        int idx = 0;
        for (int r = 0; r < 3; r++)
            for (int c = 3; c < 6; c++)
                cube.set(idx++, lines.get(r).charAt(c));
        for (int r = 3; r < 6; r++)
            for (int c = 0; c < 3; c++)
                cube.set(idx++, lines.get(r).charAt(c));
        for (int r = 3; r < 6; r++)
            for (int c = 3; c < 6; c++)
                cube.set(idx++, lines.get(r).charAt(c));
        for (int r = 3; r < 6; r++)
            for (int c = 6; c < 9; c++)
                cube.set(idx++, lines.get(r).charAt(c));
        for (int r = 3; r < 6; r++)
            for (int c = 9; c < 12; c++)
                cube.set(idx++, lines.get(r).charAt(c));
        for (int r = 6; r < 9; r++)
            for (int c = 3; c < 6; c++)
                cube.set(idx++, lines.get(r).charAt(c));
    }

    public Cube(ArrayList<Character> arr) {
        this.cube = new ArrayList<>(arr.size());
        this.cube.addAll(arr);
    }

    public Cube() {
        cube = new ArrayList<>(54);
        for (int i = 0; i < 54; i++) cube.add('a');
    }

    public float getScore() {
        return 0;
    }

    @Override
    public Cube clone() {
        Cube cloned = new Cube(new ArrayList<>(this.cube));
        cloned.parentEdge = this.parentEdge;
        cloned.hscore = this.hscore;
        cloned.stage = this.stage;
        return cloned;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(54);
        for (char ch : cube) sb.append(ch);
        return sb.toString();
    }

    private void swap(int a, int b) {
        char t = cube.get(a);
        cube.set(a, cube.get(b));
        cube.set(b, t);
    }


    private void rotateFaceCW(int base) {
        swap(base + 0, base + 6);
        swap(base + 6, base + 8);
        swap(base + 8, base + 2);
        swap(base + 1, base + 3);
        swap(base + 3, base + 7);
        swap(base + 7, base + 5);
    }

    public ArrayList<Character> move(String m) {
        switch (m) {
            // --- Single Moves (U, U', D, D', L, L', R, R', F, F', B, B') ---
            case "U":
                rotateFaceCW(0);
                char u0 = cube.get(9), u1 = cube.get(10), u2 = cube.get(11);

                cube.set(9, cube.get(18));
                cube.set(10, cube.get(19));
                cube.set(11, cube.get(20));
                cube.set(18, cube.get(27));
                cube.set(19, cube.get(28));
                cube.set(20, cube.get(29));
                cube.set(27, cube.get(36));
                cube.set(28, cube.get(37));
                cube.set(29, cube.get(38));

                cube.set(36, u0);
                cube.set(37, u1);
                cube.set(38, u2);
                break;

            case "U'":
                move("U");
                move("U");
                move("U");
                break;

            case "D":
                rotateFaceCW(45);
                char d0 = cube.get(15), d1 = cube.get(16), d2 = cube.get(17);
                char dt0 = cube.get(24), dt1 = cube.get(25), dt2 = cube.get(26);

                cube.set(15, cube.get(42));
                cube.set(16, cube.get(43));
                cube.set(17, cube.get(44));
                cube.set(24, cube.get(33));
                cube.set(25, cube.get(34));
                cube.set(26, cube.get(35));

                cube.set(33, dt0);
                cube.set(34, dt1);
                cube.set(35, dt2);

                cube.set(42, d0);
                cube.set(43, d1);
                cube.set(44, d2);
                break;

            case "D'":
                move("D");
                move("D");
                move("D");
                break;

            case "L":
                rotateFaceCW(9);

                char lu0 = cube.get(0), lu3 = cube.get(3), lu6 = cube.get(6);

                cube.set(0, cube.get(44));
                cube.set(3, cube.get(41));
                cube.set(6, cube.get(38));
                cube.set(38, cube.get(51));
                cube.set(41, cube.get(48));
                cube.set(44, cube.get(45));
                cube.set(45, cube.get(18));
                cube.set(48, cube.get(21));
                cube.set(51, cube.get(24));

                cube.set(18, lu0);
                cube.set(21, lu3);
                cube.set(24, lu6);
                break;

            case "L'":
                move("L");
                move("L");
                move("L");
                break;

            case "R":
                rotateFaceCW(27);

                char ru2 = cube.get(2), ru5 = cube.get(5), ru8 = cube.get(8);

                cube.set(2, cube.get(20));
                cube.set(5, cube.get(23));
                cube.set(8, cube.get(26));
                cube.set(20, cube.get(47));
                cube.set(23, cube.get(50));
                cube.set(26, cube.get(53));
                cube.set(47, cube.get(42));
                cube.set(50, cube.get(39));
                cube.set(53, cube.get(36));

                cube.set(42, ru2);
                cube.set(39, ru5);
                cube.set(36, ru8);
                break;

            case "R'":
                move("R");
                move("R");
                move("R");
                break;

            case "F":
                rotateFaceCW(18);

                char fu6 = cube.get(6), fu7 = cube.get(7), fu8 = cube.get(8);

                cube.set(6, cube.get(11));
                cube.set(7, cube.get(14));
                cube.set(8, cube.get(17));
                cube.set(11, cube.get(45));
                cube.set(14, cube.get(46));
                cube.set(17, cube.get(47));
                cube.set(45, cube.get(27));
                cube.set(46, cube.get(30));
                cube.set(47, cube.get(33));

                cube.set(27, fu6);
                cube.set(30, fu7);
                cube.set(33, fu8);
                break;

            case "F'":
                move("F");
                move("F");
                move("F");
                break;

            case "B":
                rotateFaceCW(36);

                char bu0 = cube.get(0), bu1 = cube.get(1), bu2 = cube.get(2);
                char bt0 = cube.get(9), bt1 = cube.get(12), bt2 = cube.get(15);

                cube.set(0, cube.get(29));
                cube.set(1, cube.get(32));
                cube.set(2, cube.get(35));
                cube.set(9, bu2);
                cube.set(12, bu1);
                cube.set(15, bu0);
                cube.set(29, cube.get(53));
                cube.set(32, cube.get(52));
                cube.set(35, cube.get(51));

                cube.set(51, bt0);
                cube.set(52, bt1);
                cube.set(53, bt2);
                break;

            case "B'":
                move("B");
                move("B");
                move("B");
                break;
            
            // --- NEW: Double Moves (X2) ---
            case "U2":
                move("U");
                move("U");
                break;
            case "D2":
                move("D");
                move("D");
                break;
            case "L2":
                move("L");
                move("L");
                break;
            case "R2":
                move("R");
                move("R");
                break;
            case "F2":
                move("F");
                move("F");
                break;
            case "B2":
                move("B");
                move("B");
                break;

            default:
                throw new IllegalArgumentException("Invalid move: " + m);
        }
        return this.cube;
    }

    public void printNet() {
        String s = this.toString();

        System.out.println("   " + s.substring(0, 3) + "   ");
        System.out.println("   " + s.substring(3, 6) + "   ");
        System.out.println("   " + s.substring(6, 9) + "   ");

        for (int row = 0; row < 3; row++) {
            int L = 9 + row * 3;
            int F = 18 + row * 3;
            int R = 27 + row * 3;
            int B = 36 + row * 3;

            System.out.println(
                    s.substring(L, L + 3) +
                            s.substring(F, F + 3) +
                            s.substring(R, R + 3) +
                            s.substring(B, B + 3)
            );
        }

        System.out.println("   " + s.substring(45, 48) + "   ");
        System.out.println("   " + s.substring(48, 51) + "   ");
        System.out.println("   " + s.substring(51, 54) + "   ");
    }

    public boolean isSolved() {
        char[] centers = getCenterColors();
        int[][] faceStickerIndices = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8},      // U face
            {9, 10, 11, 12, 13, 14, 15, 16, 17}, // L face
            {18, 19, 20, 21, 22, 23, 24, 25, 26}, // F face
            {27, 28, 29, 30, 31, 32, 33, 34, 35}, // R face
            {36, 37, 38, 39, 40, 41, 42, 43, 44}, // B face
            {45, 46, 47, 48, 49, 50, 51, 52, 53}  // D face
        };

        for (int i = 0; i < 6; i++) {
            char centerColor = centers[i];
            for (int index : faceStickerIndices[i]) {
                if (cube.get(index) != centerColor) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCrossSolved() {
        char D_CENTER = cube.get(49);
        char F_CENTER = cube.get(22);
        char R_CENTER = cube.get(31);
        char B_CENTER = cube.get(40);
        char L_CENTER = cube.get(13);

        int[] dFaceIndices = {46, 50, 52, 48}; // D face stickers of the cross
        int[] adjFaceIndices = {25, 34, 43, 16}; // Adjacent stickers
        char[] adjCenters = {F_CENTER, R_CENTER, B_CENTER, L_CENTER};

        for (int i = 0; i < 4; i++) {
            if (cube.get(dFaceIndices[i]) != D_CENTER) return false;
            if (cube.get(adjFaceIndices[i]) != adjCenters[i]) return false;
        }
        return true;
    }

    public boolean isF2LSolved() {
        if (!isCrossSolved()) return false;

        int[] f2lStickers = {
                21, 23, 30, 32, 39, 41, 12, 14, 
                24, 26, 33, 35, 42, 44, 15, 17, 
                45, 47, 51, 53 
        };

        for (int index : f2lStickers) {
            int faceIndex = index / 9; 
            char targetCenter = getCenterColors()[faceIndex];
            if (cube.get(index) != targetCenter) {
                return false;
            }
        }

        return true;
    }

    public boolean isOLLSolved() {
        char U_CENTER = cube.get(4);
        
        for (int i = 0; i < 9; i++) {
            if (cube.get(i) != U_CENTER) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isF2LPreserved() {
        char[] centers = getCenterColors();
        int[] f2lStickers = {
                9, 10, 11, 12, 13, 14, 15, 16, 17, 
                18, 19, 20, 21, 22, 23, 24, 25, 26, 
                27, 28, 29, 30, 31, 32, 33, 34, 35, 
                36, 37, 38, 39, 40, 41, 42, 43, 44, 
                45, 46, 47, 48, 49, 50, 51, 52, 53  
        };

        for (int index : f2lStickers) {
            int faceIndex = index / 9; 
            char targetCenter = centers[faceIndex];
            if (cube.get(index) != targetCenter) {
                return false;
            }
        }
        return true;
    }
}