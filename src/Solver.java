import java.util.*;

public class Solver {

    private int maxDepth;
    private final Map<String, String> OLL_ALGORITHMS;
    private final Map<String, String> PLL_ALGORITHMS;
    
    // Define StageGoalChecker instances as fields for reliable comparison
    private final StageGoalChecker IS_CROSS_SOLVED = this::isCrossSolved;
    private final StageGoalChecker IS_F2L_P1_SOLVED = this::isF2LPair1Solved;
    private final StageGoalChecker IS_F2L_P2_SOLVED = this::isF2LPair2Solved;
    private final StageGoalChecker IS_F2L_P3_SOLVED = this::isF2LPair3Solved;
    private final StageGoalChecker IS_F2L_P4_SOLVED = this::isF2LPair4Solved;
    private final StageGoalChecker IS_OLL_SOLVED = this::isOLLSolved;

    public Solver(int maxDepth) {
        this.maxDepth = maxDepth;
        
        this.OLL_ALGORITHMS = new HashMap<>();
        OLL_ALGORITHMS.put("SUNE", "R U R' U R U2 R'");

        this.PLL_ALGORITHMS = new HashMap<>();
        PLL_ALGORITHMS.put("T_PERM", "R U R' U' R' F R2 U' R' U' R U R' F'");
    }

    // New Heuristic Node Wrapper for A* search
    private static class AStarNode {
        CubeGraph.Node cubeNode;
        int gScore; // Path cost (path.size())
        int hScore; // Heuristic estimate

        public AStarNode(CubeGraph.Node node, int h) {
            this.cubeNode = node;
            this.gScore = node.path.size();
            this.hScore = h;
        }

        public int fScore() {
            return gScore + hScore;
        }
    }

    // Custom Comparator for the PriorityQueue
    private static class AStarComparator implements Comparator<AStarNode> {
        @Override
        public int compare(AStarNode a, AStarNode b) {
            int fDiff = a.fScore() - b.fScore();
            if (fDiff != 0) return fDiff;
            return a.hScore - b.hScore; 
        }
    }


