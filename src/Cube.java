import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Cube {
    public ArrayList<Character> cube;


    public Cube(String fileName) throws IOException, IncorrectFormatException {
        File file = new File(fileName);

        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IOException("Cannot read file: " + file.getAbsolutePath());
        }

        final String allowed = "OGWBYR";
        StringBuilder letters = new StringBuilder(54);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int ch;
            while ((ch = br.read()) != -1) {
                char letter = (char) ch;
                if (Character.isWhitespace(letter)) continue;
                letter = Character.toUpperCase(letter);

                if (allowed.indexOf(letter) >= 0) {
                    if (letters.length() == 54) {
                        throw new IncorrectFormatException(
                                "File has more than 54 non-whitespace color letters.");
                    }
                    letters.append(letter);
                } else {
                    throw new IncorrectFormatException(
                            "Invalid character '" + letter + "'. Allowed: " + allowed + " (whitespace is ignored).");
                }
            }
        }

        if (letters.length() != 54) {
            throw new IncorrectFormatException(
                    "Expected exactly 54 non-whitespace color letters, found " + letters.length() + "."
            );
        }
        cube = new ArrayList<Character>(54);
        for (int i = 0; i < 54; i++) {
            cube.add(letters.charAt(i));
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



