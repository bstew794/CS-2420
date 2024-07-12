package braedenstewart.assign3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the class for representing a particular ParkingLot puzzle.
 * Methods are provided for accessing information about a puzzle, and
 * also for reading in a list of puzzles from a data file.
 * <p>
 * Every car is constrained to only move horizontally or vertically.
 * Therefore, each car has one dimension along which it is fixed, and
 * another dimension along which it can be moved.  The fixed dimension
 * is stored here as part of the puzzle.  Also stored here are the
 * sizes and orientations of the cars, the size of the puzzle grid,
 * the name of the puzzle and the initial (root) search node of the
 * puzzle.
 * <p>
 * The goal car is always assigned index 0.
 */
public class Puzzle {

    private String name;
    private Node initNode;
    private int numCars;
    private int fixedPos[];
    private int carSize[];
    private boolean isVertical[];
    private int gridSize;

    /**
     * The main constructor for constructing a puzzle.  You probably
     * will never need to use this constructor directly, since
     * ordinarily puzzles will be constructed by reading them in from
     * a datafile using the readPuzzlesFromFile method.  It
     * is assumed that the goal car is always assigned index 0.
     *
     * @param name     the name of the puzzle
     * @param gridSize the size of one side of the puzzle grid
     * @param numCars  the number of cars on this puzzle
     * @param orient   the orientations of each car (true if vertical)
     * @param size     the sizes of each car
     * @param x        the x-coordinates of each car
     * @param y        the y-coordinates of each car
     */
     Puzzle(String name,
                  int gridSize,
                  int numCars,
                  boolean orient[],
                  int size[],
                  int x[],
                  int y[]) {
        this.name = name;
        this.numCars = numCars;
        this.gridSize = gridSize;
        if (numCars <= 0) {
            throw new IllegalArgumentException("Each puzzle must have a positive number of cars");
        }
        isVertical = new boolean[numCars];
        carSize = new int[numCars];
        fixedPos = new int[numCars];
        int varPos[] = new int[numCars];

        boolean grid[][] = new boolean[gridSize][gridSize];

         for (int v = 0; v < numCars; v++) {
            isVertical[v] = orient[v];
            carSize[v] = size[v];
            if (size[v] <= 0) {
                throw new IllegalArgumentException("Cars must have positive size");
            }

            if (x[v] < 0 || y[v] < 0
                    || (orient[v] && y[v] + size[v] > gridSize)
                    || (!orient[v] && x[v] + size[v] > gridSize)) {
                throw new IllegalArgumentException("Cars must be within bounds of grid");
            }

            for (int d = 0; d < size[v]; d++) {
                int xv = x[v], yv = y[v];
                if (orient[v]) yv += d;
                else xv += d;
                if (grid[xv][yv])
                    throw new IllegalArgumentException("Cars cannot overlap");
                grid[xv][yv] = true;
            }

            if (orient[v]) {
                fixedPos[v] = x[v];
                varPos[v] = y[v];
            } else {
                fixedPos[v] = y[v];
                varPos[v] = x[v];
            }

        }

        initNode = new Node(null, "", this, varPos, "Initial", 0);
        //System.out.println(initNode.toString());
    }

    /**
     * A static method for reading in a list of puzzles from the data
     * file called filename.  Each puzzle is described in the
     * data file using the format described on the assignment.  The
     * set of puzzles is returned as an array of Puzzle's.
     */
    public static Puzzle[] readPuzzlesFromFile(String filename)
            throws FileNotFoundException, IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        ArrayList<Puzzle> puzzles = new ArrayList<Puzzle>();
        ArrayList<CarRec> car_list = null;

        String name = null;
        String line;
        String[] words = null;

        int read_mode = 0;
        int line_count = 0;
        int gridsize = 0;

