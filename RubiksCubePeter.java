import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class RubiksCube {
    public char cube[];

    public RubiksCube() {
        cube = new char[54];
        for (int i = 0; i < 54; i++) {
            if (i <= 8) {
                cube[i] = 'O';
            }
            if ((9 <= i && i <= 11) || (21 <= i && i <= 23) || (33 <= i && i <= 35)) {
                cube[i] = 'G';
            }
            if ((12 <= i && i <= 14) || (24 <= i && i <= 26) || (36 <= i && i <= 38)) {
                cube[i] = 'W';
            }
            if ((15 <= i && i <= 17) || (27 <= i && i <= 29) || (39 <= i && i <= 41)) {
                cube[i] = 'B';
            }
            if ((18 <= i && i <= 20) || (30 <= i && i <= 32) || (42 <= i && i <= 44)) {
                cube[i] = 'Y';
            }
            if ((45 <= i && i <= 47) || (48 <= i && i <= 50) || (51 <= i && i <= 53)) {
                cube[i] = 'R';
            }
        }
    }

    public givenCube(String fileName) throws IOException, IncorrectFormatException {
        File file = new File(fileName);

        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IOException("Cannot read file: " + file.getAbsolutePath());
        }

        final String allowed = "OGWBYR";
        StringBuilder letters = new StringBuilder(54);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int ch;
            while ((ch = br.read()) != -1) {
                char letter = (char) ch;
                if (Character.isWhitespace(letter)) continue;
                letter = Character.toUpperCase(letter);

                if (allowed.indexOf(letter) >= 0) {
                    if (letters.length() == 54) {
                        throw new IncorrectFormatException(
                                "File has more than 54 non-whitespace color letters.");
                    }
                    letters.append(letter);
                } else {
                    throw new IncorrectFormatException(
                            "Invalid character '" + letter + "'. Allowed: " + allowed + " (whitespace is ignored).");
                }
            }
        }

        if (letters.length() != 54) {
            throw new IncorrectFormatException(
                    "Expected exactly 54 non-whitespace color letters, found " + letters.length() + "."
            );
        }
        cube = new char[54];
        for (int i = 0; i < 54; i++) {
            cube[i] = letters.charAt(i);
        }
    }

    public void applyMoves(String moves) {
        for (int i = 0; i < moves.length(); i++) {
            char m = moves.charAt(i);
            switch (m) {
                case 'U':
                    turnU();
                    break;
                case 'R':
                    turnR();
                    break;
                case 'L':
                    turnL();
                    break;
                case 'F':
                    turnF();
                    break;
                case 'B':
                    turnB();
                    break;
                case 'D':
                    turnD();
                    break;
                default:
            }
        }
    }

    private void rotateFaceCW(int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        char a0 = cube[i0], a1 = cube[i1], a2 = cube[i2], a3 = cube[i3], a5 = cube[i5], a6 = cube[i6], a7 = cube[i7], a8 = cube[i8];
        cube[i0] = a6;
        cube[i1] = a3;
        cube[i2] = a0;
        cube[i3] = a7;
        cube[i5] = a1;
        cube[i6] = a8;
        cube[i7] = a5;
        cube[i8] = a2;
    }

    private void turnU() {
        rotateFaceCW(0, 1, 2, 3, 4, 5, 6, 7, 8);

        char l0 = cube[9], l1 = cube[10], l2 = cube[11];
        char f0 = cube[12], f1 = cube[13], f2 = cube[14];
        char r0 = cube[15], r1 = cube[16], r2 = cube[17];
        char b0 = cube[18], b1 = cube[19], b2 = cube[20];

        cube[12] = r0;
        cube[13] = r1;
        cube[14] = r2;
        cube[15] = b0;
        cube[16] = b1;
        cube[17] = b2;
        cube[18] = l0;
        cube[19] = l1;
        cube[20] = l2;
        cube[9] = f0;
        cube[10] = f1;
        cube[11] = f2;
    }

    private void turnD() {
        rotateFaceCW(45, 46, 47, 48, 49, 50, 51, 52, 53);

        char f0 = cube[36], f1 = cube[37], f2 = cube[38];
        char r0 = cube[39], r1 = cube[40], r2 = cube[41];
        char b0 = cube[42], b1 = cube[43], b2 = cube[44];
        char l0 = cube[33], l1 = cube[34], l2 = cube[35];

        cube[39] = f0;
        cube[40] = f1;
        cube[41] = f2;
        cube[42] = r0;
        cube[43] = r1;
        cube[44] = r2;
        cube[33] = b0;
        cube[34] = b1;
        cube[35] = b2;
        cube[36] = l0;
        cube[37] = l1;
        cube[38] = l2;
    }

    private void turnL() {
        rotateFaceCW(9, 10, 11, 21, 22, 23, 33, 34, 35);

        char u0 = cube[0], u1 = cube[3], u2 = cube[6];
        char f0 = cube[12], f1 = cube[24], f2 = cube[36];
        char d0 = cube[45], d1 = cube[48], d2 = cube[51];
        char br0 = cube[20], br1 = cube[32], br2 = cube[44];

        cube[12] = u0;
        cube[24] = u1;
        cube[36] = u2;
        cube[45] = f0;
        cube[48] = f1;
        cube[51] = f2;
        cube[20] = d2;
        cube[32] = d1;
        cube[44] = d0;
        cube[0] = br2;
        cube[3] = br1;
        cube[6] = br0;
    }

    private void turnR() {
        rotateFaceCW(15, 16, 17, 27, 28, 29, 39, 40, 41);

        char u0 = cube[2], u1 = cube[5], u2 = cube[8];
        char bl0 = cube[18], bl1 = cube[30], bl2 = cube[42];
        char d0 = cube[47], d1 = cube[50], d2 = cube[53];
        char f0 = cube[14], f1 = cube[26], f2 = cube[38];

        cube[18] = u2;
        cube[30] = u1;
        cube[42] = u0;
        cube[47] = bl2;
        cube[50] = bl1;
        cube[53] = bl0;
        cube[14] = d0;
        cube[26] = d1;
        cube[38] = d2;
        cube[2] = f0;
        cube[5] = f1;
        cube[8] = f2;
    }

    private void turnF() {
        rotateFaceCW(12, 13, 14, 24, 25, 26, 36, 37, 38);

        char u0 = cube[6], u1 = cube[7], u2 = cube[8];
        char rl0 = cube[15], rl1 = cube[27], rl2 = cube[39];
        char d0 = cube[47], d1 = cube[46], d2 = cube[45];
        char lr0 = cube[35], lr1 = cube[23], lr2 = cube[11];

        cube[15] = u0;
        cube[27] = u1;
        cube[39] = u2;
        cube[47] = rl0;
        cube[46] = rl1;
        cube[45] = rl2;
        cube[35] = d0;
        cube[23] = d1;
        cube[11] = d2;
        cube[6] = lr0;
        cube[7] = lr1;
        cube[8] = lr2;
    }

    private void turnB() {
        rotateFaceCW(18, 19, 20, 30, 31, 32, 42, 43, 44);

        char u0 = cube[0], u1 = cube[1], u2 = cube[2];
        char ll0 = cube[33], ll1 = cube[21], ll2 = cube[9];
        char d0 = cube[51], d1 = cube[52], d2 = cube[53];
        char rr0 = cube[17], rr1 = cube[29], rr2 = cube[41];

        cube[33] = u2;
        cube[21] = u1;
        cube[9] = u0;
        cube[51] = ll0;
        cube[52] = ll1;
        cube[53] = ll2;
        cube[17] = d0;
        cube[29] = d1;
        cube[41] = d2;
        cube[0] = rr0;
        cube[1] = rr1;
        cube[2] = rr2;
    }

    public boolean isSolved() {
        return this.toString().equals(
                "   OOO\n" +
                        "   OOO\n" +
                        "   OOO\n" +
                        "GGGWWWBBBYYY\n" +
                        "GGGWWWBBBYYY\n" +
                        "GGGWWWBBBYYY\n" +
                        "   RRR\n" +
                        "   RRR\n" +
                        "   RRR\n"
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 3; r++) {
            sb.append("   ");
            for (int c = 0; c < 3; c++) {
                sb.append(cube[r * 3 + c]);
            }
            sb.append('\n');
        }

        for (int r = 0; r < 3; r++) {
            int base = 9 + r * 12;
            for (int c = 0; c < 12; c++) {
                sb.append(cube[base + c]);
            }
            sb.append('\n');
        }

        for (int r = 0; r < 3; r++) {
            sb.append("   ");
            int base = 45 + r * 3;
            for (int c = 0; c < 3; c++) {
                sb.append(cube[base + c]);
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    public static int order(String moves) {
        if (moves == null || moves.isEmpty()) return 1;

        final int MAX_ITERS = 10000;

        RubiksCube cur = new RubiksCube();
        String solvedState = cur.toString();

        for (int k = 1; k <= MAX_ITERS; k++) {
            cur.applyMoves(moves);
            if (cur.toString().equals(solvedState)) {
                return k;
            }
        }
        return -1;
    }
}