    /**
     * Solves the cube using a seven-stage CFOP-like approach.
     */
    public CubeGraph.Node solve(Cube start) {
        ArrayList<String> fullPath = new ArrayList<>();
        Cube nextStageStart = start;
        CubeGraph.Node finalResult = null;
        
        // Use Integer.MAX_VALUE as the "no limit" indicator for F2L stages
        int NO_LIMIT = Integer.MAX_VALUE;
        
        // --- Stage 1: Cross (A* Search) ---
        System.out.println("Starting Stage 1: Cross (A* Search)...");
        // Cross is fast and uses the original maxDepth
        CubeGraph.Node stage1Result = stageSearch(nextStageStart, IS_CROSS_SOLVED, this::getCrossHeuristic, null, maxDepth);
        if (stage1Result == null) return null;
        nextStageStart = stage1Result.currentState;
        fullPath.addAll(stage1Result.path);
        finalResult = new CubeGraph.Node(nextStageStart, stage1Result.parentEdge, fullPath);
        System.out.println("--- Stage 1 (Cross) Solved in " + stage1Result.path.size() + " moves ---");
        
        // --- Stage 2: F2L Pair 1 (FR) (A* Search) ---
        System.out.println("\nStarting Stage 2: F2L Pair 1 (FR) (A* Search)...");
        // Preservation check set to null (removed), limit is effectively unlimited
        CubeGraph.Node stage2Result = stageSearch(nextStageStart, IS_F2L_P1_SOLVED, this::getF2LPair1Heuristic, null, NO_LIMIT);
        if (stage2Result == null) { System.out.println("F2L Pair 1 failed. Stopping."); return finalResult; }
        nextStageStart = stage2Result.currentState;
        fullPath.addAll(stage2Result.path);
        System.out.println("--- Stage 2 (F2L P1) Solved in " + stage2Result.path.size() + " relative moves ---");
        
        // --- Stage 3: F2L Pair 2 (RB) (A* Search) ---
        System.out.println("\nStarting Stage 3: F2L Pair 2 (RB) (A* Search)...");
        // Preservation check set to null (removed), limit is effectively unlimited
        CubeGraph.Node stage3Result = stageSearch(nextStageStart, IS_F2L_P2_SOLVED, this::getF2LPair2Heuristic, null, NO_LIMIT);
        if (stage3Result == null) { System.out.println("F2L Pair 2 failed. Stopping."); return finalResult; }
        nextStageStart = stage3Result.currentState;
        fullPath.addAll(stage3Result.path);
        System.out.println("--- Stage 3 (F2L P2) Solved in " + stage3Result.path.size() + " relative moves ---");
        
        // --- Stage 4: F2L Pair 3 (BL) (A* Search) ---
        System.out.println("\nStarting Stage 4: F2L Pair 3 (BL) (A* Search)...");
        // Preservation check set to null (removed), limit is effectively unlimited
        CubeGraph.Node stage4Result = stageSearch(nextStageStart, IS_F2L_P3_SOLVED, this::getF2LPair3Heuristic, null, NO_LIMIT);
        if (stage4Result == null) { System.out.println("F2L Pair 3 failed. Stopping."); return finalResult; }
        nextStageStart = stage4Result.currentState;
        fullPath.addAll(stage4Result.path);
        System.out.println("--- Stage 4 (F2L P3) Solved in " + stage4Result.path.size() + " relative moves ---");
        
        // --- Stage 5: F2L Pair 4 (LF) (A* Search) ---
        System.out.println("\nStarting Stage 5: F2L Pair 4 (LF) (A* Search)...");
        // Preservation check set to null (removed), limit is effectively unlimited
        CubeGraph.Node stage5Result = stageSearch(nextStageStart, IS_F2L_P4_SOLVED, this::getF2LPair4Heuristic, null, NO_LIMIT);
        if (stage5Result == null) { System.out.println("F2L Pair 4 failed. Stopping."); return finalResult; }
        nextStageStart = stage5Result.currentState;
        fullPath.addAll(stage5Result.path);
        finalResult = new CubeGraph.Node(nextStageStart, stage5Result.parentEdge, fullPath);
        System.out.println("--- Stage 5 (F2L P4) Solved in " + stage5Result.path.size() + " relative moves ---");


        // --- Stage 6: OLL (Algorithm lookup/application based) ---
        System.out.println("\nStarting Stage 6: OLL...");
        CubeGraph.Node stage6Result = stageApplyAlgorithm(nextStageStart, IS_OLL_SOLVED, OLL_ALGORITHMS, "OLL", fullPath);

        if (stage6Result != null) {
            nextStageStart = stage6Result.currentState;
            fullPath = stage6Result.path; 
            finalResult = new CubeGraph.Node(nextStageStart, stage6Result.parentEdge, fullPath);
        } else {
             System.out.println("OLL failed to solve using hardcoded algorithms. Proceeding to PLL attempt.");
        }


        // --- Stage 7: PLL (Algorithm lookup/application based) ---
        System.out.println("\nStarting Stage 7: PLL...");
        CubeGraph.Node stage7Result = stageApplyAlgorithm(nextStageStart, Cube::isSolved, PLL_ALGORITHMS, "PLL", fullPath);

        if (stage7Result != null) {
            finalResult = stage7Result;
        } else {
            System.out.println("PLL failed to solve using hardcoded algorithms.");
        }


        // --- Final Node Construction and Reporting ---
        System.out.println("\n--- Solver Finished ---");
        System.out.println("Total Path Length: " + finalResult.path.size() + " moves.");
        System.out.println("Total Path: " + String.join(" ", finalResult.path));

        return finalResult;
    }
    
    // --- Functional Interfaces for Search ---
    private interface StageGoalChecker {
        boolean check(Cube cube);
    }
    
