
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class HeuristicDB {

    // Build a BFS-based heuristic DB from the solved cube
    // maxDepth is how far from solved we explore
    public static HashMap<String, Integer> build(int maxDepth) {

        HashMap<String, Integer> dist = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();

        // Construct solved cube
        Cube solved = makeSolvedCube();
        String solvedState = solved.toString();

        dist.put(solvedState, 0);
        queue.add(solvedState);

        String[] moves = {"U", "D", "L", "R", "F", "B"};

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            int d = dist.get(cur);
            if (d == maxDepth) continue;

            for (String m : moves) {
                Cube c = cubeFromState(cur);
                c.move(m);
                String ns = c.toString();

                if (!dist.containsKey(ns)) {
                    dist.put(ns, d + 1);
                    queue.add(ns);
                }
            }
        }

        return dist;
    }

    // Create a solved Cube using your 1D representation
    private static Cube makeSolvedCube() {
        Cube c = new Cube();
        String solved =
                "OOOOOOOOO" +  // U
                        "GGGGGGGGG" +  // L
                        "WWWWWWWWW" +  // F
                        "BBBBBBBBB" +  // R
                        "YYYYYYYYY" +  // B
                        "RRRRRRRRR";   // D

        for (int i = 0; i < 54; i++) {
            c.cube.set(i, solved.charAt(i));
        }
        return c;
    }

    // Rebuild a Cube object from a 54-char state string
    private static Cube cubeFromState(String s) {
        Cube c = new Cube();
        for (int i = 0; i < 54; i++) {
            c.cube.set(i, s.charAt(i));
        }
        return c;
    }
}
