package braedenstewart.assign3;

// AvlTree class
//
// CONSTRUCTION: with no initializer
//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// void remove( x )       --> Remove x (unimplemented)
// boolean contains( x )  --> Return true if x is present
// boolean remove( x )    --> Return true if x was present
// Comparable findMin( )  --> Return smallest item
// Comparable findMax( )  --> Return largest item
// boolean isEmpty( )     --> Return true if empty; else false
// void makeEmpty( )      --> Remove all items
// void printTree( )      --> Print tree in sorted order
// ******************ERRORS********************************
// Throws UnderflowException as appropriate

/**
 * Implements an AVL tree.
 * Note that all "matching" is based on the compareTo method.
 * @author Mark Allen Weiss
 */
public class AVLTree<AnyType extends Comparable<? super AnyType>>{
    private static final int ALLOWED_IMBALANCE = 1; // maximum allowed imbalance allowed in AVL tree
    private AvlNode<AnyType> root; // the tree root

    /**
     * Construct the tree.
     */
    public AVLTree(){
        root = null;
    }

    /**
     * Insert into the tree; duplicates are ignored.
     * @param value the item to insert.
     */
    public void insert(AnyType value){
        root = insert(value, root);
    }

    /**
     * Remove from the tree. Nothing is done if value is not found.
     * @param value the item to remove.
     */
    public void remove(AnyType value){
        root = remove(value, root);
    }

    /**
     * Find the smallest item in the tree.
     * @return smallest item or null if empty.
     */
    public AnyType findMin(){
        // throws a runtime exception if the tree is empty
        if(isEmpty()){
            throw new RuntimeException();
        }
        // call internal method findMin()
        return findMin(root).element;
    }

    /**
     * function to delete smallest node in tree
     * @author Braeden Bruce Stewart
     */
    public void deleteMin(){
        // call internal deleteMin() method and then balance the tree
        root = deleteMin(root);
        root = balance(root);
     }

    /**
     * Find the largest item in the tree.
     * @return the largest item of null if empty.
     */
    public AnyType findMax(){
        // throws a runtime exception if the tree is empty
        if(isEmpty()){
            throw new RuntimeException( );
        }
        // call internal findMax() method
        return findMax(root).element;
    }

    /**
     * Find an item in the tree.
     * @param value the item to search for.
     * @return true if value is found.
     */
    public boolean contains(AnyType value){
        // call internal contains() method for value
        return contains(value, root);
    }

    /**
     * Make the tree logically empty.
     */
    public void makeEmpty(){
        root = null;
    }

    /**
     * Test if the tree is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty(){
        // returns true if the root is equal to null
        return root == null;
    }

    /**
     * Print the tree contents in sorted order.
     */
    public void printTree(String label){
        // print out the title
        System.out.println(label);

        // print "Empty tree" for empty trees; call printTree() recursively otherwise
        if(isEmpty()){
            System.out.println("Empty tree");
        }
        else{
            printTree(root,"");
        }
    }

    /**
     * function t determine if AVL tree is balanced
     */
    public void checkBalance(){
        // call internal method checkBalance()
        checkBalance(root);
    }

    /** PRIVATE FUNCTIONS **/

    /**
     * Internal method to remove from a subtree.
     * @param value the item to remove.
     * @param root the node that roots the subtree.
     * @return the new root of the subtree.
     */
    private AvlNode<AnyType> remove(AnyType value, AvlNode<AnyType> root){
        // Item not found; do nothing
        if(root == null){
            return root;
        }
        // compare current nodes value to the desired value
        int compareResult = value.compareTo(root.element);
        if(compareResult < 0){
            // call recursively on left subtree if the desired value is less than current root
            root.left = remove(value, root.left);
        }
        else if(compareResult > 0){
            // call recursively on right subtree if desired value is greater than current root
            root.right = remove(value, root.right);
        }
        else if( root.left != null && root.right != null ){
            // replace current root with the smallest node in its right subtree if the current root has two children
            root.element = findMin(root.right).element;
            root.right = remove(root.element, root.right);
        }
        else{
            // replace node with its left child if root.left exists; replace with root.right otherwise
            root = ( root.left != null ) ? root.left : root.right;
        }
        // balance tree after removing the value
        return balance( root );
    }

