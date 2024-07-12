package braedenstewart.assign6;

import java.io.IOException;
import java.util.Scanner;

/**
 * this class is just used to run and use the features from Hex.java
 */
public class main {
    public static void main(String[] args) throws IOException {
        Hex hex = new Hex();
        Scanner scanner = new Scanner(System.in);

        // ask the user to type in the name of the file that contains the set of moves
        System.out.println("Please type in the name of the file containing the moves: ");
        String input = scanner.nextLine();

        hex.play(input);
    }
}
