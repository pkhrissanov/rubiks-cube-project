
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
        return bfs(root, maxDepth);
    }

    private CubeGraph.Node bfs(CubeGraph.Node root, int depthLimit) {

        // queue of nodes to explore
        Queue<CubeGraph.Node> queue = new LinkedList<>();

        // queue of depths for each node
        Queue<Integer> depthQueue = new LinkedList<>();

        queue.add(root);
        depthQueue.add(0);

        while (!queue.isEmpty()) {

            CubeGraph.Node node = queue.poll();
            int depth = depthQueue.poll();

            // solved?
            if (node.currentState.isSolved()) {
                return node;
            }

            // cannot go deeper than limit
            if (depth == depthLimit) {
                continue;
            }

            // expand node
            CubeGraph graph = new CubeGraph(node.currentState);
            graph.currentNode = node;
            graph.expandCurrent();

            // enqueue children
            for (CubeGraph.Node child : node.children) {
                queue.add(child);
                depthQueue.add(depth + 1);
            }
        }

        return null; // no solution found
    }
}
