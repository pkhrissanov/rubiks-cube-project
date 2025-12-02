import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Solver {



    private int maxDepth;

    public Solver(int maxDepth) {
        this.maxDepth = maxDepth;
    }




    // -------------------------
    // New HUMAN-METHOD SOLVER   THIS IS THE ACTUAL SOLVER FUNCTION
    // -------------------------
    public CubeGraph.Node solveHuman(Cube start) {

        // Stage 0: quick BFS

        CubeGraph.Node quick = quickBFS(start, 6);
        if(quick == null) System.out.println("bfs failed");;
        if (quick != null) return quick;




        // Stage 1: White Cross
        CubeGraph.Node wc = solveWhiteCross(start);
        if(wc != null) return wc;
        if (wc == null) return null;

        // Stage 2: F2L
        CubeGraph.Node f2l = solveF2L(wc.currentState);
        if (f2l == null) return null;

        // Stage 3: OLL
        CubeGraph.Node oll = solveOLL(f2l.currentState);
        if (oll == null) return null;

        // Stage 4: PLL
        CubeGraph.Node pll = solvePLL(oll.currentState);
        return pll;
    }


   // THIS IS THE BFS QUICK SEARCH

    private CubeGraph.Node quickBFS(Cube start, int depthLimit) {

        CubeGraph.Node root = new CubeGraph.Node(start, null, new ArrayList<>());

        for (int depth = 0; depth <= depthLimit; depth++) {
            CubeGraph.Node result = dfs(root, depth);
            if (result != null) {
                return result;
            }
        }

        return null;
    }



    private static class AlgoNode implements Comparable<AlgoNode> {
        CubeGraph.Node node;
        float g;  // path cost
        float h;  // heuristic
        float f;  // g + h

        AlgoNode(CubeGraph.Node n, float g, float h) {
            this.node = n;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        @Override
        public int compareTo(AlgoNode other) {
            return Float.compare(this.f, other.f);
        }
    }


    // Stage 1
    private CubeGraph.Node solveWhiteCross(Cube start) {

        PriorityQueue<AlgoNode> open = new PriorityQueue<>();
        HashSet<String> closed = new HashSet<>();

        CubeGraph.Node root = new CubeGraph.Node(start, null, new ArrayList<>());
        float h = heuristicWhiteCross(start);

        open.add(new AlgoNode(root, 0, h));  // g = 0


        while (!open.isEmpty()) {
            AlgoNode current = open.poll();
            CubeGraph.Node node = current.node;

            String stateKey = node.currentState.toString();
            if (closed.contains(stateKey))
                continue;

            closed.add(stateKey);


            if (isWhiteCrossSolved(node.currentState)) {
                return node;
            }


            CubeGraph graph = new CubeGraph(node.currentState);
            graph.currentNode = node;
            graph.expandCurrent();

            for (CubeGraph.Node child : node.children) {
                String childKey = child.currentState.toString();
                if (closed.contains(childKey))
                    continue;

                float g = child.path.size();          // cost so far
                float hChild = heuristicWhiteCross(child.currentState);
                open.add(new AlgoNode(child, g, hChild));
            }
        }

        return null;
    }


    //All helpers for stage 1

    public boolean isWhiteCrossSolved(Cube c) {
        return countCrossPositionsWrong(c) == 0 && countCrossMisaligned(c) == 0;
    }


    private float heuristicWhiteCross(Cube c) {
        return 3f * countCrossPositionsWrong(c) + 2f * countCrossMisaligned(c) + 1f * crossPenaltyCases(c);
    }


    //how many white stickers siblings are not alligned with the center color
    private int countCrossMisaligned(Cube c) {
        int mis = 0;

        // upward edge
        if (c.cube.get(19) == 'W' && c.cube.get(1) != c.cube.get(4)) mis++;

        // left edge
        if (c.cube.get(21) == 'W' && c.cube.get(12) != c.cube.get(13)) mis++;

        // right edge
        if (c.cube.get(23) == 'W' && c.cube.get(32) != c.cube.get(31)) mis++;

        // down edge
        if (c.cube.get(25) == 'W' && c.cube.get(46) != c.cube.get(49)) mis++;

        return mis;
    }


    //how many white edges are not in the correct position
    private int countCrossPositionsWrong(Cube c) {
        int wrong = 0;
        int[] target = {19, 21, 23, 25};

        for (int idx : target) {
            if (c.cube.get(idx) != 'W') wrong++;
        }

        return wrong;
    }

    private static final int[][] WHITE_EDGE_PAIRS = {{1, 19}, {12, 21}, {23, 30}, {25, 46}};

    //how many white edges are in a super shit place
    private int crossPenaltyCases(Cube c) {
        int penalty = 0;

        HashSet<Integer> goal = new HashSet<>(Arrays.asList(19,21,23,25));

        for (int[] e : WHITE_EDGE_PAIRS) {
            int a = e[0], b = e[1];
            char A = c.cube.get(a), B = c.cube.get(b);

            if (A != 'W' && B != 'W')
                continue;

            // already placed correctly → no penalty
            if (goal.contains(a) || goal.contains(b))
                continue;

            // white on U or D (should be on F)
            if ((a >= 0 && a <= 8) || (b >= 0 && b <= 8) ||
                    (a >= 45 && a <= 53) || (b >= 45 && b <= 53))
            {
                penalty += 1;
                continue;
            }

            // middle layer (should not be)
            if ((a >= 9 && a <= 44) || (b >= 9 && b <= 44))
                penalty += 2;
        }

        return penalty;
    }













    // Stage 2
    private CubeGraph.Node solveF2L(Cube afterCross) {
        return null;
    }

    // Stage 3
    private CubeGraph.Node solveOLL(Cube afterF2L) {
        return null;
    }

    // Stage 4
    private CubeGraph.Node solvePLL(Cube afterOLL) {
        return null;
    }




    //helper function for stage 0
    private CubeGraph.Node dfs(CubeGraph.Node node, int limit) {
        if (node.currentState.isSolved()) return node;
        if (limit == 0) return null;

        CubeGraph graph = new CubeGraph(node.currentState);
        graph.currentNode = node;
        graph.expandCurrent();

        for (CubeGraph.Node child : node.children) {
            CubeGraph.Node found = dfs(child, limit - 1);
            if (found != null) return found;
        }
        return null;
    }


    //idk if we really need this guy
    private boolean isCycle(CubeGraph.Node node, String state) {
        CubeGraph.Node cur = node;
        while (cur != null) {
            if (cur.currentState.toString().equals(state)) return true;
            cur = getParent(cur);
        }
        return false;
    }

    private CubeGraph.Node getParent(CubeGraph.Node node) {
        if (node.path == null || node.path.size() == 0) return null;
        try {
            ArrayList<String> parentPath = new ArrayList<>(node.path);
            parentPath.remove(parentPath.size() - 1);

            Cube parentCube = node.currentState.clone();
            parentCube.move(inverseMove(node.parentEdge));

            return new CubeGraph.Node(parentCube,
                    parentPath.size() > 0 ? parentPath.get(parentPath.size()-1) : null,
                    parentPath);

        } catch (Exception e) {
            return null;
        }
    }

    private String inverseMove(String move) {
        switch (move) {
            case "U":  return "U'";
            case "U'": return "U";
            case "D":  return "D'";
            case "D'": return "D";
            case "L":  return "L'";
            case "L'": return "L";
            case "R":  return "R'";
            case "R'": return "R";
            case "F":  return "F'";
            case "F'": return "F";
            case "B":  return "B'";
            case "B'": return "B";
        }
        return null;
    }


}
