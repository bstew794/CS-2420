package braedenstewart.assign3;

import java.util.ArrayList;

public class Node implements Comparable<Node>{

    private String history;  //Sequence of moves to get to this state
    private Node parent;     //Node from which this node was expanded
    private Puzzle puzzle;   //Puzzle this node is trying to solve
    private int varPos[];    //Starting point of the variable location of car
    private String move;     //Most recent move  in format Cx.y  where Car x is moved to location y.
    private int depth;       //Total number of moves in history
    private int movesToGoal; //Estimated number of remaining moves to reach a solution
    private int priority;    //depth + movesToGoal
    private int grid[][];    //Current contents of grid in terms of car number.
    private int hashcode;    //State of grid in coded form (for easy comparison)

    final int GOAL_CAR = 0;
    /**
     * @param parent  the parent of this node
     * @param history the moves needed to get here
     * @param puzzle  Puzzle being solved
     * @param varPos  Variable location of cars
     * @param move    Last Move
     * @param depth   Total number of moves to get to this node
     */
     Node(Node parent, String history, Puzzle puzzle, int[] varPos, String move, int depth
    ) {
        this.history = history;
        this.parent = parent;
        this.puzzle = puzzle;
        this.varPos = varPos;
        this.move = move;
        this.depth = depth;
        getGrid();
        computeHashCode();
        setMovesToGoal();
        this.priority = this.depth + this.movesToGoal;
    }

    /**
     * @return the node's priority
     */
    public int getPriority(){
         return priority;
    }

    /**
     * @return the node's parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @return the node's depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return the node's hashcode
     */
    public int hashCode() {
        return hashcode;
    }

    @Override
    /**
     * helps implement comparable
     */
    public int compareTo(Node node){
        // compare current node's priority to the priority of the given node
         if (this.priority < node.getPriority()){
            return -1; // return a negative if it is lower
         }
         else if (this.priority > node.getPriority()){
            return 1; // return a positive for greater than
         }
         return 0; // return 0 if it is equal to
    }

    /**
     * Computes a grid representation of the state of the puzzle.  In particular, an
     * nxn two-dimensional integer array is computed.  The (i,j)
     * element of this grid is equal to -1 if square (i,j) is
     * unoccupied, and otherwise contains the index of the car
     * occupying this square.  Note that the grid is recomputed each
     * time this method is called.
     */
    private void getGrid() {
        int gridsize = puzzle.getGridSize();
        grid = new int[gridsize][gridsize];

        for (int i = 0; i < gridsize; i++){
            for (int j = 0; j < gridsize; j++) {
                grid[i][j] = -1;
            }
        }

        int num_cars = puzzle.getNumCars();

        for (int v = 0; v < num_cars; v++) {
            boolean isVertical = puzzle.getCarOrient(v);
            int size = puzzle.getCarSize(v);
            int fp = puzzle.getFixedPosition(v);
            if (v == GOAL_CAR && varPos[v] + size > gridsize) {
                size--;
            }
            if (isVertical){
                for (int d = 0; d < size; d++)
                    grid[fp][varPos[v] + d] = v;
            }
            else {
                for (int d = 0; d < size; d++)
                    grid[varPos[v] + d][fp] = v;
            }
        }
    }

    /**
     * calculates how many cars are in front of or behind the GOAL_CAR
     */
    private void setMovesToGoal(){
        // get the y position of GOAL_CAR
        int goalCarFP = puzzle.getFixedPosition(GOAL_CAR);
        ArrayList<Integer> carsInWay = new ArrayList<>();

        // find all the cars on the same y level as GOAL_CAR and add them to carsInWay
        for (int i = 0; i < puzzle.getGridSize(); i++){
            if (grid[i][goalCarFP] == -1 || grid[i][goalCarFP] == 0){
                continue;
            }
            carsInWay.add(i);
        }
        // set the variable movesToGoal equal to the length of the carsInWay list
        movesToGoal = carsInWay.size();
    }