    private interface HeuristicFunction {
        int get(Cube cube);
    }
    
    private interface PreservationChecker {
        boolean check();
    }
    
    // --- Heuristic Access Methods (Required for method references) ---
    private int getCrossHeuristic(Cube cube) { return cube.getCrossHeuristic(); }
    private int getF2LPair1Heuristic(Cube cube) { return cube.getF2LPair1Heuristic(); }
    private int getF2LPair2Heuristic(Cube cube) { return cube.getF2LPair2Heuristic(); }
    private int getF2LPair3Heuristic(Cube cube) { return cube.getF2LPair3Heuristic(); }
    private int getF2LPair4Heuristic(Cube cube) { return cube.getF2LPair4Heuristic(); }
    
    // --- Goal Check Access Methods (Required for method references) ---
    private boolean isCrossSolved(Cube cube) { return cube.isCrossSolved(); }
    private boolean isF2LPair1Solved(Cube cube) { return cube.isF2LPair1Solved(); }
    private boolean isF2LPair2Solved(Cube cube) { return cube.isF2LPair2Solved(); }
    private boolean isF2LPair3Solved(Cube cube) { return cube.isF2LPair3Solved(); }
    private boolean isF2LPair4Solved(Cube cube) { return cube.isF2LPair4Solved(); }

    private boolean isOLLSolved(Cube cube) {
        return cube.isOLLSolved();
    }
    
    /**
     * Generic stage search implementing A*.
     * Preservation checks and depth limit checks are now removed internally for F2L stages.
     */
    private CubeGraph.Node stageSearch(Cube start, StageGoalChecker goalChecker, 
                                       HeuristicFunction heuristic, PreservationChecker preservationChecker, int limit) {
        
        Set<String> visited = new HashSet<>();
        CubeGraph.Node root = new CubeGraph.Node(start, null, new ArrayList<>());
        visited.add(start.toString());
        
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(new AStarComparator());
        AStarNode rootA = new AStarNode(root, heuristic.get(root.currentState));
        pq.add(rootA);

        while (!pq.isEmpty()) {
            AStarNode currentA = pq.poll();
            CubeGraph.Node node = currentA.cubeNode;
            
            if (goalChecker.check(node.currentState)) {
                return node;
            }
            
            // Limit check is only performed for the Cross stage (when limit is low)
            if (limit < 1000 && node.path.size() >= limit) {
                continue;
            }

            CubeGraph graph = new CubeGraph(node.currentState);
            graph.currentNode = node;
            graph.expandCurrent();

            for (CubeGraph.Node child : node.children) {
                if (!visited.contains(child.currentState.toString())) {
                    
                    // The aggressive preservation check is now fully removed for F2L stages.
                    // The search will rely purely on the heuristic guiding it toward the next goal.

                    visited.add(child.currentState.toString());
                    
                    int h = heuristic.get(child.currentState);
                    AStarNode childA = new AStarNode(child, h);
                    pq.add(childA);
                }
            }
        }
        return null;
    }
    
    // --- Algorithm Stage (Unchanged) ---
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

            // Preservation check for OLL/PLL relies on the full F2L layer being preserved
            if (goalChecker.check(currentCube) && currentCube.isF2LPreserved()) {
                System.out.println(stageName + " solved via Algorithm: " + patternName);
                
                ArrayList<String> fullPath = new ArrayList<>(previousPath);
                fullPath.addAll(algorithmPath);
                
                return new CubeGraph.Node(currentCube, algorithmPath.get(algorithmPath.size() - 1), fullPath);
            }
        }

        return null; 
    }
    
    // --- Helper Methods (Unchanged) ---
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
            case "U2": return "U2";
            case "D2": return "D2";
            case "L2": return "L2";
            case "R2": return "R2";
            case "F2": return "F2";
            case "B2": return "B2";
        }
        return null;
    }
}