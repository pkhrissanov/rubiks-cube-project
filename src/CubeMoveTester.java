
public class CubeMoveTester {

    public static void main(String[] args) throws Exception {

        Cube original = new Cube("test_solved_cube.txt");

        testMove(original, "U");
        testMove(original, "Uprime");
        testMove(original, "D");
        testMove(original, "Dprime");
        testMove(original, "L");
        testMove(original, "Lprime");
        testMove(original, "R");
        testMove(original, "Rprime");
        testMove(original, "F");
        testMove(original, "Fprime");
        testMove(original, "B");
        testMove(original, "Bprime");
    }

    private static void testMove(Cube original, String move) throws Exception {
        Cube a = cloneCube(original);
        Cube b = cloneCube(original);

        System.out.println("\n================ " + move + " ================");

        // original cube
        System.out.println("ORIGINAL:");
        original.printNet();

        // cube after move
        System.out.println("\nAFTER " + move + ":");
        apply(a, move);
        a.printNet();

        // cube after move + inverse
        System.out.println("\nAFTER " + move + " THEN " + inverse(move) + ":");
        apply(b, move);
        apply(b, inverse(move));
        b.printNet();
    }

    private static Cube cloneCube(Cube c) throws Exception {
        Cube x = new Cube("temp_clone.txt");
        for (int i = 0; i < 54; i++) x.cube.set(i, c.cube.get(i));
        return x;
    }

    private static void apply(Cube c, String m) {
        switch (m) {
            case "U" -> c.U();
            case "Uprime" -> c.Uprime();
            case "D" -> c.D();
            case "Dprime" -> c.Dprime();
            case "L" -> c.L();
            case "Lprime" -> c.Lprime();
            case "R" -> c.R();
            case "Rprime" -> c.Rprime();
            case "F" -> c.F();
            case "Fprime" -> c.Fprime();
            case "B" -> c.B();
            case "Bprime" -> c.Bprime();
        }
    }

    private static String inverse(String m) {
        return switch (m) {
            case "U" -> "Uprime";
            case "Uprime" -> "U";
            case "D" -> "Dprime";
            case "Dprime" -> "D";
            case "L" -> "Lprime";
            case "Lprime" -> "L";
            case "R" -> "Rprime";
            case "Rprime" -> "R";
            case "F" -> "Fprime";
            case "Fprime" -> "F";
            case "B" -> "Bprime";
            case "Bprime" -> "B";
            default -> throw new RuntimeException("bad move");
        };
    }







}
