package dataModel.tree;

import transforms.Vec2D;

public class NodeNewick {
    public String label;
    public double distance;
    public double depth;
    public int level;
    public int groupId;
    public boolean isLeaf;
    public Vec2D posXY;

    public NodeNewick(String label, double distance, double depth, int groupId) {
        this.label = label;
        this.distance = distance;
        this.depth = depth;
        this.groupId = groupId;
    }

    public NodeNewick(String label, double distance) {
        this(label,distance,0,0);
    }
    public NodeNewick(String label) {
        this(label,0,0,0);
    }

    @Override
    public String toString() {
        return label
                + ":" + String.format("%3.3f", distance)
                + "<" + String.format("%3.3f", depth) + "[" + level + "]"+ "-" + groupId + ">"
                + (isLeaf?" Leaf":" NL")
                + (posXY!=null?posXY:" noPpsXY");
    }
}