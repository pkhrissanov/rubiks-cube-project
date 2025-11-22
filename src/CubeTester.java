import java.io.*;

public class CubeTester {

    public static void main(String[] args) {
        String[] files = {
                "testCases/scramble01.txt",
                "testCases/scramble02.txt",
                "testCases/scramble03.txt",
                "testCases/scramble04.txt",
                "testCases/scramble05.txt",
                "testCases/scramble06.txt",
                "testCases/scramble07.txt",
                "testCases/scramble08.txt",
                "testCases/scramble09.txt",
                "testCases/scramble10.txt"
        };

        for (String file : files) {
            System.out.println("Testing: " + file);

            try {
                Cube c = new Cube(file);
                System.out.println("✔ SUCCESS: Cube loaded");
                System.out.println("Cube string: " + c.toString());
            } catch (Exception e) {
                System.out.println("❌ ERROR: " + e.getClass().getSimpleName());
                System.out.println("Message: " + e.getMessage());
            }

            System.out.println("-----------------------------------------");
        }
    }
}
