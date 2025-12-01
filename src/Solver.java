import java.util.*;

public class Solver {

    private int maxDepth;
    private final Map<String, String> OLL_ALGORITHMS;
    private final Map<String, String> PLL_ALGORITHMS;
    
    // Define StageGoalChecker instances as fields for reliable comparison
    private final StageGoalChecker IS_CROSS_SOLVED = this::isCrossSolved;
    private final StageGoalChecker IS_F2L_SOLVED = this::isF2LSolved;
    private final StageGoalChecker IS_OLL_SOLVED = this::isOLLSolved;

    public Solver(int maxDepth) {
        this.maxDepth = maxDepth;
        
        // --- Simplified Lookup Tables for Demonstration ---
        this.OLL_ALGORITHMS = new HashMap<>();
        // Example OLL Algorithm (Sune Case: solves a single solved corner case)
        OLL_ALGORITHMS.put("SUNE", "R U R' U R U2 R'");

        this.PLL_ALGORITHMS = new HashMap<>();
        // Example PLL Algorithm (T-Permutation)
        PLL_ALGORITHMS.put("T_PERM", "R U R' U' R' F R2 U' R' U' R U R' F'");
    }

    /**
     * Attempts to apply a direct algorithm sequence from a lookup map to solve a stage.
     */
    private CubeGraph.Node stageApplyAlgorithm(Cube start, StageGoalChecker goalChecker, Map<String, String> algMap, String stageName, ArrayList<String> previousPath) {
        
        if (algMap.isEmpty()) return null;

        for (Map.Entry<String, String> entry : algMap.entrySet()) {
            String algorithm = entry.getValue();
            String patternName = entry.getKey();
            
            System.out.println("Attempting Algorithm: " + patternName + " (" + algorithm + ")");

            Cube currentCube = start.clone();
            ArrayList<String> algorithmPath = new ArrayList<>();
            String[] moves = algorithm.split(" ");
            
            for (String move : moves) {
                currentCube.move(move);
                algorithmPath.add(move);
            }

            // After applying the algorithm, check if the goal is met AND F2L is preserved.
            if (goalChecker.check(currentCube) && currentCube.isF2LPreserved()) {
                System.out.println(stageName + " solved via Algorithm: " + patternName);
                
                // The full path is the previous stage path + the new algorithm path
                ArrayList<String> fullPath = new ArrayList<>(previousPath);
                fullPath.addAll(algorithmPath);
                
                return new CubeGraph.Node(currentCube, algorithmPath.get(algorithmPath.size() - 1), fullPath);
            }
        }

        return null; // Algorithm didn't solve the state
    }


