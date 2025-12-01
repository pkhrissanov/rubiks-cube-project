
import java.io.File;
import java.io.*;
import java.util.*;
import java.lang.invoke.MethodHandles;

public class Solver{


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

        return null;  // no solution found
    }

    private CubeGraph.Node dfs(CubeGraph.Node node, int limit) {

             // success?
        if (node.currentState.isSolved()) {
            return node;
        }

        // cannot go deeper
        if (limit == 0) {
            return null;
        }

        // expand children
        CubeGraph graph = new CubeGraph(node.currentState);
        graph.currentNode = node;
        graph.expandCurrent();

        // explore children in DFS order
        for (CubeGraph.Node child : node.children) {

            CubeGraph.Node found = dfs(child, limit - 1);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

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





    private CubeGraph.Node getParent(CubeGraph.Node node) {
        if (node.path == null || node.path.size() == 0) return null;

        try {
            ArrayList<String> parentPath = new ArrayList<>(node.path);
            parentPath.remove(parentPath.size() - 1);

            Cube parentCube = node.currentState.clone();
            parentCube.move(inverseMove(node.parentEdge)); // undo last move

            return new CubeGraph.Node(parentCube,
                                      (parentPath.size() > 0 ? parentPath.get(parentPath.size()-1) : null),
                                      parentPath);

        } catch (Exception e) {
            return null;
        }
    }

    private String inverseMove(String move) {
        switch (move) {
            case "U": return "U'";
            case "U'": return "U";
            case "D": return "D'";
            case "D'": return "D";
            case "L": return "L'";
            case "L'": return "L";
            case "R": return "R'";
            case "R'": return "R";
            case "F": return "F'";
            case "F'": return "F";
            case "B": return "B'";
            case "B'": return "B";
        }
        return null;
    }




















}




/*
solver
    1. do bfs of depth 7 to find any quick solutions, if no solution, move to next solver

    2. Human method solver
            going from intial cube - use the white cross heuristic to figure out where to go

            once hit white cross - switch heuristic to f2l, solve until f2l
            after f2l - switch heuristic again - OLL solve
            PLL heuristic and solve









 */