    /**
     * creates the complete display of the node
     * @return the final string
     */
    public String toString() {
        return puzzle.getName() + " " + history + " [" + depth + "]\n" + displayGrid() + "\n";
    }

    /**
     * checks if current node is the goal
     * @return true if it is the goal
     */
    public boolean isGoal() {
        return (varPos[0] == puzzle.getGridSize() - 1);
    }

    /**
     * turns the grid of the current state into to a string
     * @return the string representation of the current state
     */
    public String displayGrid() {
        String symb = "0123456789ABCDEFGHIJKLMNPQRSTUVWXYZ@#$%^&*?!";
        StringBuilder sb = new StringBuilder();
        int gridsize = puzzle.getGridSize();

        // print top line

        sb.append("+-");
        for (int x = 0; x < gridsize; x++) {
            sb.append("--");
        }
        sb.append("+");
        sb.append("\n");

        for (int y = 0; y < gridsize; y++) {
            sb.append("| ");
            for (int x = 0; x < gridsize; x++) {
                int v = grid[x][y];
                if (v < 0) {
                    sb.append(". ");
                } else {
                    sb.append(symb.charAt(v) + " ");
                }
            }
            if (y == puzzle.getFixedPosition(GOAL_CAR))
                sb.append("  \n");
            else sb.append("| \n");
        }
        // print bottom line
        sb.append("+-");
        for (int i = 0; i < gridsize; i++) {
            sb.append("--");
        }
        sb.append("+");
        // When lots of states are printed, I found it helpful to search for them by code.
        sb.append(" HashCode " + hashcode + "\n");

        return sb.toString();
    }

    /**
     * Computes all of the Nodes immediately reachable from this
     * Node and returns them as an array of Nodes.
     */
    public Node[] expand() {
        int gridsize = puzzle.getGridSize();
        int num_cars = puzzle.getNumCars();

        ArrayList<Node> new_nodes = new ArrayList<Node>();

        for (int v = 0; v < num_cars; v++) {
            int varPo = varPos[v];
            int fixPos = puzzle.getFixedPosition(v);
            boolean orient = puzzle.getCarOrient(v);
            // Find all locations to left or up from current position
            for (int np = varPo - 1; np >= 0 && (orient ? grid[fixPos][np] : grid[np][fixPos]) < 0; np--){
                int[] newVarPos = (int[]) varPos.clone();
                newVarPos[v] = np;
                String move = " C" + v + "." + newVarPos[v];
                new_nodes.add(new Node(this, (history + move), puzzle, newVarPos, move, depth + 1));
            }

            // Find all locations to right or down from current position
            //Original code used tertiary operators, so I left it in for "exposure"
            int carSize = puzzle.getCarSize(v);
            for (int np = varPo + carSize; ((np < gridsize) && ((orient ? grid[fixPos][np] : grid[np][fixPos]) < 0))
                         || ((v == GOAL_CAR) && (np == gridsize)); np++){
                int[] newVarPos = (int[]) varPos.clone();
                newVarPos[v] = np - carSize + 1;
                String move = " C" + v + "." + newVarPos[v];
                new_nodes.add(new Node(this, (history + move), puzzle, newVarPos, move, depth + 1));
            }
        }
        return (Node[]) new_nodes.toArray(new Node[0]);
    }


    /**
     * Returns true if and only if this state is considered
     * equal to the given object.
     * Adjust what is checked to satisfy they way you intend to use it.
     */
    public boolean equals(Object o) {
        Node s;
        try{
            s = (Node) o;
        }
        catch (ClassCastException e){
            return false;
        }
        if (hashcode != s.hashcode)
            return false;
        for (int i = 0; i < varPos.length; i++)
            if (varPos[i] != s.varPos[i])
                return false;
        return true;
    }

    /**
     * calculates the hashcode for easy comparison
     */
    private void computeHashCode() {
        final int SHIFT=31;
        hashcode = 0;
        for (int i = 0; i < varPos.length; i++) {
            hashcode = SHIFT * hashcode + varPos[i];
        }
    }
}