    /**
     * Solves the cube using a four-stage CFOP-like approach.
     */
    public CubeGraph.Node solve(Cube start) {
        // Path accumulator for all stages
        ArrayList<String> fullPath = new ArrayList<>();
        Cube nextStageStart = start;
        CubeGraph.Node finalResult = null;
        
        // --- Stage 1: Cross (Search based) ---
        System.out.println("Starting Stage 1: Cross...");
        CubeGraph.Node stage1Result = stageSearch(nextStageStart, IS_CROSS_SOLVED, maxDepth);
        if (stage1Result == null) return null;
        nextStageStart = stage1Result.currentState;
        fullPath.addAll(stage1Result.path);
        finalResult = new CubeGraph.Node(nextStageStart, stage1Result.parentEdge, fullPath);
        System.out.println("--- Stage 1 (Cross) Solved in " + stage1Result.path.size() + " moves ---");
        
        // --- Stage 2: F2L (Search based) ---
        System.out.println("\nStarting Stage 2: F2L...");
        CubeGraph.Node stage2Result = stageSearch(nextStageStart, IS_F2L_SOLVED, maxDepth + 5);
        if (stage2Result == null) { System.out.println("F2L failed. Stopping."); return finalResult; }
        nextStageStart = stage2Result.currentState;
        fullPath.addAll(stage2Result.path);
        finalResult = new CubeGraph.Node(nextStageStart, stage2Result.parentEdge, fullPath);
        System.out.println("--- Stage 2 (F2L) Solved in " + stage2Result.path.size() + " relative moves ---");
        
        // --- Stage 3: OLL (Algorithm lookup/application based) ---
        System.out.println("\nStarting Stage 3: OLL...");
        CubeGraph.Node stage3Result = stageApplyAlgorithm(nextStageStart, IS_OLL_SOLVED, OLL_ALGORITHMS, "OLL", fullPath);

        if (stage3Result != null) {
            nextStageStart = stage3Result.currentState;
            fullPath = stage3Result.path; // Path is already merged in stageApplyAlgorithm
            finalResult = new CubeGraph.Node(nextStageStart, stage3Result.parentEdge, fullPath);
        } else {
             System.out.println("OLL failed to solve using hardcoded algorithms. Proceeding to PLL attempt.");
        }


        // --- Stage 4: PLL (Algorithm lookup/application based) ---
        System.out.println("\nStarting Stage 4: PLL...");
        CubeGraph.Node stage4Result = stageApplyAlgorithm(nextStageStart, Cube::isSolved, PLL_ALGORITHMS, "PLL", fullPath);

        if (stage4Result != null) {
            nextStageStart = stage4Result.currentState;
            fullPath = stage4Result.path; // Path is already merged in stageApplyAlgorithm
            finalResult = new CubeGraph.Node(nextStageStart, stage4Result.parentEdge, fullPath);
        } else {
            System.out.println("PLL failed to solve using hardcoded algorithms.");
        }


        // --- Final Node Construction and Reporting ---
        System.out.println("\n--- Solver Finished ---");
        System.out.println("Total Path Length: " + finalResult.path.size() + " moves.");
        System.out.println("Total Path: " + String.join(" ", finalResult.path));

        return finalResult;
    }
    
    
    private boolean isCrossSolved(Cube cube) {
        return cube.isCrossSolved();
    }

    private boolean isF2LSolved(Cube cube) {
        return cube.isF2LSolved();
    }
    
    private boolean isOLLSolved(Cube cube) {
        return cube.isOLLSolved();
    }
    

    private CubeGraph.Node stageSearch(Cube start, StageGoalChecker goalChecker, int limit) {
        Queue<CubeGraph.Node> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        CubeGraph.Node root = new CubeGraph.Node(start, null, new ArrayList<>()); 
        queue.add(root);
        visited.add(start.toString());

        while (!queue.isEmpty()) {
            CubeGraph.Node node = queue.poll();
            
            if (goalChecker.check(node.currentState)) {
                return node;
            }
            
            if (node.path.size() >= limit) { 
                continue;
            }

            CubeGraph graph = new CubeGraph(node.currentState);
            graph.currentNode = node;
            graph.expandCurrent();

            for (CubeGraph.Node child : node.children) {
                if (!visited.contains(child.currentState.toString())) {
                    
                    // Corrected F2L Preservation Check: 
                    // If we are searching for a goal other than Cross (i.e., F2L or later),
                    // prune moves that break the solved F2L layers (including the Cross).
                    if (goalChecker != IS_CROSS_SOLVED) {
                        if (!child.currentState.isF2LPreserved()) {
                            continue;
                        }
                    }

                    visited.add(child.currentState.toString());
                    queue.add(child);
                }
            }
        }

        return null;
    }

    private interface StageGoalChecker {
        boolean check(Cube cube);
    }
    
    private CubeGraph.Node getParent(CubeGraph.Node node) {
        if (node.path.isEmpty()) return null;

        try {
            ArrayList<String> parentPath = new ArrayList<>(node.path);
            String lastMove = parentPath.remove(parentPath.size() - 1);

            Cube parentCube = node.currentState.clone();
            parentCube.move(inverseMove(lastMove));

            return new CubeGraph.Node(parentCube,
                                      (parentPath.isEmpty() ? null : parentPath.get(parentPath.size()-1)),
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