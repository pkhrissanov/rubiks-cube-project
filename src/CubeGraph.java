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


    private String inverseMove(String move) {
        switch (move){
            case("U"): return ("U'");
            case("U'"): return ("U");
            case("D"): return ("D'");
            case("D'"): return ("D");
            case("L"): return ("L'");
            case("L'"): return ("L");
            case("R"): return ("R'");
            case("R'"): return ("R");
            case("B"): return ("B'");
            case("B'"): return ("B");
            case("F"): return ("F'");
            case("F'"): return ("F");

            default:
                return move;
        }
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
