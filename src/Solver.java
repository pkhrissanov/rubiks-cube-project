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




        // ====== Stage 1: WHITE CROSS =========
        CubeGraph.Node wc = solveWhiteCross(start);
        if(wc!=null) System.out.println("cross complete");;
        if (wc == null) return null;

// build cube after WC
        Cube cubeAfterWC = start.clone();
        for (String mv : wc.path) cubeAfterWC.move(mv);



// ====== Stage 2: F2L =========
        CubeGraph.Node f2l = solveF2L(cubeAfterWC);
        if (f2l == null) System.out.println("f2l failed");;
            if (f2l == null) return null;

// merge WC + F2L moves
        ArrayList<String> f2lFullPath = new ArrayList<>();
        f2lFullPath.addAll(wc.path);
        f2lFullPath.addAll(f2l.path);

// build cube after F2L
        Cube cubeAfterF2L = start.clone();
        for (String mv : f2lFullPath) cubeAfterF2L.move(mv);



// ====== Stage 3: OLL =========
        CubeGraph.Node oll = solveOLL(cubeAfterF2L);
        if (oll == null) return null;

// merge WC + F2L + OLL
        ArrayList<String> ollFullPath = new ArrayList<>(f2lFullPath);
        ollFullPath.addAll(oll.path);

// build cube after OLL
        Cube cubeAfterOLL = start.clone();
        for (String mv : ollFullPath) cubeAfterOLL.move(mv);



// ====== Stage 4: PLL =========
        CubeGraph.Node pll = solvePLL(cubeAfterOLL);
        if (pll == null) return null;

// merge ALL MOVES
        ArrayList<String> finalPath = new ArrayList<>(ollFullPath);
        finalPath.addAll(pll.path);

// return final cube/node
        Cube finalCube = start.clone();
        for (String mv : finalPath) finalCube.move(mv);

        return new CubeGraph.Node(finalCube, null, finalPath);

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


//helpers for stage 2
    public float heuristicF2L(Cube c) {
    int solvedPairs   = countSolvedF2LPairs(c);
    int wrongLayer    = countF2LPiecesWrongLayer(c);
    int misoriented   = countF2LMisorientation(c);

    // weights can be tuned
    return 4.0f * (4 - solvedPairs) + 2.0f * wrongLayer + 1.0f * misoriented;}






    // FL pair
    private static final int[] FL_CORNER = {6, 11, 18};
    private static final int[] FL_EDGE   = {10, 3};

    // FR pair
    private static final int[] FR_CORNER = {8, 20, 27};
    private static final int[] FR_EDGE   = {5, 28};

    // BR pair
    private static final int[] BR_CORNER = {26, 23, 47};
    private static final int[] BR_EDGE   = {50, 34};

    // BL pair
    private static final int[] BL_CORNER = {24, 45, 17};
    private static final int[] BL_EDGE   = {16, 48};

    private static final String SOLVED_STR =
                "OOOOOOOOO" +
                "GGGGGGGGG" +
                "WWWWWWWWW" +
                "BBBBBBBBB" +
                "YYYYYYYYY" +
                "RRRRRRRRR";

    public boolean isPairSolved(Cube c, int[] cornerIdx, int[] edgeIdx) {
        // check corner stickers
        for (int idx : cornerIdx) {
            if (c.cube.get(idx) != SOLVED_STR.charAt(idx)) {
                return false;
            }
        }
        // check edge stickers
        for (int idx : edgeIdx) {
            if (c.cube.get(idx) != SOLVED_STR.charAt(idx)) {
                return false;
            }
        }
        return true;
    }


    public int countSolvedF2LPairs(Cube c) {
        int solved = 0;

        if (isPairSolved(c, FL_CORNER, FL_EDGE)) solved++;
        if (isPairSolved(c, FR_CORNER, FR_EDGE)) solved++;
        if (isPairSolved(c, BR_CORNER, BR_EDGE)) solved++;
        if (isPairSolved(c, BL_CORNER, BL_EDGE)) solved++;

        return solved;
    }


    public int countF2LPiecesWrongLayer(Cube c) {
        int wrong = 0;

        if (!isPairSolved(c, FL_CORNER, FL_EDGE)) wrong++;
        if (!isPairSolved(c, FR_CORNER, FR_EDGE)) wrong++;
        if (!isPairSolved(c, BR_CORNER, BR_EDGE)) wrong++;
        if (!isPairSolved(c, BL_CORNER, BL_EDGE)) wrong++;

        return wrong;
    }


    public int countF2LMisorientation(Cube c) {
        int mis = 0;

        if (!isPairSolved(c, FL_CORNER, FL_EDGE)) mis++;
        if (!isPairSolved(c, FR_CORNER, FR_EDGE)) mis++;
        if (!isPairSolved(c, BR_CORNER, BR_EDGE)) mis++;
        if (!isPairSolved(c, BL_CORNER, BL_EDGE)) mis++;

        return mis;
    }












    // Stage 2
    public CubeGraph.Node solveF2L(Cube start) {

        CubeGraph.Node root = new CubeGraph.Node(start, null, new ArrayList<>());

        PriorityQueue<AlgoNode> pq = new PriorityQueue<>();
        float h0 = heuristicF2L(start);
        pq.add(new AlgoNode(root, 0, h0));

        HashSet<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            AlgoNode cur = pq.poll();
            CubeGraph.Node curNode = cur.node;
            Cube curCube = curNode.currentState;
            String curKey = curCube.toString();

            if (visited.contains(curKey)) continue;
            visited.add(curKey);

            // GOAL CHECK
            if (countSolvedF2LPairs(curCube) == 4) {
                return curNode;
            }

            // Expand children
            CubeGraph graph = new CubeGraph(curCube);
            graph.currentNode = curNode;
            graph.expandCurrent();

            for (CubeGraph.Node child : curNode.children) {
                String childKey = child.currentState.toString();
                if (visited.contains(childKey)) continue;

                float g = cur.g + 1;
                float h = heuristicF2L(child.currentState);

                pq.add(new AlgoNode(child, g, h));
            }
        }

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
