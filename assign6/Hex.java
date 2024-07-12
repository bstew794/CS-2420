package braedenstewart.assign6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Hex {
    private ArrayList<Integer> blueList = new ArrayList<>(); // keeps track of how many moves blue has made
    private ArrayList<Integer> redList = new ArrayList<>(); // keeps track of how many moves red has made
    private ArrayList<GridEntry> grid; // representation of the game grid
    private int LEFT = 122;
    private int RIGHT = 123;
    private int TOP = 124;
    private int BOTTOM = 125;

    /**
     * constructor calls setUp().
     */
    Hex(){
        setUp();
    }

    /**
     * initializes the grid with entry objects so we can use unionFind on it later
     */
    private void setUp(){
        int numSet[] = new int[126]; // EMPTY = left extra empty space for raisins at numSet[0];

        // numSet has indexes 122 - 125 dedicated for identifying LEFT, RIGHT, TOP, BOTTOM.
        for (int i = 1; i < numSet.length; i++){
            numSet[i] = i;
        }
        grid = createArray(numSet);
    }

    /**
     * reads in a set of moves and determines who the winner it (if any)
     * @param fileName the file we want to grab the set of moves from
     * @throws IOException
     */
    public void play(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String line;
        String playColor = "BLUE";
        int endStr; // stores how many moves it took for a player to win

        // runs the game until a winner is found or the we reach the end of the file
        while ((line = in.readLine()) != null){
            int currTile = Integer.parseInt(line); // the current tile that was selected

            // exits the program when a tile that has already been selected is selected again
            if (grid.get(currTile).isClicked){
                System.out.println("ERROR: Has already been clicked");
                System.exit(3);
            }
            grid.get(currTile).clickIt(playColor);
            int neighbors[] = getNeighbors(currTile);

            // adds tile to move-list for BLUE and unions the tile with LEFT or RIGHT if it is a suitable edge tile
            if (playColor == "BLUE"){
                blueList.add(currTile);

                if (isEdge(currTile, playColor) == "LEFT"){
                    union(grid, currTile, LEFT);
                }
                else if (isEdge(currTile, playColor) == "RIGHT"){
                    union(grid, currTile, RIGHT);
                }
            }
            // adds tile to move-list for RED and unions the tile with LEFT or RIGHT if it is a suitable edge tile
            else{
                redList.add(currTile);

                if (isEdge(currTile, playColor) == "TOP"){
                    union(grid, currTile, TOP);
                }
                else if (isEdge(currTile, playColor) == "BOTTOM"){
                    union(grid, currTile, BOTTOM);
                }
            }
            // unions a tile with its possible neighbors (excluding LEFT, RIGHT, TOP, BOTTOM)
            for (int i = 0; i < neighbors.length; i++){
                if (grid.get(currTile + neighbors[i]).isClicked){
                    if (grid.get(currTile + neighbors[i]).playColor == playColor){
                        union(grid, currTile, currTile + neighbors[i]);
                    }
                }
            }
            // determines who the winner is
            if (isWin(playColor)){
                if (playColor == "BLUE"){
                    endStr = blueList.size();
                }
                else{
                    endStr = redList.size();
                }
                System.out.println(playColor + " wins in " + endStr + " moves!");
                break;
            }
            // switches colors before the next iteration of the while loop
            if (playColor == "BLUE"){
                playColor = "RED";
            }
            else{
                playColor = "BLUE";
            }
        }
        System.out.println("Game ended before someone won"); // no one won if the program reaches this point
    }

    /**
     * determines if the grid is a win state for the given player color
     * @param playColor the color of the player
     * @return true if it is a win state; false otherwise
     */
    private boolean isWin(String playColor){
        if (playColor == "BLUE"){
            if (find(grid, LEFT) == find(grid, RIGHT)){
                return true;
            }
            return false;
        }
        else{
            if (find(grid, TOP) == find(grid, BOTTOM)){
                return true;
            }
            return false;
        }
    }

    /**
     * determines if given tile location is a desired edge for the given playColor
     * @param location the location of the tile we are looking at
     * @param playColor the player's color
     * @return the edge it is attached to or "no" if it is not a desired edge
     */
    private String isEdge(int location, String playColor){
        // return whether the edge is attached to "LEFT" or "RIGHT" for "BLUE"
        if (playColor == "BLUE"){
            int x = 1;
            int y = 11;
            for (int i = 0; i < 11; i++){
                if (location == x){
                    return "LEFT";
                }
                else if (location == y){
                    return "RIGHT";
                }
                x += 11;
                y += 11;
            }
            return "no"; // if the tile is not a desired edge
        }
        // return whether the edge is attached to "TOP" or "BOTTOM" for "RED"
        else if (playColor == "RED"){
            int x = 1;
            int y = 122;
            for (int i = 0; i < 11; i++){
                if (location == x){
                    return "TOP";
                }
                else if (location == y){
                    return "BOTTOM";
                }
                x += 1;
                y += 1;
            }
            return "no"; // if the tile is not a desired edge
        }
        return "You are an Idiot HA-HA!"; // something terribly wrong must have happened for this result
    }

    /**
     * returns all possible increment directions for the given tile
     * @param location the current tile it is looking at
     * @return an array containing the possible increments for the tile
     */
    private int[] getNeighbors(int location){
        int neighbors[] = new int[0];

        // check the corner tiles and assign them the correct increments
        if (location == 1){
            neighbors = new int[]{1, 11};
            return neighbors;
        }
        else if (location == 111){
            neighbors = new int[]{-11, 1};
            return neighbors;
        }
        else if (location == 11){
            neighbors = new int[]{-1, 11};
            return neighbors;
        }
        else if (location == 121){
            neighbors = new int[]{-11, -1};
            return neighbors;
        }
        String isEdgeBlue = isEdge(location, "BLUE"); // check if the given tile is an edge for BLUE

        if (isEdgeBlue == "You are an Idiot HA-HA!"){
            System.exit(1); // exit the system, something terrible has happened
        }
        else if (isEdgeBlue == "LEFT"){
            neighbors = new int[]{-11, -10, 1, 11};
        }
        else if (isEdgeBlue == "RIGHT"){
            neighbors = new int[]{-11, -1, 10, 11};
        }
        String isEdgeRed = isEdge(location, "RED"); // check if the tile is an edge for RED

        if (isEdgeRed == "You are an Idiot HA-HA!"){
            System.exit(1); // exit the system, something terrible has happened
        }
        else if (isEdgeRed == "TOP"){
            neighbors = new int[]{-1, 1, 10, 11};
        }
        else if (isEdgeRed == "BOTTOM"){
            neighbors = new int[]{-11, -10, -1, 1};
        }
        // the edge must be a non-Edge tile which means it can have 6 different members/increments
        else{
            neighbors = new int[]{-11, -10, -1, 1, 10, 11};
        }
        return neighbors; // tile has no neighbors
    }

    /**
     * utility function to take a given integer array and transform it into an array of GridEntry objects
     * @param numSet the array of given integers (in sorted order)
     * @return the final array of GridEntry objects
     */
    private ArrayList<GridEntry> createArray(int numSet[]){
        ArrayList<GridEntry> entries = new ArrayList<>();

        // creates a GridEntry object and initially assigns its parent to itself
        for (int i = 0; i < numSet.length; i++){
            entries.add(new GridEntry(numSet[i]));
            entries.get(i).parent = numSet[i];
            entries.get(i).rank = 0;
        }
        return entries;
    }

    /**
     * unions two trees by rank given two integers that are in the same array of GridEntry objects
     * @param miniTree the array of GridEntry objects that i and j are in
     * @param i the first integer
     * @param j the second integer
     */
    private void union(ArrayList<GridEntry> miniTree, int i, int j){
        // find the parents of both i and j
        int iRoot = find(miniTree, i);
        int jRoot = find(miniTree, j);

        // compare the ranks of the two trees and merge the smaller one to the larger one
        if (miniTree.get(iRoot).rank < miniTree.get(jRoot).rank){
            miniTree.get(iRoot).parent = jRoot;
        }
        else if(miniTree.get(iRoot).rank > miniTree.get(jRoot).rank){
            miniTree.get(jRoot).parent = iRoot;
        }
        // if the trees are equal, then assign the i tree to the j tree and increase the j tree's rank
        else{
            miniTree.get(iRoot).parent = jRoot;
            miniTree.get(jRoot).rank++;
        }
    }

    /**
     * finds an integer in a given array of GridEntry objects
     * @param miniTree the given array of GridEntry objects
     * @param index the integer to found which is also its index in the array
     * @return the index/integer's parent
     */
    private int find(ArrayList<GridEntry> miniTree, int index){
        // if the given integer is larger than the size of the array or it is less than zero then exit the program
        if (index >= miniTree.size() || index < 0){
            System.out.println("ERROR: Index out of range for miniTree[]");
            System.exit(2);
        }
        // if the integer's parent is not itself, then search for its parent
        if (miniTree.get(index).parent != index){
            miniTree.get(index).parent = find(miniTree, miniTree.get(index).parent);
        }
        return miniTree.get(index).parent;
    }
}

/**
 * utility class to make the problem easier to solve and so that unionFind can take advantage of parent pointers
 * I realize that in doing this I have not really make UnionFind easy to use with an array of normal integers,
 * and I have tailored it towards the solution of this assignment. I am sorry, but I could not figure anything else out
 * with the amount of time I spent on the assignment.
 */
class GridEntry {
    int element; // the integer and also index of the GridEntry object
    int parent;
    int rank; // the size of the subtree it is a root of.
    boolean isClicked; // stores whether the entry has been selected already in the game
    String playColor; // stores the color of the player who clicked it

    /**
     * constructor
     * @param num the integer we want to store in GridEntry
     */
    GridEntry(int num){
        element = num;
    }

    /**
     * tells the GridEntry that the tile has been selected and who selected it.
     * @param color
     */
    public void clickIt(String color){
        isClicked = true;
        playColor = color;
    }
}