        while ((line = in.readLine()) != null) {
            line_count++;
            line = line.trim();
            words = line.split("\\s+");
            if (line.equals(""))
                continue;

            if (read_mode == 0) {   // reading name
                name = line;
                car_list = new ArrayList<>();
                read_mode = 1;
            } else if (read_mode == 1) { // reading grid size
                if (words.length != 1)
                    throw new RuntimeException("Expected single integer for grid size at line " + line_count + " in file " + filename);
                try {
                    gridsize = Integer.parseInt(words[0]);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Expected integer grid size at line " + line_count + " in file " + filename);
                }
                if (gridsize <= 0)
                    throw new RuntimeException("Expected positive grid size at line " + line_count + " in file " + filename);

                read_mode = 2;
            } else if (line.equals(".")) { // end of puzzle description
                int numcars = car_list.size();
                boolean orient[] = new boolean[numcars];
                int size[] = new int[numcars];
                int x[] = new int[numcars];
                int y[] = new int[numcars];

                for (int v = 0; v < numcars; v++) {
                    CarRec carrec = (CarRec) car_list.get(v);
                    orient[v] = carrec.orient;
                    size[v] = carrec.size;
                    x[v] = carrec.x;
                    y[v] = carrec.y;
                }
                puzzles.add(new Puzzle(name, gridsize, numcars, orient, size, x, y));
                read_mode = 0;
            } else {
                final int CAR_DESCRIPTION_COUNT = 4;
                CarRec carrec = new CarRec();
                if (words.length != CAR_DESCRIPTION_COUNT) {
                    throw new RuntimeException("Expected four arguments at line " + line_count + " in file " + filename);
                }
                try {
                    carrec.x = Integer.parseInt(words[0]);
                    carrec.y = Integer.parseInt(words[1]);
                    carrec.size = Integer.parseInt(words[3]);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Expected integer at line " + line_count + " in file " + filename);
                }
                String orientation = words[2].toLowerCase();

                if (!orientation.equals("v") && !orientation.equals("h")) {
                    throw new RuntimeException("Expected orientation to be 'v' or 'h' at line " + line_count + " in file " + filename);
                }
                carrec.orient = orientation.equals("v");
                car_list.add(carrec);
            }
        }

        if (read_mode != 0)
            throw new RuntimeException("Puzzle description ended prematurely in file " + filename);

