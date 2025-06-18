package com.university.exam.exceptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class ProductUPCExtractor {
    public static void main(String[] args) {
        Map<BadHash, String> map = new HashMap<>();

        map.put(new BadHash(1), "Value 1");
        map.put(new BadHash(2), "Value 2");
        map.put(new BadHash(3), "Value 3");

        System.out.println(map); // All entries are stored in the same bucket
    }

    private static String extractProductInfo(String jsonContent) {
        StringBuilder output = new StringBuilder();
        Pattern pattern = Pattern.compile(
                "\\{[^{}]*\\\"UPC\\\":\\\"?(\\d+)\\\"?,.*?\\\"productName\\\":\\\"?([^,\\\"]+)\\\"?,.*?" +
                        "\\\"quantity\\\":(\\d+),.*?\\\"fromStoreId\\\":\\{[^{}]*\\\"name\\\":\\\"([^\\\"]+)\\\".*?" +
                        "\\\"toStoreId\\\":\\{[^{}]*\\\"name\\\":\\\"([^\\\"]+)\\\".*?" +
                        "\\\"st_productExpirationDate\\\":\\\"([^\\\"]+)\\\".*?" +
                        "\\\"standardUnitPrice\\\":([\\d.]+).*?\\\"VAT\\\":([\\d.]+).*?" +
                        "\\\"availableQuantity\\\":(\\d+).*?\\\"toProductId\\\":\\{[^{}]*\\\"availableQuantity\\\":(\\d+)"
        );

        Matcher matcher = pattern.matcher(jsonContent.replaceAll("\\s+", " "));

        int itemCount = 1;
        while (matcher.find()) {
            String upc = matcher.group(1);
            String productName = matcher.group(2).trim();
            String quantity = matcher.group(3);
            String fromStore = matcher.group(4);
            String toStore = matcher.group(5);
            String expirationDate = formatDate(matcher.group(6));
            String unitPrice = matcher.group(7);
            String vat = matcher.group(8);
            String sourceQuantity = matcher.group(9);
            String destQuantity = matcher.group(10);

            output.append(String.format(
                    "Item %d:\n" +
                            "Product: %s\n\n" +
                            "UPC: %s\n\n" +
                            "Quantity: %s\n\n" +
                            "From Store: %s\n\n" +
                            "To Store: %s\n\n" +
                            "Expiration Date: %s\n\n" +
                            "Unit Price: SAR %s (VAT %s%%)\n\n" +
                            "Current Available Quantity at Source: %s\n\n" +
                            "Destination Available Quantity: %s\n" +
                            "============================\n",
                    itemCount++,
                    productName,
                    upc,
                    quantity,
                    fromStore,
                    toStore,
                    expirationDate,
                    unitPrice,
                    String.format("%.0f", Double.parseDouble(vat) * 100),
                    sourceQuantity,
                    destQuantity
            ));
        }

        return output.toString();
    }

    private static String formatDate(String dateStr) {
        try {
            // Handle different date formats in the input
            SimpleDateFormat inputFormat;
            if (dateStr.contains("GMT+03:00")) {
                inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT+03:00' yyyy");
            } else if (dateStr.contains("GMT")) {
                inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy");
            } else {
                return dateStr; // return as-is if format not recognized
            }

            Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MMM dd yyyy");
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr; // return original if parsing fails
        }
    }

    static class BadHash {
        int value;

        BadHash(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return 1; // BAD: Always returns the same hash code (causes collisions)
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BadHash badHash = (BadHash) obj;
            return value == badHash.value;
        }

        @Override
        public String toString() {
            return String.format("BadHash{value=%d}", value);
        }
    }

}
