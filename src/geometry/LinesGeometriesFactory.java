package geometry;

import dataModel.Group;
import dataModel.Record;
import dataModel.tree.BinaryTree;
import dataModel.tree.IndexColor;
import dataModel.tree.NodeNewick;
import lwjglutils.OGLBuffers;
import transforms.Col;
import transforms.Vec2D;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LinesGeometriesFactory {
    static class Line{
        Vec3D a,b;

        Line(Vec3D a, Vec3D b) {
            this.a = a;
            this.b = b;
        }
    }

    private static void makeLine(BinaryTree<NodeNewick> binaryTree, Vec3D posInit, List<Line> lines, float zMin){
        Vec3D a = new Vec3D(binaryTree.getData().posXY.getX(),
                binaryTree.getData().posXY.getY(), zMin);
        lines.add(new Line(posInit, a));
        if (binaryTree.hasLeft() )
            makeLine(binaryTree.getLeft(),a,lines,zMin);

        if (binaryTree.hasRight() )
            makeLine(binaryTree.getRight(),a,lines, zMin);
    }

    //make lines
    private static void  addLine(float[]cloud, int index, Vec3D a, Col col){
        cloud[index] = (float) a.getX();
        cloud[index+1] = (float) a.getY();
        cloud[index+2] = (float) a.getZ();
        cloud[index+3] = 1;
        cloud[index+4] = (float)col.getR();
        cloud[index+5] = (float)col.getG();
        cloud[index+6] = (float)col.getB();
        cloud[index+7] = 1f;
    }

    public static OGLBuffers createBuffersTreeFromCSVrecord(BinaryTree<NodeNewick> binaryTree, Vec2D maxPosXY, float zMin, float thresh) {
        List<Line> lines = new ArrayList<>();
        makeLine(binaryTree,new Vec3D(), lines, zMin);
        int recSize = 16;
        float[] cloud = new float [(lines.size()+1)*recSize];
        for (int i = 0; i < lines.size(); i++) {
            Vec3D a = lines.get(i).a.mul(new Vec3D(1/maxPosXY.getX(),1/maxPosXY.getY(),1));
            Vec3D b = lines.get(i).b.mul(new Vec3D(1/maxPosXY.getX(),1/maxPosXY.getY(),1));
            Col col = new Col(0.5,0.5,0.5);

            addLine(cloud, recSize*i, a, col);
            addLine(cloud, recSize*i+recSize/2,b,col);

        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 4),
                new OGLBuffers.Attrib("inColor", 4) };

        //create geometry without index buffer as the point list
        OGLBuffers buffersTree = new OGLBuffers(cloud, attributes, null);

        System.out.println(buffersTree.toString());

        return buffersTree;
    }

    //make lines
    public static OGLBuffers createBuffersLineFromCSVRecord(List<Record> dataRecords,
                                                      List<Group> groups,
                                                      HashMap<String, NodeNewick> species,
                                                     int startIndexColor,
                                                     Vec2D maxPosXY, float zMin) {
        OGLBuffers buffersLine;
        int recSize = 16;
        float[] cloud = new float [dataRecords.size()*recSize];

        for (int i = 0; i < dataRecords.size(); i++) {
            Record r = dataRecords.get(i);

            String[] labels = r.name.split(" ");
            String label = labels[0];
            if (species.containsKey(label) ) {

                int id = species.get(label).groupId;
                if (groups.get(id).nodes.size()<=1) {
                    System.out.println(groups.get(id).center);
                    System.out.println(r.position);
                    continue;
                }
                r.groupID = id;
                Col col = IndexColor.getIndexColorCol(id+startIndexColor);
                r.color = col;
                cloud[recSize*i] = (float) r.position.getX();
                cloud[recSize*i+1] = (float) r.position.getY();
                cloud[recSize*i+2] = (float) r.position.getZ();
                //cloud[recSize*i] = (float) species.get(label).posXY.getX()/25;
                //cloud[recSize*i+1] = (float) species.get(label).posXY.getY()/250;
                //cloud[recSize*i+2] = 0f;
                cloud[recSize*i+3] = r.id;
                cloud[recSize*i+4] = (float)col.getR();
                cloud[recSize*i+5] = (float)col.getG();
                cloud[recSize*i+6] = (float)col.getB();
                cloud[recSize*i+7] = 1f;
                //System.out.println(id + ":" +groups.get(id).count);
                cloud[recSize*i+recSize/2] = (float) groups.get(id).center.getX();
                cloud[recSize*i+recSize/2+1] = (float) groups.get(id).center.getY();
                cloud[recSize*i+recSize/2+2] = (float) groups.get(id).center.getZ();
                cloud[recSize*i+recSize/2+3] = r.id;
                cloud[recSize*i+recSize/2+4] = (float)col.getR();
                cloud[recSize*i+recSize/2+5] = (float)col.getG();
                cloud[recSize*i+recSize/2+6] = (float)col.getB();
                cloud[recSize*i+recSize/2+7] = 1f;

            }

        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 4),
                new OGLBuffers.Attrib("inColor", 4) };

        //create geometry without index buffer as the point list
        buffersLine = new OGLBuffers(cloud, attributes, null);

        System.out.println(buffersLine.toString());

        return buffersLine;
    }

    //make points
    public static OGLBuffers createBuffersPointFromCSVrecord(List<Record> dataRecords,
                                                       List<Group> groups,
                                                       HashMap<String, NodeNewick> species,
                                                      int startIndexColor,
                                                      Vec2D maxPosXY, float zMin) {
        OGLBuffers buffersPoint;
        int recSize = 10;
        float[] cloud = new float [dataRecords.size()*recSize];

        for (int i = 0; i < dataRecords.size(); i++) {
            Record r = dataRecords.get(i);

            String[] labels = r.name.split(" ");
            String label = labels[0];
            if (species.containsKey(label) ) {

                int id = species.get(label).groupId;
                r.groupID = id;
                Col col = IndexColor.getIndexColorCol(id+startIndexColor);
                r.color = col;
                cloud[recSize*i] = (float) r.position.getX();
                cloud[recSize*i+1] = (float) r.position.getY();
                cloud[recSize*i+2] = (float) r.position.getZ();

                cloud[recSize*i+3] = r.id;
                cloud[recSize*i+4] = (float)col.getR();
                cloud[recSize*i+5] = (float)col.getG();
                cloud[recSize*i+6] = (float)col.getB();
                cloud[recSize*i+7] = (float) (species.get(label).posXY.getX()/maxPosXY.getX());
                cloud[recSize*i+8] = (float) (species.get(label).posXY.getY()/maxPosXY.getY());
                cloud[recSize*i+9] = zMin;

            }

        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 4),
                new OGLBuffers.Attrib("inColor", 3),
                new OGLBuffers.Attrib("inPositionXY", 3) };

        //create geometry without index buffer as the point list
        buffersPoint = new OGLBuffers(cloud, attributes, null);

        System.out.println(buffersPoint.toString());

        return buffersPoint;
    }

}
