
import java.io.File;
import java.io.*;
import java.util.*;
import java.lang.invoke.MethodHandles;

public class Solver {


    private int maxDepth;

    public Solver(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public CubeGraph.Node solve(Cube start) {
        CubeGraph.Node root = new CubeGraph.Node(start, null, new ArrayList<>());

        // Iterative deepening loop
        for (int depth = 0; depth <= maxDepth; depth++) {
            CubeGraph.Node result = dfs(root, depth);
            if (result != null) return result;
        }
        // no solution found
        return null;
    }

    private CubeGraph.Node dfs(CubeGraph.Node node, int limit) {

        if (node.currentState.isSolved()) {
            return node;
        }

        CubeGraph graph = new CubeGraph(node);
        graph.expandCurrent();

        if (node.path.size() == limit) {
            PriorityQueue<CubeGraph.Node> queue = new PriorityQueue<>((a, b) -> Integer.compare(b.score, a.score));

            for (CubeGraph.Node child : node.children) {

                if (isCycle(node, child.currentState.toString())) {
                    continue;
                }
                queue.add(child);
            }

            return dfs(queue.poll(), limit);

        }


        for (
                CubeGraph.Node child : node.children) {

            // cycle check: avoid revisiting any ancestor state
            if (isCycle(node, child.currentState.toString())) {
                continue;
            }

            CubeGraph.Node found = dfs(child, limit - 1);
            if (found != null) {
                return found;
            }
        }

        return null;
    }


// heuristic function
    // cube itself -> heuristitic function index to determine howmuch of the cube is solved (layer by latyer)
    // needs to have 3 cases (for checking each layer)
    //each case has different function to be able to determine how close we are to solving the cube


// implement into generation 3 moves into bfs
//make go back logic
//best picker
// layer by layer choice
    //

    private boolean isCycle(CubeGraph.Node node, String newStateString) {
        CubeGraph.Node cur = node;
        while (cur != null) {
            if (cur.currentState.toString().equals(newStateString)) {
                return true;
            }
            // climb using parent path (we infer parent via path)
            cur = getParent(cur);
        }
        return false;
    }

    /**
     * Reconstructs parent node by undoing last move.
     * Minimal-impact helper. Your Node does not store parent,
     * so we infer parent by removing the last move.
     */
    private CubeGraph.Node getParent(CubeGraph.Node node) {
        if (node.path == null || node.path.isEmpty()) return null;

        try {
            ArrayList<String> parentPath = new ArrayList<>(node.path);
            parentPath.remove(parentPath.size() - 1);

            Cube parentCube = node.currentState.clone();
            parentCube.move(inverseMove(node.parentEdge)); // undo last move

            return new CubeGraph.Node(parentCube,
                    (!parentPath.isEmpty() ? parentPath.getLast() : null),
                    parentPath);

        } catch (Exception e) {
            return null;
        }
    }

    private String inverseMove(String move) {
        switch (move) {
            case "U":
                return "U'";
            case "U'":
                return "U";
            case "D":
                return "D'";
            case "D'":
                return "D";
            case "L":
                return "L'";
            case "L'":
                return "L";
            case "R":
                return "R'";
            case "R'":
                return "R";
            case "F":
                return "F'";
            case "F'":
                return "F";
            case "B":
                return "B'";
            case "B'":
                return "B";
        }
        return null;
    }
}

