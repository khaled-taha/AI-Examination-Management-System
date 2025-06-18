package com.university.exam.exceptions;

import java.io.*;
import java.util.regex.*;

public class InventoryReportGenerator {

    public static void main(String[] args) {

        File inputFile = new File("D:\\exam\\file.txt");
        File outputFile = new File("D:\\exam\\report.txt");


        if (!inputFile.exists()) {
            System.out.println("Input file not found: " + inputFile.getAbsolutePath());
            return;
        }

        StringBuilder contentBuilder = new StringBuilder();

        // Read entire file into a string
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        String content = contentBuilder.toString();

        // Regex pattern to extract required fields including availableQuantity
        Pattern productPattern = Pattern.compile(
                "productName:\"?(.*?)\"?.*?availableQuantity:(\\d+).*?change:(\\d+),difference:(\\d+),damaged:(\\d+),expired:(\\d+)",
                Pattern.DOTALL
        );
        Matcher matcher = productPattern.matcher(content);

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Inventory Report:");
            writer.println("===================================");

            boolean found = false;
            while (matcher.find()) {
                found = true;
                String productName = matcher.group(1).trim();
                String availableQuantity = matcher.group(2);
                String change = matcher.group(3);
                String difference = matcher.group(4);
                String damaged = matcher.group(5);
                String expired = matcher.group(6);

                if((Integer.parseInt(damaged) != 0 || Integer.parseInt(expired) != 0 || Integer.parseInt(difference) != 0 || Integer.parseInt(change) != 0 || Integer.parseInt(availableQuantity) != 0)) {

                    writer.println("Product Name: " + productName);
                    writer.println("Available Quantity: " + availableQuantity);
                    writer.println("Change: " + change + ", Difference: " + difference +
                            ", Damaged: " + damaged + ", Expired: " + expired);
                    writer.println();
                }
            }

            if (!found) {
                writer.println("No matching products found in the input.");
            }

            System.out.println("Report has been generated in: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing the report file: " + e.getMessage());
        }
    }
}


