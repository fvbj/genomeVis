package dataModel.tree;

import transforms.Vec2D;

import java.util.*;

public class NewickTree {
    public int calculateDepth(BinaryTree<NodeNewick> binaryTree, double depthInit, int levelInit){
        double depth = depthInit + binaryTree.data.distance;
        int level = levelInit+1;
        binaryTree.data.depth = depth;
        binaryTree.data.level = level;
        binaryTree.data.isLeaf = !binaryTree.hasLeft()&&!binaryTree.hasRight();
        int maxDepth = level;

        if (binaryTree.hasLeft() )
            maxDepth = Math.max(maxDepth,
                calculateDepth(binaryTree.getLeft(),depth,level));
        if (binaryTree.hasRight() )
            maxDepth = Math.max(maxDepth,
                    calculateDepth(binaryTree.getRight(),depth,level));
        return maxDepth;
    }

    public int calculatePosXYLevel(BinaryTree<NodeNewick> binaryTree, Vec2D posInit, int initY){
        binaryTree.data.posXY = posInit.add(new Vec2D(binaryTree.data.level,initY));

        if (binaryTree.hasLeft() )
            initY= calculatePosXYLevel(binaryTree.getLeft(),posInit,initY);

        if (binaryTree.hasRight() )
            initY= calculatePosXYLevel(binaryTree.getRight(),posInit,initY+1);
        return initY;
    }

    public int calculatePosXYDepth(BinaryTree<NodeNewick> binaryTree, Vec2D posInit, int initY, double initDepth){
        double depth = initDepth + binaryTree.data.depth;
        binaryTree.data.posXY = posInit.add(new Vec2D(depth,initY));

        if (binaryTree.hasLeft() )
            initY= calculatePosXYDepth(binaryTree.getLeft(),posInit,initY, depth);

        if (binaryTree.hasRight() )
            initY= calculatePosXYDepth(binaryTree.getRight(),posInit,initY+1, depth);
        return initY;
    }

    private Vec2D getMaxPosXY(BinaryTree<NodeNewick> binaryTree, Vec2D maxInit){
        Vec2D max = new Vec2D(Math.max(maxInit.getX(), binaryTree.data.posXY.getX()),
                Math.max(maxInit.getY(), binaryTree.data.posXY.getY()));
        if (binaryTree.hasLeft() )
            max= getMaxPosXY(binaryTree.getLeft(),max);

        if (binaryTree.hasRight() )
            max= getMaxPosXY(binaryTree.getRight(),max);
        return max;
    }

    private void setGroupID(BinaryTree<NodeNewick> binaryTree, int groupID){
        binaryTree.data.groupId = groupID;
        if (binaryTree.hasLeft() )
            setGroupID(binaryTree.getLeft(),groupID);
        if (binaryTree.hasRight() )
            setGroupID(binaryTree.getRight(),groupID);
    }

    public int splitByDistance(BinaryTree<NodeNewick> tree, double depthLimit, int initGroupID){
        int id = initGroupID;
        if (depthLimit < tree.data.depth && depthLimit > (tree.data.depth- tree.data.distance)) {
            id++;
            //setGroupID(tree,id);
            //return id;
        }
        setGroupID(tree,id);
        if (tree.hasLeft() )
            id = splitByDistance(tree.getLeft(),depthLimit,id);
        if (tree.hasRight() )
            id = splitByDistance(tree.getRight(),depthLimit,id);
        return id;
    }

    public int splitByLevel(BinaryTree<NodeNewick> tree, int levelLimit, int initGroupID){
        int id = initGroupID;
        if (levelLimit == tree.data.level || tree.isLeaf()) {
            id++;
            setGroupID(tree,id);
            return id;
        }

        if (tree.hasLeft() )
            id = splitByLevel(tree.getLeft(),levelLimit, id);
        if (tree.hasRight() )
            id = splitByLevel(tree.getRight(),levelLimit, id);
        return id;

    }



    /**
     * Very simplistic binary tree parser based on Newick representation
     * Assumes that each node is given a label; that becomes the data
     * Any distance information (following the colon) is stripped
     * <tree> = "(" <tree> "," <tree> ")" <label> [":"<dist>]
     *        | <label> [":"<dist>]
     * No effort at all to handle malformed trees or those not following these strict requirements
     */
    public BinaryTree<NodeNewick> parseNewick(String s) {
        //BinaryTree<NodeNewick> t = parseNewick(new StringTokenizer(s, "(,);", true));
        //Get rid of the semicolon
        //t.data = new Node(t.data.label.substring(0,t.data.label.length()-1)); //semicolon

        return parseNewick(new StringTokenizer(s, "(,);", true));//t;
    }

    /**
     * Does the real work of parsing, now given a tokenizer for the string
     */
    private BinaryTree<NodeNewick> parseNewick(StringTokenizer st) {
        String token = st.nextToken();
        if (token.equals("(")) {
            // Inner node
            BinaryTree<NodeNewick> left = parseNewick(st);
            st.nextToken(); //comma
            BinaryTree<NodeNewick> right = parseNewick(st);
            st.nextToken(); //close
            String label = st.nextToken();
            String[] pieces = label.split(":");
            //return new BinaryTree<String>(pieces[0], left, right);
            //System.out.println(pieces[0]+" "+pieces[1]);
            if (pieces.length>1)
                return new BinaryTree<>(new NodeNewick(pieces[0], Double.parseDouble(pieces[1])), left, right);
            return new BinaryTree<>(new NodeNewick(label), left, right);
        }
        else {
            // Leaf
            String[] pieces = token.split(":");
            if (pieces.length>1)
                return new BinaryTree<>(new NodeNewick(pieces[0], Double.parseDouble(pieces[1])));
            return new BinaryTree<>(new NodeNewick(token));
            //return new BinaryTree<String>(pieces[0]);
        }
    }

    public HashMap<String, NodeNewick> getSpecies(List<NodeNewick> list){
        HashMap<String, NodeNewick> map = new HashMap<>();
        for (NodeNewick node: list) map.put(node.label, node);
        return map;
    }

    public static class Group{
        List<NodeNewick> node;
    }

    List<List<NodeNewick>> getGroups(List<NodeNewick> list, int groups){
        List<List<NodeNewick>> map = new ArrayList<>();
        for(int i=0; i<groups;i++)
            map.add(new ArrayList<>());

        for (NodeNewick node: list) {
            map.get(node.groupId).add(node);
        }
        return map;
    }

}
