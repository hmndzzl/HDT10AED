package com.ejemplo;

import java.io.*;
import java.util.Scanner;

/**
 * Handles file operations for reading and writing graph data
 */
public class FileHandler {
    
    /**
     * Read graph data from file
     * File format: ciudad1 ciudad2 tiempoNormal tiempoLluvia tiempoNieve tiempoTormenta
     */
    public static Graph readGraphFromFile(String filename) throws IOException {
        Graph graph = new Graph();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    String[] parts = line.split("\\s+");
                    
                    if (parts.length != 6) {
                        System.err.println("Warning: Line " + lineNumber + 
                            " has incorrect format. Expected 6 values, found " + parts.length);
                        continue;
                    }
                    
                    String city1 = parts[0];
                    String city2 = parts[1];
                    double normalTime = Double.parseDouble(parts[2]);
                    double rainTime = Double.parseDouble(parts[3]);
                    double snowTime = Double.parseDouble(parts[4]);
                    double stormTime = Double.parseDouble(parts[5]);
                    
                    graph.addEdge(city1, city2, normalTime, rainTime, snowTime, stormTime);
                    
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Line " + lineNumber + 
                        " contains invalid number format: " + line);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Create a sample logistica.txt file for testing
     */
    public static void createSampleFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("BuenosAires SaoPaulo 10 15 20 50");
            writer.println("BuenosAires Lima 15 20 30 70");
            writer.println("Lima Quito 10 12 15 20");
            writer.println("SaoPaulo Lima 8 10 12 25");
            writer.println("SaoPaulo Quito 20 25 30 60");
            writer.println("Quito Bogota 5 8 10 15");
            writer.println("Lima Bogota 12 15 18 35");
            writer.println("Bogota Caracas 8 10 12 20");
            writer.println("SaoPaulo Caracas 25 30 35 70");
            writer.println("BuenosAires Montevideo 3 4 5 8");
            writer.println("Montevideo SaoPaulo 12 15 18 30");
        }
        
        System.out.println("Sample file '" + filename + "' created successfully.");
    }
    
    /**
     * Save graph data to file
     */
    public static void saveGraphToFile(Graph graph, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String city1 : graph.getCities()) {
                for (String city2 : graph.getCities()) {
                    if (!city1.equals(city2) && graph.hasEdge(city1, city2, Graph.NORMAL)) {
                        double normalTime = graph.getEdgeWeight(city1, city2, Graph.NORMAL);
                        double rainTime = graph.getEdgeWeight(city1, city2, Graph.RAIN);
                        double snowTime = graph.getEdgeWeight(city1, city2, Graph.SNOW);
                        double stormTime = graph.getEdgeWeight(city1, city2, Graph.STORM);
                        
                        writer.printf("%s %s %.1f %.1f %.1f %.1f%n", 
                            city1, city2, normalTime, rainTime, snowTime, stormTime);
                    }
                }
            }
        }```java
/**
 * Read graph data from file
 * File format: ciudad1 ciudad2 tiempoNormal tiempoLluvia tiempoNieve tiempoTormenta
 */
public static Graph readGraphFromFile(String filename) throws IOException {
    Graph graph = new Graph();
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        int lineNumber = 0;
        
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            
            // Skip empty lines
            if (line.isEmpty()) {
                continue;
            }
            
            try {
                String[] parts = line.split("\\s+");
                
                if (parts.length != 6) {
                    System.err.println("Warning: Line " + lineNumber + 
                        " has incorrect format. Expected 6 values, found " + parts.length);
                    continue;
                }
                
                String city1 = parts[0];
                String city2 = parts[1];
                double normalTime = Double.parseDouble(parts[2]);
                double rainTime = Double.parseDouble(parts[3]);
                double snowTime = Double.parseDouble(parts[4]);
                double stormTime = Double.parseDouble(parts[5]);
                
                // Validate city names
                if (city1.isEmpty() || city2.isEmpty()) {
                    System.err.println("Warning: Line " + lineNumber + 
                        " contains empty city name: " + line);
                    continue;
                }
                
                // Validate time values
                if (normalTime < 0 || rainTime < 0 || snowTime < 0 || stormTime < 0) {
                    System.err.println("Warning: Line " + lineNumber + 
                        " contains negative time value: " + line);
                    continue;
                }
                
                graph.addEdge(city1, city2, normalTime, rainTime, snowTime, stormTime);
                
            } catch (NumberFormatException e) {
                System.err.println("Warning: Line " + lineNumber + 
                    " contains invalid number format: " + line);
            }
        }
    }
    
    return graph;
}

/**
 * Save graph data to file
 */
public static void saveGraphToFile(Graph graph, String filename) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        for (String city1 : graph.getCities()) {
            for (String city2 : graph.getCities()) {
                if (!city1.equals(city2) && graph.hasEdge(city1, city2, Graph.NORMAL)) {
                    double normalTime = graph.getEdgeWeight(city1, city2, Graph.NORMAL);
                    double rainTime = graph.getEdgeWeight(city1, city2, Graph.RAIN);
                    double snowTime = graph.getEdgeWeight(city1, city2, Graph.SNOW);
                    double stormTime = graph.getEdgeWeight(city1, city2, Graph.STORM);
                    
                    // Format time values to one decimal place
                    writer.printf("%s %s %.1f %.1f %.1f %.1f%n", 
                        city1, city2, normalTime, rainTime, snowTime, stormTime);
                }
            }
        }
    }
    
    System.out.println("Graph saved to '" + filename + "' successfully.");
}

/**
 * Create a sample logistica.txt file for testing
 */
public static void createSampleFile(String filename) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        writer.println("BuenosAires SaoPaulo 10.0 15.0 20.0 50.0");
        writer.println("BuenosAires Lima 15.0 20.0 30.0 70.0");
        writer.println("Lima Quito 10.0 12.0 15.0 20.0");
        writer.println("SaoPaulo Lima 8.0 10.0 12.0 25.0");
        writer.println("SaoPaulo Quito 20.0 25.0 30.0 60.0");
        writer.println("Quito Bogota 5.0 8.0 10.0 15.0");
        writer.println("Lima Bogota 12.0 15.0 18.0 35.0");
        writer.println("Bogota Caracas 8.0 10.0 12.0 20.0");
        writer.println("SaoPaulo Caracas 25.0 30.0 35.0 70.0");
        writer.println("BuenosAires Montevideo 3.0 4.0 5.0 8.0");
        writer.println("Montevideo SaoPaulo 12.0 15.0 18.0 30.0");
    }
    
    System.out.println("Sample file '" + filename + "' created successfully.");
}
```
        
        System.out.println("Graph saved to '" + filename + "' successfully.");
    }
    
    /**
     * Display file reading instructions
     */
    public static void displayFileFormat() {
        System.out.println("\nFile format for logistica.txt:");
        System.out.println("Each line should contain:");
        System.out.println("Ciudad1 Ciudad2 tiempoNormal tiempoLluvia tiempoNieve tiempoTormenta");
        System.out.println("\nExample:");
        System.out.println("BuenosAires SaoPaulo 10 15 20 50");
        System.out.println("BuenosAires Lima 15 20 30 70");
        System.out.println("Lima Quito 10 12 15 20");
        System.out.println("\nNote: City names should not contain spaces.");
    }
}
