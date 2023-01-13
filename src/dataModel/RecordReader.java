package dataModel;

import lwjglutils.ShaderUtils;
import transforms.Point3D;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecordReader {
    public static List<Record> readCSV(String fileName){
        List<Record> records = new ArrayList<>();
        InputStream is = ShaderUtils.class.getResourceAsStream(fileName);
        if (is == null) {
            System.out.println("File not found ");
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        int index = 0;
        try {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                //records.add(Arrays.asList(values));
                Record record = new Record();
                record.position = new Point3D(Float.parseFloat(values[0].replace(',', '.').trim()),
                        Float.parseFloat(values[1].replace(',', '.').trim()),
                        Float.parseFloat(values[2].replace(',', '.').trim()));

                if(values.length>4) {
                    record.name = values[4].trim().toLowerCase();
                } else
                    record.name = "";
                if(values.length>5) {
                    record.groupNumber = Integer.parseInt(values[5]);
                } else
                    record.groupNumber = 0;

                record.abr = values[3].trim();
                record.id = index;
                records.add(record);
                index++;
            }
        } catch (IOException e) {
            System.err.println("Read error in ");
            e.printStackTrace();
        }
        return records;
    }
}
