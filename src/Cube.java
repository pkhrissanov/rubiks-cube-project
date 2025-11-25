import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cube implements Cloneable {

    public ArrayList<Character> cube;  // final clean cube: U,L,F,R,B,D contiguous

    // --- existing file-reading constructor left unchanged ---
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

    /** Copy constructor for creating clones */
    public Cube(ArrayList<Character> arr) {
        this.cube = new ArrayList<>(arr.size());
        this.cube.addAll(arr);
    }

    /** Helper to deep-clone */
    @Override
    public Cube clone() {
        return new Cube(new ArrayList<>(this.cube));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(54);
        for (char ch : cube) sb.append(ch);
        return sb.toString();
    }

    // determine if cube is solved: each face's 9 stickers equal to face center

    // --- your swap/rotateFaceCW and move methods (unchanged) ---
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

    public void U() {
        rotateFaceCW(0);
        char l0 = cube.get(9), l1 = cube.get(10), l2 = cube.get(11);

        cube.set(9, cube.get(18));
        cube.set(10, cube.get(19));
        cube.set(11, cube.get(20));
        cube.set(18, cube.get(27));
        cube.set(19, cube.get(28));
        cube.set(20, cube.get(29));
        cube.set(27, cube.get(36));
        cube.set(28, cube.get(37));
        cube.set(29, cube.get(38));

        cube.set(36, l0);
        cube.set(37, l1);
        cube.set(38, l2);
    }

    public void Uprime() {
        U(); U(); U();
    }

    public void D() {
        rotateFaceCW(45);
        char l6 = cube.get(15), l7 = cube.get(16), l8 = cube.get(17);
        char temp1 = cube.get(24), temp2 = cube.get(25), temp3 = cube.get(26);

        cube.set(15, cube.get(42));
        cube.set(16, cube.get(43));
        cube.set(17, cube.get(44));
        cube.set(24, cube.get(33));
        cube.set(25, cube.get(34));
        cube.set(26, cube.get(35));

        cube.set(33, temp1);
        cube.set(34, temp2);
        cube.set(35, temp3);

        cube.set(42, l6);
        cube.set(43, l7);
        cube.set(44, l8);
    }

    public void Dprime() {
        D(); D(); D();
    }

    public void L() {
        rotateFaceCW(9);

        char u0 = cube.get(0), u3 = cube.get(3), u6 = cube.get(6);

        cube.set(0, cube.get(38));
        cube.set(3, cube.get(41));
        cube.set(6, cube.get(44));
        cube.set(38, cube.get(45));
        cube.set(41, cube.get(48));
        cube.set(44, cube.get(51));
        cube.set(45, cube.get(18));
        cube.set(48, cube.get(21));
        cube.set(51, cube.get(24));

        cube.set(18, u0);
        cube.set(21, u3);
        cube.set(24, u6);
    }

    public void Lprime() {
        L(); L(); L();
    }

    public void R() {
        rotateFaceCW(27);

        char u2 = cube.get(2), u5 = cube.get(5), u8 = cube.get(8);

        cube.set(2, cube.get(20));
        cube.set(5, cube.get(23));
        cube.set(8, cube.get(26));
        cube.set(20, cube.get(47));
        cube.set(23, cube.get(50));
        cube.set(26, cube.get(53));
        cube.set(47, cube.get(36));
        cube.set(50, cube.get(39));
        cube.set(53, cube.get(42));

        cube.set(36, u2);
        cube.set(39, u5);
        cube.set(42, u8);
    }

    public void Rprime() {
        R(); R(); R();
    }

    public void F() {
        rotateFaceCW(18);

        char u6 = cube.get(6), u7 = cube.get(7), u8 = cube.get(8);

        cube.set(6, cube.get(11));
        cube.set(7, cube.get(14));
        cube.set(8, cube.get(17));
        cube.set(11, cube.get(45));
        cube.set(14, cube.get(46));
        cube.set(17, cube.get(47));
        cube.set(45, cube.get(27));
        cube.set(46, cube.get(30));
        cube.set(47, cube.get(33));

        cube.set(27, u6);
        cube.set(30, u7);
        cube.set(33, u8);
    }

    public void Fprime() {
        F(); F(); F();
    }

    public void B() {
        rotateFaceCW(36);

        char u0 = cube.get(0), u1 = cube.get(1), u2 = cube.get(2);
        char temp0 = cube.get(9), temp1 = cube.get(12), temp2 = cube.get(15);

        cube.set(0, cube.get(29));
        cube.set(1, cube.get(32));
        cube.set(2, cube.get(35));
        cube.set(9, u0);
        cube.set(12, u1);
        cube.set(15, u2);
        cube.set(29, cube.get(51));
        cube.set(32, cube.get(52));
        cube.set(35, cube.get(53));

        cube.set(51, temp0);
        cube.set(52, temp1);
        cube.set(53, temp2);
    }

    public void Bprime() {
        B(); B(); B();
    }

    // Printing unchanged
    public void printNet() {
        String s = this.toString();

        System.out.println("   " + s.substring(0, 3) + "   ");
        System.out.println("   " + s.substring(3, 6) + "   ");
        System.out.println("   " + s.substring(6, 9) + "   ");

        for (int row = 0; row < 3; row++) {
            int L = 9  + row * 3;
            int F = 18 + row * 3;
            int R = 27 + row * 3;
            int B = 36 + row * 3;

            System.out.println(
                    s.substring(L, L+3) +
                    s.substring(F, F+3) +
                    s.substring(R, R+3) +
                    s.substring(B, B+3)
            );
        }

        System.out.println("   " + s.substring(45, 48) + "   ");
        System.out.println("   " + s.substring(48, 51) + "   ");
        System.out.println("   " + s.substring(51, 54) + "   ");
    }

    // --- new helper: successor structure for neighbors() ---
    public static class Succ {
        public final Cube state;
        public final String move;
        public Succ(Cube s, String m) { state = s; move = m; }
    }

    /** Return all successors (one move away) as (state, moveName) pairs.
     *  We include the 12 face moves: U, U', D, D', L, L', R, R', F, F', B, B'
     *
     *  Optional pruning (like skipping inverse of lastMove) is implemented in the solver.
     */
    public List<Succ> neighbors() {
        List<Succ> out = new ArrayList<>(12);
        String[] moves = {"U","U'","D","D'","L","L'","R","R'","F","F'","B","B'"};
        for (String m : moves) {
            Cube c = this.clone();
            applyMoveByName(c, m);
            out.add(new Succ(c, m));
        }
        return out;
    }

    /** Apply named move to a cube instance */
    public static void applyMoveByName(Cube c, String move) {
        switch (move) {
            case "U":  c.U(); break;
            case "U'": c.Uprime(); break;
            case "D":  c.D(); break;
            case "D'": c.Dprime(); break;
            case "L":  c.L(); break;
            case "L'": c.Lprime(); break;
            case "R":  c.R(); break;
            case "R'": c.Rprime(); break;
            case "F":  c.F(); break;
            case "F'": c.Fprime(); break;
            case "B":  c.B(); break;
            case "B'": c.Bprime(); break;
            default: throw new IllegalArgumentException("Unknown move: " + move);
        }
    }

    // --- equality & hash based on the string encoding of the cube ---
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

}
