import java.util.*;

public class HeuristicDB {

    private static final String[] MOVES = {
            "U","U'","D","D'",
            "L","L'","R","R'",
            "F","F'","B","B'"
    };

    // Map inverse moves to prune useless backtracking
    private static final HashMap<String,String> INVERSE = new HashMap<>();
    static {
        INVERSE.put("U",  "U'");
        INVERSE.put("U'", "U");
        INVERSE.put("D",  "D'");
        INVERSE.put("D'", "D");
        INVERSE.put("L",  "L'");
        INVERSE.put("L'", "L");
        INVERSE.put("R",  "R'");
        INVERSE.put("R'", "R");
        INVERSE.put("F",  "F'");
        INVERSE.put("F'", "F");
        INVERSE.put("B",  "B'");
        INVERSE.put("B'", "B");
    }

    // ----------------------------------------------------------
    // Build a pattern database using BFS from the solved cube
    // ----------------------------------------------------------
    public static HashMap<String, Integer> build(int maxDepth) {

        HashMap<String, Integer> dist = new HashMap<>(1_000_000);
        ArrayDeque<Node> queue = new ArrayDeque<>();

        Cube solvedCube = makeSolvedCube();
        String solvedState = solvedCube.toString();

        dist.put(solvedState, 0);
        queue.add(new Node(solvedCube, solvedState, null)); // lastMove = null

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            int depth = dist.get(cur.stateString);

            if (depth == maxDepth)
                continue;

            for (String move : MOVES) {

                // prune inverse moves: do NOT undo last move
                if (cur.lastMove != null && INVERSE.get(move).equals(cur.lastMove))
                    continue;

                Cube nextCube = cur.cube.clone(); // make a fast clone
                nextCube.move(move);

                String nextState = nextCube.toString();

                if (!dist.containsKey(nextState)) {
                    dist.put(nextState, depth + 1);
                    queue.add(new Node(nextCube, nextState, move));
                }
            }
        }

        return dist;
    }

    // A tiny helper structure to store state, cube, last move
    private static class Node {
        Cube cube;
        String stateString;
        String lastMove;

        Node(Cube cube, String s, String lastMove) {
            this.cube = cube;
            this.stateString = s;
            this.lastMove = lastMove;
        }
    }

    // ----------------------------------------------------------
    // Construct solved cube
    // ----------------------------------------------------------
    private static Cube makeSolvedCube() {
        Cube c = new Cube();
        String solved =
                "OOOOOOOOO" +  // U
                        "GGGGGGGGG" +  // L
                        "WWWWWWWWW" +  // F
                        "BBBBBBBBB" +  // R
                        "YYYYYYYYY" +  // B
                        "RRRRRRRRR";   // D

        for (int i = 0; i < 54; i++)
            c.cube.set(i, solved.charAt(i));

        return c;
    }
}
