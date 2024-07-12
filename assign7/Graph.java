package braedenstewart.assign7;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Graph{
    int vertexCt;  // Number of vertices in the graph.
    GraphNode[] grid;  // Adjacency list for graph.
    String graphName;  //The file from which the graph was created.
    private int residual[][];
    ArrayList<Integer>[] paths;

    public Graph(){
        this.vertexCt = 0;
        this.graphName = "";
        this.paths = new ArrayList[8];
    }

    public static void main(String[] args){
        Graph graph1 = new Graph();
        graph1.makeGraph("demands4.txt");
        graph1.setResidual();
        System.out.println(graph1.toString());

        if (!graph1.evalAugs()){
            System.out.println("The parameters cannot be resolved");
        }
        else{
            System.out.println("End of line...");
        }
    }

    public int getDemand(){
        int demand = 0;

        for (int i = 0; i < vertexCt; i++){
            GraphNode currVex = grid[i];

            for (int j = 0; j < currVex.succ.size(); j++){
                GraphNode.EdgeInfo currEdge = currVex.succ.get(j);

                if (currEdge.to == vertexCt - 1){
                    demand += currEdge.capacity;
                }
            }
        }
        return demand;
    }

    public void readResidual(){
        for (int i = 0; i < vertexCt; i++){
            GraphNode currVex = grid[i];

            for (int j = 0; j < currVex.succ.size(); j++){
                GraphNode.EdgeInfo currEdge = currVex.succ.get(j);
                int from = currEdge.from;
                int to = currEdge.to;

                int residue = residual[from][to];

                if (residue != -1){
                    int transported = currEdge.capacity - residue;

                    if (transported <= 0){
                        continue;
                    }
                    String edgeStr = "Edge (" + from + ", " + to + ")";

                    System.out.println(edgeStr + "transports " + transported + " cases");
                }
            }

        }
    }

    public void setResidual(){
        residual = new int[vertexCt][vertexCt];

        for (int i = 0; i < vertexCt; i++){
            for (int j = 0; j < vertexCt; j++){
                residual[i][j] = -1;
            }
        }

        for (int i = 0; i < vertexCt; i++){

            for (int j = 0; j < grid[i].succ.size(); j++){
                GraphNode.EdgeInfo currEdge = grid[i].succ.get(j);
                residual[i][currEdge.to] = currEdge.capacity;
            }
        }
    }

    public boolean evalAugs(){
        int demand = getDemand();
        int produced = 0;

        for (int i = 0; i < grid[0].succ.size(); i++){
            while (residual[grid[0].nodeID][grid[0].succ.get(i).to] != 0){
                for (int j = 0; j < vertexCt; j++){
                    grid[j].visited = false;
                }

                ArrayList<Integer> currAug = findAugs(grid[0].nodeID);

                if (currAug.size() == 0){
                    return false;
                }
                int minCap = Integer.MAX_VALUE;

                for (int j = currAug.size() - 1; j >= 0; j--){
                    GraphNode currVex = grid[currAug.get(j)];

                    for (int k = 0; k < currVex.succ.size(); k++){
                        GraphNode.EdgeInfo currEdge = currVex.succ.get(k);

                        if (currEdge.to != currAug.get(j + 1)){
                            continue;
                        }

                        if (residual[currVex.nodeID][currEdge.to] < minCap){
                            minCap = residual[currVex.nodeID][currEdge.to];
                        }
                    }
                }

                produced += minCap;

                for (int j = currAug.size() - 1; j >= 0; j--){
                    GraphNode currVex = grid[currAug.get(j)];

                    for (int k = 0; k < currVex.succ.size(); k++){
                        GraphNode.EdgeInfo currEdge = currVex.succ.get(k);

                        if (currEdge.to != currAug.get(j + 1)){
                            continue;
                        }

                        residual[currVex.nodeID][currEdge.to] -= minCap;

                        if (residual[currVex.nodeID][currEdge.to] < 0){
                            return false;
                        }
                    }
                }
                System.out.println("found flow " + (i + 1) + " " + currAug);
            }
        }
        if (produced == demand){
            System.out.println("Success: Produced " + produced + ", Demand " + demand);
            readResidual();
            return true;
        }
        else{
            System.out.println("Failure: Produced " + produced + ", Demand " + demand);
            return false;
        }
    }

    public ArrayList<Integer> findAugs(int sourceNum){

        if (sourceNum == vertexCt - 1){
            ArrayList<Integer> path = new ArrayList<>();
            path.add(sourceNum);
            return path;
        }
        for (int i = 0; i < grid[sourceNum].succ.size(); i++){
            GraphNode.EdgeInfo currEdge = grid[sourceNum].succ.get(i);

            if (!grid[sourceNum].visited && residual[sourceNum][currEdge.to] != 0){
                grid[sourceNum].visited = true;
                ArrayList<Integer> otherPath = findAugs(currEdge.to);

                if (otherPath.size() == 0){
                    grid[sourceNum].visited = false;
                    continue;
                }

                ArrayList<Integer> path = new ArrayList<>();

                path.add(sourceNum);
                path.addAll(otherPath);

                return path;
            }
        }
        return new ArrayList<>();
    }

    public boolean addEdge(int source, int destination, int cap) {
        //System.out.println("addEdge " + source + "->" + destination + "(" + cap + ")");
        if (source < 0 || source >= vertexCt) return false;
        if (destination < 0 || destination >= vertexCt) return false;

        //add edge
        grid[source].addEdge(source, destination, cap);
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("The Graph " + graphName + " \n");

        for (int i = 0; i < vertexCt; i++) {
            sb.append(grid[i].toString());
        }
        return sb.toString();
    }

    public void makeGraph(String filename) {
        try {
            graphName = filename;
            Scanner reader = new Scanner(new File(filename));
            vertexCt = reader.nextInt();
            grid = new GraphNode[vertexCt];
            for (int i = 0; i < vertexCt; i++) {
                grid[i] = new GraphNode(i);
            }
            while (reader.hasNextInt()) {
                int v1 = reader.nextInt();
                int v2 = reader.nextInt();
                int cap = reader.nextInt();
                if (!addEdge(v1, v2, cap))
                    throw new Exception();
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}