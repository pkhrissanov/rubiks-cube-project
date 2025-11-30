import java.io.IOException;

public class scoreTester {

    public static void main(String[] args) throws IOException {
        try {
            Cube cube = new Cube("testCases/scramble01.txt");
            System.out.print(cube.getScore());
        }
        catch (Exception e){
            System.out.println("fuck you");
        }
    }
}