        return (Puzzle[]) puzzles.toArray(new Puzzle[0]);
    }


    public int getFixedPosition(int v) {
        return fixedPos[v];
    }

    public int getCarSize(int v) {
        return carSize[v];
    }
    public int getNumCars(){
        return numCars;
    }
    /**
     * Returns the orientation of car v, where true
     * means that the car is vertically oriented.
     */
    public boolean getCarOrient(int v) {
        return isVertical[v];
    }

    public String getName() {
        return name;
    }

    public int getGridSize() {
        return gridSize;
    }

    public Node getInitNode() {
        return initNode;
    }

    /**
     * normal solve function
     * @param doPrint determines whether to print it fully or not
     */
    public void solve(boolean doPrint){
        // print border and first node
        System.out.println("========================");
        System.out.println(initNode.toString());

        // create various lists and other variables
        ArrayList<Node> allNodes = new ArrayList<>();
        ArrayList<Node> solutions = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        Node solution=null;

        // add the initial node to the queue
        queue.add(initNode);

        // find the goal state {solve the puzzle}
        while (doPrint){
            // add the front of the queue to allNodes
            Node[] possibleStates = queue.peek().expand();
            allNodes.add(queue.peek());

            // iterate through possibleStates to find states to add to allNodes and queue
            for (int i = 0; i < possibleStates.length; i++){
                boolean doNotAdd = false;

                // go to next state if the current state is a goal state
                if (possibleStates[i].isGoal()){
                     solutions.add(possibleStates[i]);
                }
                // check for duplicates
                for (int j = 0; j < allNodes.size();j++){
                    if (possibleStates[i].equals(allNodes.get(j))){
                        doNotAdd = true;
                        break;
                    }
                }
                if (doNotAdd){
                    continue;
                }
                // add the current state
                queue.add(possibleStates[i]);
                allNodes.add(possibleStates[i]);
            }
            // find the solution with the least amount of moves and assign it to solutions
            if (solutions.isEmpty() != true){
                Node leastMoves = solutions.get(solutions.size() - 1);

                for (int i = 0; i < solutions.size(); i++){
                    if(solutions.get(i).getDepth() < leastMoves.getDepth()){
                        leastMoves = solutions.get(i);
                    }
                }
                solution = leastMoves;
            }
            if (solution != null){
                break;
            }
            // remove Node at front of queue
            queue.remove();
        }
        // print out the solution
        System.out.print("\n\nSOLUTION  of Depth " + solution.getDepth());
        System.out.println(" Total Nodes Expanded " + allNodes.size() + "\n");
        printSolution(solution);
    }

    /**
     * an A* strategy to solve the puzzles using AVL tree queues and hash structures
     * @param doPrint determines whether to solve or not
     */
    public void aStarSolve(boolean doPrint){
        // print a barrier and the the initial state
        System.out.println("========================");
        System.out.println(initNode.toString());

        // create the various hash-maps, trees, and other variables
        HashMap<Integer, Node> allNodes = new HashMap<>();
        ArrayList<Node> solutions = new ArrayList<>();
        AVLTree<Node> queue = new AVLTree<>();
        Node solution = null;
        Node recentNode = initNode; // set recentNode equal to the initial state

        // add the initial state to the queue
        queue.insert(initNode);
        allNodes.put(initNode.hashCode(), initNode);

        // search for goal state until it is found
        while (solution == null){
            // place most recent node into allNodes and then expand recentNode
            Node[] possibleStates = recentNode.expand();

            // iterate through possible states to add them if they are not a duplicate
            for (int i = 0; i < possibleStates.length; i++){
                boolean doNotAdd = false;

                // add node to solutions list if it is equal to the goal state
                if (possibleStates[i].isGoal()){
                    solutions.add(possibleStates[i]);
                }
                // find out if te current state has already been expanded from
                if (allNodes.containsKey(possibleStates[i].hashCode())){
                    Node tempNode = allNodes.get(possibleStates[i].hashCode());

                    // it is okay to add a duplicate state if it reached that state faster
                    if (possibleStates[i].equals(tempNode)){
                        if (possibleStates[i].getDepth() >= tempNode.getDepth()){
                            doNotAdd = true;
                        }
                    }
                }
                if (doNotAdd){
                    continue;
                }
                // add the current state to the queue and allNodes
                queue.insert(possibleStates[i]);
                allNodes.put(possibleStates[i].hashCode(), possibleStates[i]);
            }
            // find the solution with the least amount of moves
            if (solutions.isEmpty() != true){
                Node leastMoves = solutions.get(solutions.size() - 1);

                for (int i = 0; i < solutions.size(); i++){
                    if(solutions.get(i).getDepth() < leastMoves.getDepth()){
                        leastMoves = solutions.get(i);
                    }
                }
                solution = leastMoves;
            }
            // delete recentNode and then assign the next min to it
            queue.deleteMin();
            recentNode = queue.findMin();
        }
        // print the solution after emptying queue
        queue.makeEmpty();
        if(doPrint){
            System.out.print("\n\nA* SOLUTION  of Depth " + solution.getDepth());
            System.out.println(" Total Nodes " + allNodes.size() + "\n");
            printSolution(solution);
        }
        else{
            System.out.println("move " + solution.toString());
        }
        System.gc();
    }


    /**
     * Print the series of moves (in order) to find the solution
     * @param n: ending node in the chain of nodes which solves the puzzle
     */
    public void printSolution(Node n){

        if (n == null) {
            return;
        }
        printSolution(n.getParent());
        System.out.println("Move " + n.toString());
    }

    private static class CarRec{
        boolean orient;
        int size;
        int x;
        int y;
    }

}
