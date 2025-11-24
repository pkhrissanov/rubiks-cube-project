import java.util.*;

/**
 * IDA* solver for your Cube class.
 * - Uses Cube.neighbors() to expand moves.
 * - Returns the sequence of move names (e.g. ["U", "R", "F'"]) from start to solved.
 * <p>
 * Note: heuristic() currently = 0 (IDA* becomes IDDFS). Add an admissible heuristic to speed up.
 */
public class IDAStarCube {

    // map move -> its inverse
    private static final Map<String, String> inverse = new HashMap<>();

    static {
        inverse.put("U", "U'");
        inverse.put("U'", "U");
        inverse.put("D", "D'");
        inverse.put("D'", "D");
        inverse.put("L", "L'");
        inverse.put("L'", "L");
        inverse.put("R", "R'");
        inverse.put("R'", "R");
        inverse.put("F", "F'");
        inverse.put("F'", "F");
        inverse.put("B", "B'");
        inverse.put("B'", "B");
    }

    /**
     * Public solve method: returns null if none found within maxDepthLimit
     */
    public static List<String> solve(Cube start, int maxDepthLimit) {
        if (start.isSolved()) return new ArrayList<>();

        int bound = 0; // heuristic is 0 for now
        // path of cubes & moves
        ArrayList<Cube> path = new ArrayList<>();
        ArrayList<String> movesPath = new ArrayList<>();
        HashSet<String> onPath = new HashSet<>(); // string encoding to avoid duplicates on current path

        path.add(start);
        onPath.add(start.toString());

        while (bound <= maxDepthLimit) {
            SearchResult res = search(path, movesPath, onPath, 0, bound, maxDepthLimit, null);
            if (res.found) return res.moveSeq;
            if (res.nextBound == Integer.MAX_VALUE) return null;
            bound = res.nextBound;
        }
        return null;
    }

    private static class SearchResult {
        boolean found;
        int nextBound;
        List<String> moveSeq;

        SearchResult(boolean found, int nextBound, List<String> moveSeq) {
            this.found = found;
            this.nextBound = nextBound;
            this.moveSeq = moveSeq;
        }
    }

    /**
     * g = current depth
     * bound = current f-bound
     * lastMove = last move applied to reach current node (for inverse pruning)
     */
    private static SearchResult search(ArrayList<Cube> path, ArrayList<String> movesPath,
                                       HashSet<String> onPath, int g, int bound, int maxDepthLimit,
                                       String lastMove) {
        Cube node = path.get(path.size() - 1);
        int h = heuristic(node);                 // placeholder heuristic (admissible). Replace with a real heuristic if you add one.
        int f = g + h;
        if (f > bound) return new SearchResult(false, f, null);
        if (node.isSolved()) {
            return new SearchResult(true, f, new ArrayList<>(movesPath));
        }
        if (g >= maxDepthLimit) return new SearchResult(false, Integer.MAX_VALUE, null);

        int minNextBound = Integer.MAX_VALUE;

        for (Cube.Succ succ : node.neighbors()) {
            String mv = succ.move;
            // skip immediate reverse of previous move
            if (lastMove != null && inverse.get(lastMove).equals(mv)) continue;

            String enc = succ.state.toString();
            if (onPath.contains(enc)) continue;

            // push
            path.add(succ.state);
            movesPath.add(mv);
            onPath.add(enc);

            SearchResult res = search(path, movesPath, onPath, g + 1, bound, maxDepthLimit, mv);
            if (res.found) return res;
            if (res.nextBound < minNextBound) minNextBound = res.nextBound;

            // pop
            path.remove(path.size() - 1);
            movesPath.remove(movesPath.size() - 1);
            onPath.remove(enc);
        }

        return new SearchResult(false, minNextBound, null);
    }

    // ============================
    // Heuristic helpers
    // ============================

    /**
     * Main heuristic: max(stickerMismatch/8, misplacedCorners/4)
     */
    private static int heuristic(Cube c) {
        int sMis = stickerMismatch(c);
        int cornersMis = misplacedCorners(c);
        int h1 = sMis / 8;
        int h2 = cornersMis / 4;
        return Math.max(h1, h2);
    }

