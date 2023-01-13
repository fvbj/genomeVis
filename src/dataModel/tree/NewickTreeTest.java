package dataModel.tree;

import transforms.Vec2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class NewickTreeTest {
    /**
     * Slurps the entire file into a single String, and returns it
     */
    public static String readIntoString(String filename) throws IOException {
        StringBuffer buff = new StringBuffer();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = in.readLine()) != null) buff.append(line);
        in.close();
        return buff.toString();
    }

    /**
     * Some tree testing
     */
    public static void main(String[] args) throws IOException {
        // Smaller trees
        NewickTree bt = new NewickTree();
        BinaryTree<NodeNewick> t1 = bt.parseNewick("((a:0.1,b:0.15)c:0.2,(d:0.25,e:0.28)f:0.3)g:0.11;");
        int max = bt.calculateDepth(t1,0, 0);
        bt.calculatePosXYLevel(t1, new Vec2D(), 0);
        System.out.println(t1);
        //bt.splitByDistance(t1, 0.45, 0);
        //bt.splitByLevel(t1, 1, 0);
        System.out.println(t1);
        System.out.println("height:" + t1.height());
        System.out.println("size:" + t1.size());
        System.out.println("fringe:" + t1.fringe());
        System.out.println("max:" + max);

        /*Tree<String> t2 = bt.parseNewick("((a,b)c,(d,e)f)g;");
        Tree<String> t3 = bt.parseNewick("((a,b)z,(d,e)f)g;");
        System.out.println("== " + t1.equalsTree(t2) + " " + t1.equalsTree(t3));

        // Tournament
        Tree<String> tournament = bt.parseNewick("(((b,c1)b,(c2,d)d)d,((h,p1)h,(p2,y)y)h)d;");
        System.out.println(tournament);
        */
        // Tree of life
        String s = readIntoString("data/speciesTree.txt");
        BinaryTree<NodeNewick> itol = bt.parseNewick(s);
        max = bt.calculateDepth(itol,0,0);
        bt.calculatePosXYLevel(itol, new Vec2D(), 0);
        //int groups = bt.splitBydistance(itol, 0.2, 0);
        int groupsN = bt.splitByLevel(itol, 15, 0);
        System.out.println(itol);
        System.out.println("height:" + itol.height());
        System.out.println("size:" + itol.size());
        System.out.println("fringe:" + itol.fringe().size());
        System.out.println("nodes:" + itol.nodes().size());
        //System.out.println("fringe:" + itol.fringe());
        System.out.println("max:" + max);
        System.out.println("groups:" + groupsN);
        System.out.println(bt.getSpecies(itol.fringe()));
        List<List<NodeNewick>> groups = bt.getGroups(itol.nodes(),groupsN+1);
        for(List<NodeNewick> group:groups){
            System.out.println(" " + group.size());
            if (group.size()>50)
                System.out.println(" " + group);
        }
    }
}
