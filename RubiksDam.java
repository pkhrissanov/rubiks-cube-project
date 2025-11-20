package rubikscube;

import java.io.*;

public class RubiksCube {
    /**
     * default constructor
     * Creates a Rubik's Cube in an initial state:
     *    OOO
     *    OOO
     *    OOO
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     *    RRR
     *    RRR
     *    RRR
     */             

    private char [][][] faces; //Each face is a 3x3 grid

    private char[] Default_C = {'O', 'G', 'W', 'B', 'Y', 'R'};


    public RubiksCube() {
        // TODO implement me
        //Default cube
        faces = new char [6][3][3]; //Since 6 faces each with a 3x3 grid
        for (int face = 0; face < 6; face++){ //Face
            for (int i = 0; i < 3; i++){ // i (row) + j (column) used as coords on a given face
                for (int j = 0; j < 3; j++){ 
                    faces[face][i][j] = Default_C[face]; //Fill the arrays with the base colours
               } 
            }
        }
    }

    /**
     * @param fileName
     * @throws IOException
     * @throws IncorrectFormatException
     * Creates a Rubik's Cube from the description in fileName
     */
    public RubiksCube(String fileName) throws IOException, IncorrectFormatException {
        // TODO implement me
        //Default cube
        faces = new char [6][3][3]; //Since 6 faces each with a 3x3 grid
        for (int face = 0; face < 6; face++){ //Face
            for (int i = 0; i < 3; i++){ // i(row) + j(column) used as coords on a given face
                for (int j = 0; j < 3; j++){ 
                    faces[face][i][j] = Default_C[face]; //Fill the arrays with the base colours
               } 
            }
        }

        //Get cube from file (only 9 lines in the provided txt)
        String[] lines = new String[9]; //array for insturctions

        //read file
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName); 
        if (in == null) {
            throw new IOException("File not found: " + fileName);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        for (int i = 0; i < 9; i++){
            String line = reader.readLine(); //get string from each line
            lines[i] = line; //get list of instructions
        }
        reader.close();

        //create the "top" segment of the cube
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                faces[0][i][j] = lines[i].charAt(3 + j); //+3 to account for the empty start spaces
            }
        }

        //create 'middle'
        for (int i = 0; i < 3; i++){ //rows
            String row = lines[3 + i];
            for (int j = 0; j < 12; j++){ //columns

                // cols: 012 345    678   91011
                //       LLL FFF    RRR   B B B
                //Faces; Left Front Right Back

                char colour = row.charAt(j);
                int face = (j / 3) + 1; 
                // divide by 3 to group every 3 columns into a face
                //+ 1 cause Ex. 0-2/3 = 0 but we want faces[1] (left), not 0 (up)
                faces[face][i][j % 3] = colour;
                //% gets the proper value in the row
            }
        }

        //create the "bottom" segment of the cube
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                faces[5][i][j] = lines[6 + i].charAt(3 + j); //+3 to account for the empty start spaces
            }
        }
    }   


    /**
     * @param moves
     * Applies the sequence of moves on the Rubik's Cube
     */
    public void applyMoves(String moves) {
        // TODO implement me
        for (int m = 0; m < moves.length(); m++) {
            char curMove = moves.charAt(m);
            int face = 0;
            
            //find the face we are on
            if (curMove == 'U') face = 0;
            else if (curMove == 'L') face = 1;
            else if (curMove == 'F') face = 2;
            else if (curMove == 'R') face = 3;
            else if (curMove == 'B') face = 4;
            else if (curMove == 'D') face = 5;
    
            //rotate the face itself
            char[][] rotated = new char[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    //2-i flips the row into the column direction
                    rotated[j][2 - i] = faces[face][i][j]; 
                }
            }
            faces[face] = rotated;
    
            //rotate adjacent strips to the move
            if (curMove == 'U') { 
                //use clone to save the original state
                //U/D have the same implementation
                char[] tmp = faces[1][0].clone(); //save original back top
                faces[1][0] = faces[2][0];
                faces[2][0] = faces[3][0];
                faces[3][0] = faces[4][0];
                faces[4][0] = tmp; //used the saved back row
            }

            else if (curMove == 'D') {
                char[] tmp = faces[4][2].clone(); //back bottom
                faces[4][2] = faces[1][2];
                faces[1][2] = faces[2][2];
                faces[2][2] = faces[3][2];
                faces[3][2] = tmp;
            }

            //next 4 moves follow the same logic just with extra for loops
            // L/R have an extra for loop cause it rotates columns
            else if (curMove == 'L') {
                char[] tmp = new char[3];
                for (int i = 0; i < 3; i++) tmp[i] = faces[0][i][0];
    
                for (int i = 0; i < 3; i++) faces[0][i][0] = faces[4][2 - i][2]; // U <- B
                for (int i = 0; i < 3; i++) faces[4][i][2] = faces[5][2 - i][0]; // B <- D
                for (int i = 0; i < 3; i++) faces[5][i][0] = faces[2][i][0];     // D <- F
                for (int i = 0; i < 3; i++) faces[2][i][0] = tmp[i];             // F <- U
            }
            else if (curMove == 'R') {
                char[] tmp = new char[3];
                for (int i = 0; i < 3; i++) tmp[i] = faces[0][i][2];
    
                for (int i = 0; i < 3; i++) faces[0][i][2] = faces[2][i][2];     // U <- F
                for (int i = 0; i < 3; i++) faces[2][i][2] = faces[5][i][2];     // F <- D
                for (int i = 0; i < 3; i++) faces[5][i][2] = faces[4][2 - i][0]; // D <- B
                for (int i = 0; i < 3; i++) faces[4][i][0] = tmp[2 - i];         // B <- U
            }
            else if (curMove == 'F') {
                char[] tmp = faces[0][2].clone();
    
                for (int j = 0; j < 3; j++) faces[0][2][j] = faces[1][2 - j][2]; // U <- L
                for (int j = 0; j < 3; j++) faces[1][j][2] = faces[5][0][j];     // L <- D
                for (int j = 0; j < 3; j++) faces[5][0][j] = faces[3][2 - j][0]; // D <- R
                for (int j = 0; j < 3; j++) faces[3][j][0] = tmp[j];             // R <- U
            }
            else if (curMove == 'B') {
                char[] tmp = faces[0][0].clone();
    
                for (int j = 0; j < 3; j++) faces[0][0][j] = faces[3][j][2];     // U <- R
                for (int j = 0; j < 3; j++) faces[3][j][2] = faces[5][2][2 - j]; // R <- D
                for (int j = 0; j < 3; j++) faces[5][2][j] = faces[1][j][0];     // D <- L
                for (int j = 0; j < 3; j++) faces[1][j][0] = tmp[2 - j];         // L <- U
            }
        }
    }

    /**
     * returns true if the current state of the Cube is solved,
     * i.e., it is in this state:
     *    OOO
     *    OOO
     *    OOO
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     * GGGWWWBBBYYY
     *    RRR
     *    RRR
     *    RRR
     */
    public boolean isSolved() {
        //Default cube (modified)
        for (int face = 0; face < 6; face++){
            char colour = faces[face][0][0];
            for (int i = 0; i < 3; i++){ // j + i used as coords on a given face
                for (int j = 0; j < 3; j++){ 
                    if(faces[face][i][j] != colour) return false; //check if every block in the face is the same colour
               } 
            }
        }
        return true;
    }

    @Override
    public String toString() {
        // TODO implement me
        String result = "";

        //top
        for (int i = 0; i < 3; i++){
            result += "   "; //account for empty space in original foramte
            for (int j = 0; j < 3; j++){
                result += faces[0][i][j];
            }
            result += "\n";
        }


        //middle
        for (int i = 0; i < 3; i++){
            for (int face = 1; face <= 4; face++){
                for (int j = 0; j < 3; j++){
                    result += faces[face][i][j];
                }
            }
            result += "\n";
        }

        //bottom (modified top)
        for (int i = 0; i < 3; i++){
            result += "   "; //account for empty space in original foramte
            for (int j = 0; j < 3; j++){
                result += faces[5][i][j]; //0 -> 5 only difference from top
            }
            result += "\n";
        }
        return result;
    }

    /**
     *
     * @param moves
     * @return the order of the sequence of moves
     */
    public static int order(String moves) {
        // TODO implement me
        RubiksCube cube = new RubiksCube();
        RubiksCube initial = new RubiksCube();
        int n = 0;
        while (true) {
            cube.applyMoves(moves); //get the cube
            n++; //count moves
            boolean same = true;
            for (int f = 0; f < 6; f++){
                for (int i = 0; i < 3; i++){
                    for (int j = 0; j < 3; j++){
                        if (cube.faces[f][i][j] != initial.faces[f][i][j]){ //check every face is the same
                             same = false;
                        }
                    }
                }
            }
            
            if (same) {
                return n; //return number
            }
        }
    }
}
