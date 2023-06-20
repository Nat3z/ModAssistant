package com.natia.downloadreplace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileReader {
    public static String readFile(File myObj) {
        try {
            Scanner myReader = new Scanner(myObj);
            StringBuilder dataRead = new StringBuilder();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                dataRead.append(data);
            }
            myReader.close();
            return dataRead.toString();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeFile(String fileName, String data) {
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(data);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
    }
}