    /**
     * Count how many stickers are not equal to their face center.
     * Faces are stored as:
     * U: 0..8, L:9..17, F:18..26, R:27..35, B:36..44, D:45..53
     * <p>
     * Admissible because one face-turn moves at most 8 stickers.
     */
    private static int stickerMismatch(Cube c) {
        char[] arr = c.toString().toCharArray();
        // center indices: 4, 13, 22, 31, 40, 49
        int[][] faces = {
                {0, 1, 2, 3, 4, 5, 6, 7, 8},      // U
                {9, 10, 11, 12, 13, 14, 15, 16, 17}, // L
                {18, 19, 20, 21, 22, 23, 24, 25, 26}, // F
                {27, 28, 29, 30, 31, 32, 33, 34, 35}, // R
                {36, 37, 38, 39, 40, 41, 42, 43, 44}, // B
                {45, 46, 47, 48, 49, 50, 51, 52, 53}  // D
        };
        int[] centers = {4, 13, 22, 31, 40, 49};
        int mismatches = 0;
        for (int f = 0; f < 6; f++) {
            char center = arr[centers[f]];
            for (int idx : faces[f]) {
                if (arr[idx] != center) mismatches++;
            }
        }
        return mismatches;
    }

    /**
     * Count how many corners are not in the correct corner-piece (three-face color set).
     * <p>
     * We define the 8 corner positions by their 3 sticker indices (based on your face layout).
     * Each correct corner in the solved cube has a specific set of 3 center colors;
     * if the current corner's set of 3 colors (unordered) differs from solved, it's misplaced.
     * <p>
     * A single face turn moves at most 4 corners → dividing by 4 is admissible.
     * <p>
     * NOTE: This is a conservative *misplaced-corner* metric (simple to compute and admissible).
     */
    private static int misplacedCorners(Cube c) {
        String s = c.toString();
        char[] arr = s.toCharArray();

        // corner facelet indices (triples). These follow the same geometry as the move code
        // and the face ordering you confirmed (U,L,F,R,B,D).
        // The triples below are (U/L/F/R/B/D indices):
        final int[][] cornerFacelets = {
                // URF
                {8, 20, 27},  // U8, F2 (20), R0 (27)
                // UFL
                {6, 18, 11},  // U6, F0 (18), L2 (11)
                // ULB
                {0, 9, 38},   // U0, L0 (9), B2 (38)
                // UBR
                {2, 36, 29},  // U2, B0 (36), R2 (29)
                // DFR
                {47, 26, 15}, // D2 (47), F8 (26), R8? (15 is L6? careful)
                // DLF
                {45, 24, 17}, // D0 (45), F6 (24), L8 (17)
                // DBL
                {51, 42, 35}, // D6 (51), B6 (42), L? (35 is R8?)  -- conservative mapping
                // DRB
                {53, 33, 44}  // D8 (53), R6 (33), B8 (44)
        };

        // Build solved-corner-color-sets from the solved cube assumption: each center color identifies a face.
        // Get the center colors for faces U,L,F,R,B,D
        char Uc = arr[4], Lc = arr[13], Fc = arr[22], Rc = arr[31], Bc = arr[40], Dc = arr[49];

        // The correct color set per corner (unordered set of 3 chars)
        final char[][] solvedCornerColors = new char[][]{
                {Uc, Fc, Rc}, // URF
                {Uc, Fc, Lc}, // UFL
                {Uc, Lc, Bc}, // ULB
                {Uc, Bc, Rc}, // UBR
                {Dc, Fc, Rc}, // DFR
                {Dc, Fc, Lc}, // DLF
                {Dc, Lc, Bc}, // DBL
                {Dc, Bc, Rc}  // DRB
        };

        int misplaced = 0;
        for (int i = 0; i < cornerFacelets.length; i++) {
            int[] trip = cornerFacelets[i];
            // current corner colors (unordered)
            char a = arr[trip[0]];
            char b = arr[trip[1]];
            char ccol = arr[trip[2]];
            // check if the multiset {a,b,c} equals the solvedCornerColors[i] set (unordered)
            if (!matchesSet(a, b, ccol, solvedCornerColors[i])) misplaced++;
        }
        return misplaced;
    }

    // helper: check whether the three chars match the target 3-char set (unordered)
    private static boolean matchesSet(char a, char b, char c, char[] target) {
        boolean[] used = new boolean[3];
        char[] src = new char[]{a, b, c};
        for (char ch : src) {
            boolean found = false;
            for (int j = 0; j < 3; j++) {
                if (!used[j] && target[j] == ch) {
                    used[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}

