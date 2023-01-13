package dataModel;

import dataModel.tree.NodeNewick;

import java.util.ArrayList;
import java.util.List;

public class GroupMaker {
    public static List<Group> getGroups(List<NodeNewick> list, int groups){
        List<Group> map = new ArrayList<>();
        for(int i=0; i<groups;i++)
            map.add(new Group());

        for (NodeNewick node: list) {
            map.get(node.groupId).nodes.add(node);
        }
        return map;
    }

}