    /**
     * function to balance a given AVL subtree
     * @param root the root of current subtree being balanced
     * @return the root of the newly balanced tree
     */
    private AvlNode<AnyType> balance(AvlNode<AnyType> root){
        // Assume root is either balanced or within one of being balanced
        if(root == null){
            return root;
        }
        // balance the tree if the height difference between the left and right subtrees is greater than 1
        if(height(root.left) - height(root.right) > ALLOWED_IMBALANCE){
            if(height(root.left.left) >= height(root.left.right)){
                // perform a right rotation if leftNode.left's height is >= of leftNode.right's height
                root = rightRotation(root);
            }
            else{
                // otherwise do a double right rotation
                root = doubleRightRotation(root);
            }
        }
        if(height(root.right) - height(root.left) > ALLOWED_IMBALANCE){
            if(height(root.right.right) >= height(root.right.left)){
                // perform a left rotation if rightNode.left's height is >= of rightNode.right's height
                root = leftRotation(root);
            }
            else{
                // otherwise do a double left rotation
                root = doubleLeftRotation(root);
            }
        }
        // update the height of the current root and then return the root
        root.height = Math.max(height(root.left), height(root.right)) + 1;
        return root;
    }

    /**
     * internal function to check the balance of the AVL tree
     * @param root root of AVL tree that we are checking balance for
     * @return the height of the current root
     */
    private int checkBalance(AvlNode<AnyType> root){
        // return a -1 if the tree is empty
        if(root == null){
            return -1;
        }
        // check the balance of the left and right subtrees if the root is not null
        if(root != null){
            int hl = checkBalance( root.left );
            int hr = checkBalance(root.right);
            if(Math.abs(height(root.left) - height(root.right)) > 1 ||
                    height(root.left) != hl || height(root.right) != hr){
                // print an error message if heights of root.left or root.right do no match their results from earlier
                System.out.println("\n\n***********************OOPS!!");
            }
        }
        // return height of current root
        return height(root);
    }

    /**
     * Internal method to insert into a subtree.  Duplicates are allowed
     * @param value the item to insert.
     * @param root the node that roots the subtree.
     * @return the new root of the subtree.
     */
    private AvlNode<AnyType> insert(AnyType value, AvlNode<AnyType> root){
        // if the tree is empty then insert the value as the new root
        if(root == null){
            return new AvlNode<>(value, null, null);
        }
        // compare value to current root
        int compareResult = value.compareTo(root.element);

        // insert value into left subtree if it is less than current root
        if(compareResult < 0){
            root.left = insert(value, root.left);
        }
        // insert into right subtree otherwise
        else{
            root.right = insert(value, root.right);
        }
        // balance the tree after inserting a node
        return balance(root);
    }

    /**
     * Internal method to find the smallest item in a subtree.
     * @param root the node that roots the tree.
     * @return node containing the smallest item.
     */
    private AvlNode<AnyType> findMin(AvlNode<AnyType> root){
        // return the root if given an empty tree
        if(root == null){
            return root;
        }
        // find the furthest left node {which will be a leaf node}
        while(root.left != null){
            root = root.left;
        }
        // return the furthest left node
        return root;
    }

    /**
     * Internal method to delete the smallest item in a subtree
     * @param root the node that roots the tree
     * @return the new node that roots the tree
     */
    private AvlNode<AnyType> deleteMin(AvlNode<AnyType> root){
        // don't even try to delete the min of a empty tree
       if (root == null){
           return null;
       }
       // replace current root with root.right if root.left == null && root.right != null
       else if (root.left == null){
           if (root.right != null){
               AvlNode<AnyType> rightNode = root.right;
               return rightNode;
           }
           // don't bother replacing the deleted node with its children if it has no children
           else{
               return null;
           }
       }
       // the following updates the height of the root of the subtree if it is going to turn into a leaf node
       else{
           if (root.left.left == null && root.right == null){
               root.height = 0;
           }
       }
       // return root of tree after removing its farthest left node from its left subtree
       root.left = deleteMin(root.left);
       return root;
    }

    /**
     * Internal method to find the largest item in a subtree.
     * @param root the node that roots the tree.
     * @return node containing the largest item.
     */
    private AvlNode<AnyType> findMax(AvlNode<AnyType> root){
        // return the root if the tree is empty
        if(root == null){
            return root;
        }
        // find furthest right node and return it
        while(root.right != null){
            root = root.right;
        }
        return root;
    }

    /**
     * Internal method to find an item in a subtree.
     * @param value item to search for.
     * @param root the node that roots the tree.
     * @return true if value is found in subtree.
     */
    private boolean contains(AnyType value, AvlNode<AnyType> root){
        while(root != null){
            int compareResult = value.compareTo(root.element);

            // look in left tree if the given value is less than the current root
            if(compareResult < 0){
                root = root.left;
            }
            // look in right tree if then given value is greater than the current root
            else if(compareResult > 0){
                root = root.right;
            }
            // return true once match is found
            else{
                return true;
            }
        }
        // return false once match is not found
        return false;   // No match
    }

