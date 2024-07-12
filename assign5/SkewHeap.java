package braedenstewart.assign5;

public class SkewHeap {
    private Pair root;
    private int size;

    public SkewHeap(){
        root = null;
    }
    public void insert(Pair pair){
        root = merge(root, pair);
        size++;
    }
    public Pair merge(Pair h1, Pair h2){
        if (h1 == null){
            return h2;
        }
        else if (h2 == null){
            return h1;
        }
        if (h1.compareTo(h2) > 0){
            Pair oldLeft = h1.left;
            h1.left = merge(h1.right, h2);
            h1.right = oldLeft;

            return h1;
        }
        else{
            Pair oldRight = h2.right;
            h2.right = merge(h2.left, h1);
            h2.left = oldRight;

            return h2;
        }
    }
    public Pair deleteMax(){
        if (root == null){
            return root;
        }
        Pair oldRoot = root;
        root = merge(root.left, root.right);
        size--;

        return oldRoot;
    }
    public braedenstewart.assign5.Pair getRoot(){
        return root;
    }
    public int getSize(){
        return size;
    }
}
