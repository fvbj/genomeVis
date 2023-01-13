package dataModel;

import dataModel.tree.NodeNewick;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;

public class Group{
    public List<NodeNewick> nodes;
    public Vec3D center;
    public int count;

    Group(){
        nodes = new ArrayList<>();
        center = new Vec3D();
        count = 0;
    }
    @Override
    public String toString() {
        return "Group{" +
                "center=" + center +
                ", count=" + count +
                ", nodes=" + nodes +
                '}';
    }


}