    /**
     * Internal method to print a subtree in sorted order.
     * @param root the node that roots the tree.
     */
    private void printTree(AvlNode<AnyType> root, String indent){
        // only print tree if the root is not null
        if(root != null){
            // print right subtree then root and then left subtree
            printTree(root.right, indent+"   ");
            System.out.println(indent+ root.element + "("+ root.height  +")");
            printTree(root.left, indent+"   ");
        }
    }

    /**
     * Return the height of node root, or -1, if null.
     */
    private int height(AvlNode<AnyType> root){
        // return -1 if the node is null
        if (root==null){
            return -1;
        }
        // return the current height
        return root.height;
    }

    /**
     * Rotate binary tree node with left child.
     * For AVL trees, this is a single rotation for case 1.
     * Update heights, then return new root.
     * @param root root of current subtree
     * @return the new root of current subtree
     */
    private AvlNode<AnyType> rightRotation(AvlNode<AnyType> root){
        AvlNode<AnyType> theLeft = root.left;
        root.left = theLeft.right;
        theLeft.right = root;
        root.height = Math.max( height( root.left ), height( root.right ) ) + 1;
        theLeft.height = Math.max( height( theLeft.left ), root.height ) + 1;
        return theLeft;
    }

    /**
     * Rotate binary tree node with right child.
     * For AVL trees, this is a single rotation for case 4.
     * Update heights, then return new root.
     * @param root root of current subtree
     * @return new root of current subtree
     */
    private AvlNode<AnyType> leftRotation(AvlNode<AnyType> root){
        AvlNode<AnyType> theRight = root.right;
        root.right = theRight.left;
        theRight.left = root;
        root.height = Math.max( height( root.left ), height( root.right ) ) + 1;
        theRight.height = Math.max( height( theRight.right ), root.height ) + 1;
        return theRight;
    }

    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child.
     * For AVL trees, this is a double rotation for case 2.
     * Update heights, then return new root.
     * @param root root of current subtree
     * @return new root of current subtree
     */
    private AvlNode<AnyType> doubleRightRotation(AvlNode<AnyType> root){
        root.left = leftRotation(root.left);
        return rightRotation(root);
    }

    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child.
     * For AVL trees, this is a double rotation for case 3.
     * Update heights, then return new root.
     */
    private AvlNode<AnyType> doubleLeftRotation(AvlNode<AnyType> root ){
        root.right = rightRotation( root.right );
        return leftRotation( root );
    }

    /**
     * This class defines the variables and methods for nodes in an AVL tree
     * @param <AnyType> means the node is generic
     */
    private static class AvlNode<AnyType>{
        AnyType element;      // The data in the node
        AvlNode<AnyType>  left;         // Left child
        AvlNode<AnyType>  right;        // Right child
        int height;       // Height

        // Constructors
        AvlNode(AnyType theElement){
            this(theElement, null, null);
        }

        AvlNode( AnyType theElement, AvlNode<AnyType> leftNode, AvlNode<AnyType> rightNode){
            element  = theElement;
            left     = leftNode;
            right    = rightNode;
            height   = 0;
        }
    }

    /**
     * this tests the program
     * @param args takes a argument from the console as an array of strings
     */
    public static void main( String [] args ){
        AVLTree<Integer> t = new AVLTree<>();
        AVLTree<Dwarf> t1 = new AVLTree<>();

        Integer[] integerList = {1, 2, 3, 4, 1, 2, 3, 4};
        for (int i = 0; i < integerList.length; i++){
            t.insert(integerList[i]);
        }
        String[] nameList = {"Snowflake", "Sneezy", "Doc", "Grumpy", "Bashful", "Dopey", "Happy", "Doc", "Grumpy", "Bashful", "Doc", "Grumpy", "Bashful"};

        for (int i=0; i < nameList.length; i++){
            t1.insert(new Dwarf(nameList[i]));
        }
        t.printTree("\nNumber Tree");
        t1.printTree( "\nDwarf Tree" );

        t1.remove(new Dwarf("Bashful"));
        t1.printTree( "\nDwarf Tree after deleting Bashful" );

        t.remove(1);
        t.printTree("\nNumber Tree after deleting 1");

        for (int i=0; i < 8; i++){
            t1.deleteMin();
            t1.printTree( "\n\nDwarf Tree after deleteMin" );
        }
        for (int i = 0; i < 8; i++){
            t.deleteMin();
            t.printTree("\n\nNumber Tree after deleteMin");
        }
    }
}
