package com.example.weathergadget;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class StagePositions {

    private double xPosition;
    private double yPosition;

    private Path localPath = FileSystems.getDefault().getPath("").toAbsolutePath();
    private File locationsFile = new File(localPath + File.separator + "WindowPosition.txt");

    public void writePositionsToFile(double xPos, double yPos){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(locationsFile))){
            writer.write("X:" + xPos + "\n");
            writer.write("Y:" + yPos + "\n");
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void initializeClassFieldValues(){
        try(BufferedReader reader = new BufferedReader(new FileReader(locationsFile))){
            String xStringValue = reader.readLine().replaceAll("\\p{Upper}:(.*)","$1");
            String yStringValue = reader.readLine().replaceAll("\\p{Upper}:(.*)","$1");

            xPosition = Double.parseDouble(xStringValue);
            yPosition = Double.parseDouble(yStringValue);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public double getXPosition(){
        return xPosition;
    }

    public double getYPosition(){
        return yPosition;
    }

}
