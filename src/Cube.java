import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;


public class Cube {
    public ArrayList<Character> cube;


    //this creates our cube object.
    public Cube(String scrambled) throws FileNotFoundException {

        cube = new ArrayList<Character>(54);
        File cubeFile = new File(scrambled);
        Scanner cubeScanned = new Scanner(cubeFile);

        for (int i = 0; i < 54; i++) {
            String token = cubeScanned.next();
            char c = token.charAt(0);
            cube.set(i, c);
        }
    }

    @Override //gives us a string representation of cube
    public String toString(){
        String cubeString = new String();
        for (int i = 0; i < 54; i++) {
            cubeString = cubeString.concat(cube.get(i).toString());
        }
        return cubeString;
    }


}



