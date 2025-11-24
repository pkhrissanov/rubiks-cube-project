import java.util.ArrayList;
import java.util.List;

public class CubeGraph {

    public static class Node {
        public Cube currentState;
        public Cube previousState;
        public Cube olderState;

        public String lastMove;
        public List<Edge> children;

        public Node(Cube cur, Cube prev, Cube old, String move) {
            this.currentState = cur;
            this.previousState = prev;
            this.olderState = old;
            this.lastMove = move;
            this.children = new ArrayList<>(12);
        }
    }



    public static class Edge {
        public String moveName;
        public Node child;

        public Edge(String move, Node childNode) {
            this.moveName = move;
            this.child = childNode;
        }
    }




    private Node currentNode;

    public CubeGraph(Cube startCube) {
        this.currentNode = new Node(startCube, null, null, null);
    }

    public Node getCurrentNode() {
        return currentNode;
    }


    private boolean isInverseMove(String moveA, String moveB) {
        if (moveA == null || moveB == null) return false;

        // Example: moveA = "U" and moveB = "U'"
        boolean A_isNormal = moveA.length() == 1;
        boolean B_isPrime = moveB.length() == 2;

        boolean A_isPrime = moveA.length() == 2;
        boolean B_isNormal = moveB.length() == 1;

        if (A_isNormal && B_isPrime) {
            return moveA.charAt(0) == moveB.charAt(0);
        }
        if (A_isPrime && B_isNormal) {
            return moveA.charAt(0) == moveB.charAt(0);
        }
        return false;
    }


    public void expandCurrent() {
        currentNode.children.clear();

        List<Cube.Succ> possibleMoves = currentNode.currentState.neighbors();

        for (Cube.Succ neighbor : possibleMoves) {

            String moveName = neighbor.move;

            if (isInverseMove(moveName, currentNode.lastMove)) {
                continue;
            }

            Node child = new Node(neighbor.state, currentNode.currentState, currentNode.previousState, moveName);

            currentNode.children.add(new Edge(moveName, child));
        }
    }


    public void applyMove(Edge e) {
        currentNode = e.child;
    }
}
