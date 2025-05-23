package com.ejemplo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Maneja las operaciones de archivos para la lectura y escritura de datos del grafo.
 */
public class FileHandler {
    
    /**
     * Lee los datos del grafo desde un archivo.
     * Formato del archivo: ciudad1 ciudad2 tiempoNormal tiempoLluvia tiempoNieve tiempoTormenta
     * 
     * @param filename Nombre del archivo desde donde se leerán los datos.
     * @return Un objeto Graph con los datos cargados.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static Graph readGraphFromFile(String filename) throws IOException {
        Graph graph = new Graph();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Omitir líneas vacías
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    String[] parts = line.split("\\s+");
                    
                    if (parts.length != 6) {
                        System.err.println("Advertencia: Línea " + lineNumber + 
                            " tiene formato incorrecto. Se esperaban 6 valores, se encontraron " + parts.length);
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
                    System.err.println("Advertencia: Línea " + lineNumber + 
                        " contiene formato numérico inválido: " + line);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Crea un archivo de ejemplo 'logistica.txt' para pruebas.
     * 
     * @param filename Nombre del archivo a crear.
     * @throws IOException Si ocurre un error al escribir el archivo.
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
        
        System.out.println("Archivo de ejemplo '" + filename + "' creado exitosamente.");
    }
    
    /**
     * Guarda los datos del grafo en un archivo.
     * 
     * @param graph El grafo cuyos datos se guardarán.
     * @param filename Nombre del archivo donde se guardarán los datos.
     * @throws IOException Si ocurre un error al escribir el archivo.
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
        }
        
        System.out.println("Grafo guardado en '" + filename + "' exitosamente.");
    }
    
    /**
     * Muestra las instrucciones del formato del archivo.
     */
    public static void displayFileFormat() {
        System.out.println("\nFormato del archivo para logistica.txt:");
        System.out.println("Cada línea debe contener:");
        System.out.println("Ciudad1 Ciudad2 tiempoNormal tiempoLluvia tiempoNieve tiempoTormenta");
        System.out.println("\nEjemplo:");
        System.out.println("BuenosAires SaoPaulo 10 15 20 50");
        System.out.println("BuenosAires Lima 15 20 30 70");
        System.out.println("Lima Quito 10 12 15 20");
        System.out.println("\nNota: Los nombres de las ciudades no deben contener espacios.");
    }
}
