
import java.io.File;
import java.io.*;
import java.util.*;
import java.lang.invoke.MethodHandles;

public class Solver {
    private static final String[] MOVES = {
        "U", "U'",
        "D", "D'",
        "L", "L'",
        "R", "R'",
        "F", "F'",
        "B", "B'",
    };

    private HashSet<String> visited = new HashSet<>();
    private int maxDepth;

    public Solver(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public List<String> solve(Cube start) {
        visited.clear();
        List<String> path = new ArrayList<>();
        if (bfs(start, 0, path)) {
            return path;
        }
        return null; //no solution within depth limit
    }

    private boolean bfs(Cube cube, int depth, List<String> path) {
        //If solved, we’re done
        if (cube.isSolved()) { //also would be check if white cross
            return true;
        }

        if (depth == maxDepth) {
            return false;
        }

        String key = cube.toString();

        // Already seen this exact cube state → skip
        if (visited.contains(key)) {
            return false;
        }
        visited.add(key);

        // Try all moves
        for (String m : MOVES) {
            if(path[-1] == inverseMove(m)){

            }

            Cube next = cube.clone();
            next.move(m);

            path.add(m);

            if (dfs(next, depth + 1, path)) {
                return true;
            }

            path.remove(path.size() - 1);
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println("number of arguments: " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        if (args.length < 2) {
            System.out.println("File names are not specified");
            System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
            return;
        }
    }
}
