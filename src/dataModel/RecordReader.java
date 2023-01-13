package dataModel;

import transforms.Point3D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordReader {
    public static List<Record> readCSV(String fileName){
        List<Record> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int index = 0;
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
            e.printStackTrace();
        }
        return records;
    }
}
