package braedenstewart.assign3;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The main begins by reading in
 * all of the puzzles described in a file named jams.txt.
 * It then proceeds to run a brute force solution., In each case, it prints out the solution
 * path that was computed.
 */

public class TheMain {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // read all the puzzles in file.  Only the first few are solvable without additional strategies
        Puzzle[] puzzles = Puzzle.readPuzzlesFromFile("jamsAll.txt");
        int num_puzzles = 6;

        boolean doPrint = true;
        // solve each of the first six puzzles.  The others will likely take too long
        for (int i = 0; i < num_puzzles; i++){
            puzzles[i].solve(doPrint);
            doPrint = true;
            puzzles[i].aStarSolve(doPrint);
            doPrint = true;
            if (i == 5){
                System.out.println("Puzzle 5 shows that the A* solution is faster by 319 Nodes");
            }
            System.gc();
        }
        doPrint = false;
        // solve all the puzzles with A* and print out shortened versions
        for (int i = 0; i < puzzles.length; i++){
            puzzles[i].aStarSolve(doPrint);
            doPrint = false;
            System.gc();
        }
}

}
