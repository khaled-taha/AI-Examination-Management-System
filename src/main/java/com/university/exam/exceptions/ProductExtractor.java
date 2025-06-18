package com.university.exam.exceptions;

import java.io.*;
import java.util.regex.*;

public class ProductExtractor {
    public static void main(String[] args) {
        File inputFile = new File("D:\\exam\\file2.txt");
        File outputFile = new File("D:\\exam\\products_report.txt");

        if (!inputFile.exists()) {
            System.out.println("File not found: " + inputFile.getAbsolutePath());
            return;
        }

        StringBuilder contentBuilder = new StringBuilder();

        // Read the input file
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            return;
        }

        String content = contentBuilder.toString();

        // Regular expression to extract productID, UPC, and availableQuantity
        Pattern pattern = Pattern.compile(
                "productID:([a-f0-9\\-]+).*?UPC:(\\d*).*?availableQuantity:(\\d+)",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(content);

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Extracted Product Data:");
            writer.println("==============================");

            boolean found = false;
            while (matcher.find()) {
                found = true;
                String productId = matcher.group(1);
                String upc = matcher.group(2);
                String availableQty = matcher.group(3);

                if(Integer.parseInt(availableQty) != 0) {
                    writer.println("Product ID: " + productId);
                    writer.println("UPC: " + upc);
                    writer.println("Available Quantity: " + availableQty);
                    writer.println();
                }
            }

            if (!found) {
                writer.println("No product entries found.");
            }

            System.out.println("Report generated successfully: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}
