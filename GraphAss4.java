package graph;

import java.util.Iterator;
import java.util.ArrayList;

public class Graph {

    int edgeCount;

    private static class Node {
        int nodeID; //Node id.

        private ArrayList<Integer> neighbours;
        
        public Node(int nodeID){
            this.nodeID = nodeID;
            this.neighbours = new ArrayList<>();
        }

        public void addNeighbour(int neighbourID) {
            if (!neighbours.contains(neighbourID)) {
                neighbours.add(neighbourID);
            }
            return;
        }

        public void removeNeighbour(int neighbourID) {
            if (neighbours.contains(neighbourID)) {
                Integer toRemove = neighbourID;
                neighbours.remove(toRemove); //remove method is ambiguous in ArrayList
            }
            return;
        }

        public boolean hasNeighbour (int neighbourID){
            return(neighbours.contains(neighbourID));
        }

        public int sizeNeighbours(){
            return neighbours.size();
        }
    }

    private ArrayList<Node> nodes;

    /**
     * creates an empty graph on n nodes
     * the "names" of the vertices are 0,1,..,n-1
     * @param n - number of vertices in the graph
     */
    public Graph(int n) {
        // TODO implement me
        nodes = new ArrayList<>(n);
        for (int i = 0; i < n; i++){
            nodes.add(new Node(i));
        }
        return;
    }

    /**
     * adds the edge (i,j) to the graph
     * no effect if i and j were already adjacent
     *
     * @param i, j - vertices in the graph
     */
    public void addEdge(int i, int j) {
        // TODO implement me
        if (!areAdjacent(i, j)){
            nodes.get(i).addNeighbour(j);
            nodes.get(j).addNeighbour(i);
            edgeCount++;
        }
        return;
    }

    /**
     * removes the edge (i,j) from the graph
     * no effect if i and j were not adjacent
     *
     * @param i, j - vertices in the graph
     */
    public void removeEdge(int i, int j) {
        // TODO implement me
        if (areAdjacent(i, j)){
            nodes.get(i).removeNeighbour(j);
            nodes.get(j).removeNeighbour(i);
            edgeCount--;
        }
        return;
    }

    /**
     * @param i, j - vertices in the graph
     * @return true if (i,j) is an edge in the graph, and false otherwise
     */
    public boolean areAdjacent(int i, int j) {
        // TODO implement me
        if (nodes.get(i).hasNeighbour(j)){ //only have to check one side since adding/removing get both sides.
            return true;
        }
        return false;
    }

    /**
     * @param i - a vertex in the graph
     * @return the degree of i
     */
    public int degree(int i) {
        // TODO implement me
        return nodes.get(i).sizeNeighbours();
    }

    /**
     * The iterator must output the neighbors of i in the increasing order
     * Assumption: the graph is not modified during the use of the iterator
     *
     * @param i - a vertex in the graph
     * @return an iterator that returns the neighbors of i
     */
    public Iterator<Integer> neighboursIterator(int i) {
        // TODO implement me
        ArrayList<Integer> sortedNeighbours = new ArrayList<>(nodes.get(i).neighbours);
        sortedNeighbours.sort(Integer::compareTo);

        return sortedNeighbours.iterator();
    }

    /**
     * @return number of vertices in the graph
     */
    public int numberOfVertices() {
        // TODO implement me
        return nodes.size();
    }

    /**
     * @return number of edges in the graph
     */
    public int numberOfEdges() {
        // TODO implement me
        return edgeCount;
    }

    /**
     * @param i, j - vertices in the graph
     * @return distance between the vertices
     */
    public int distance(int i, int j) {
        // TODO implement me
        //Implement a BFS
        if (i == j){
            return 0;
        }

        boolean[] visited = new boolean[nodes.size()];
        int[] dist = new int[nodes.size()];
        ArrayList<Integer> queue = new ArrayList<>();

        visited[i] = true;
        dist[i] = 0;
        queue.add(i);

        while (!queue.isEmpty()) {
            int current = queue.remove(0);

            for (int neighbor : nodes.get(current).neighbours) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    dist[neighbor] = dist[current] + 1;
                    queue.add(neighbor);

                    if (neighbor == j) {
                        return dist[neighbor];
                    }
                }
            }
        }

        return -1;
    }

    /**
     * @param n - number of vertices
     * @param p - number between 0 and 1
     * @return a random graph on n vertices, where each edge is added to the graph with probability p
     */
    public static Graph generateRandomGraph(int n, double p) {
        // TODO implement me
        Graph graph = new Graph(n);
        java.util.Random rand = new java.util.Random();
        
        for (int i = 0; i<n; i++){
            for(int j = i+1; j<n; j++){
                if(rand.nextDouble() < p){
                    graph.addEdge(i, j);
                }
            }
        }
        return graph;
    }

}
