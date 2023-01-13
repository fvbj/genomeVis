package dataModel.tree;

import java.util.ArrayList;
/**
 * Generic binary tree, storing data of a parametric data in each node
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, Spring 2016, minor updates to testing
 */


public class BinaryTree<E> {
    private BinaryTree<E> left, right;    // children; can be null
    E data;

    /**
     * Constructs leaf node -- left and right are null
     */
    public BinaryTree(E data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    /**
     * Constructs inner node
     */
    public BinaryTree(E data, BinaryTree<E> left, BinaryTree<E> right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    /**
     * Is it an inner node?
     */
    public boolean isInner() {
        return left != null || right != null;
    }

    /**
     * Is it a leaf node?
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * Does it have a left child?
     */
    public boolean hasLeft() {
        return left != null;
    }

    /**
     * Does it have a right child?
     */
    public boolean hasRight() {
        return right != null;
    }

    public BinaryTree<E> getLeft() {
        return left;
    }

    public BinaryTree<E> getRight() {
        return right;
    }

    public E getData() {
        return data;
    }

    /**
     * Number of nodes (inner and leaf) in tree
     */
    public int size() {
        int num = 1;
        if (hasLeft()) num += left.size();
        if (hasRight()) num += right.size();
        return num;
    }

    /**
     * Longest length to a leaf node from here
     */
    public int height() {
        if (isLeaf()) return 0;
        int h = 0;
        if (hasLeft()) h = Math.max(h, left.height());
        if (hasRight()) h = Math.max(h, right.height());
        return h + 1;                        // inner: one higher than highest child
    }

    /**
     * Same structure and data?
     */
    public boolean equalsTree(BinaryTree<E> t2) {
        if (hasLeft() != t2.hasLeft() || hasRight() != t2.hasRight()) return false;
        if (!data.equals(t2.data)) return false;
        if (hasLeft() && !left.equalsTree(t2.left)) return false;
        if (hasRight() && !right.equalsTree(t2.right)) return false;
        return true;
    }

    /**
     * Leaves, in order from left to right
     */
    public ArrayList<E> fringe() {
        ArrayList<E> f = new ArrayList<>();
        addToFringe(f);
        return f;
    }

    /**
     * Helper for fringe, adding fringe data to the list
     */
    private void addToFringe(ArrayList<E> fringe) {
        if (isLeaf()) {
            fringe.add(data);
        } else {
            if (hasLeft()) left.addToFringe(fringe);
            if (hasRight()) right.addToFringe(fringe);
        }
    }

    /**
     * Nodes, in order from left to right
     */
    public ArrayList<E> nodes() {
        ArrayList<E> f = new ArrayList<E>();
        addToNodes(f);
        return f;
    }

    /**
     * Helper for fringe, adding fringe data to the list
     */
    private void addToNodes(ArrayList<E> nodes) {
        nodes.add(data);
        if (isLeaf()) {
            return;
        } else {
            if (hasLeft()) left.addToNodes(nodes);
            if (hasRight()) right.addToNodes(nodes);
        }
    }

    /**
     * Returns a string representation of the tree
     */
    public String toString() {
        return toStringHelper("");
    }

    /**
     * Recursively constructs a String representation of the tree from this node,
     * starting with the given indentation and indenting further going down the tree
     */
    public String toStringHelper(String indent) {
        String res = indent + data + "\n";
        if (hasLeft()) res += left.toStringHelper(indent + "  ");
        if (hasRight()) res += right.toStringHelper(indent + "  ");
        return res;
    }
}