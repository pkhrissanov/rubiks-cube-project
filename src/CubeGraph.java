import java.util.ArrayList;

public class CubeGraph {
    private static final String[] MOVES = {"U", "U'", "D", "D'", "L", "L'", "R", "R'", "F", "F'", "B", "B'"};

    public static class Node {
        public Cube currentState;
        public String parentEdge;
        public ArrayList<String> path;
        public ArrayList<Node> children;


        public Node(Cube cur, String parentEdge, ArrayList<String> path) {
            this.currentState = cur;
            this.parentEdge = parentEdge;
            this.path = path;
            this.children = new ArrayList<>(12);
        }
    }


    Node currentNode;

    public CubeGraph(Cube startCube) {
        this.currentNode = new Node(startCube, null, null);
    }
    public CubeGraph() {
        this.currentNode = new Node(null, null, null);
    }

    public Node getCurrentNode() {
        return currentNode;
    }


    private String inverseMove(String move) {
        switch (move) {
            case ("U"):
                return ("U'");
            case ("U'"):
                return ("U");
            case ("D"):
                return ("D'");
            case ("D'"):
                return ("D");
            case ("L"):
                return ("L'");
            case ("L'"):
                return ("L");
            case ("R"):
                return ("R'");
            case ("R'"):
                return ("R");
            case ("B"):
                return ("B'");
            case ("B'"):
                return ("B");
            case ("F"):
                return ("F'");
            case ("F'"):
                return ("F");

            default:
                return move;
        }
    }


    public void expandCurrent() {

        currentNode.children.clear();

        for (String mv : MOVES) {

            if (currentNode.parentEdge != null &&
                    mv.equals(inverseMove(currentNode.parentEdge))) {
                continue;
            }
            
            //Prune triple R R R or R' R R'
            if (currentNode.path != null && currentNode.path.size() >= 2) {
            String last = currentNode.parentEdge;
            String secondLast = currentNode.path.get(currentNode.path.size() - 2);

                if (last != null &&
                    last.charAt(0) == mv.charAt(0) &&
                    secondLast.charAt(0) == mv.charAt(0)) {       
                    continue;
                }
            }

            Cube nextCube = currentNode.currentState.clone();
            nextCube.move(mv);
            nextCube.parentEdge = mv;
            ArrayList<String> newPath = new ArrayList<>();
            if (currentNode.path != null) newPath.addAll(currentNode.path);
            newPath.add(mv);
            Node child = new Node(nextCube, mv, newPath);
            currentNode.children.add(child);
        }
    }
}
