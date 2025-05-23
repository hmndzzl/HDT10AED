package com.ejemplo;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main program for logistics routing system using Floyd-Warshall algorithm
 */
public class Main {
    private Graph graph;
    private Floyd floyd;
    private Scanner scanner;
    private static final String DEFAULT_FILENAME = "logistica.txt";
    
    public Main() {
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        Main system = new Main();
        system.run();
    }
    
    /**
     * Main program loop
     */
    public void run() {
        System.out.println("=== Logistics Routing System ===");
        System.out.println("Floyd-Warshall Algorithm Implementation");
        System.out.println("=====================================\n");
        
        // Initialize the system
        if (!initializeSystem()) {
            return;
        }
        
        // Main menu loop
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getMenuChoice();
            
            switch (choice) {
                case 1:
                    findShortestRoute();
                    break;
                case 2:
                    displayGraphCenter();
                    break;
                case 3:
                    modifyGraph();
                    break;
                case 4:
                    displayGraphInfo();
                    break;
                case 5:
                    running = false;
                    System.out.println("¡Gracias por usar el sistema de logística!");
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, seleccione 1-5.");
            }
            
            if (running) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    /**
     * Initialize the system by loading graph data
     */
    private boolean initializeSystem() {
        try {
            // Try to load existing file
            try {
                graph = FileHandler.readGraphFromFile(DEFAULT_FILENAME);
                System.out.println("Archivo '" + DEFAULT_FILENAME + "' cargado exitosamente.");
            } catch (IOException e) {
                System.out.println("No se pudo cargar '" + DEFAULT_FILENAME + "'.");
                System.out.print("¿Desea crear un archivo de ejemplo? (s/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                
                if (response.equals("s") || response.equals("si") || response.equals("y") || response.equals("yes")) {
                    FileHandler.createSampleFile(DEFAULT_FILENAME);
                    graph = FileHandler.readGraphFromFile(DEFAULT_FILENAME);
                } else {
                    System.out.println("No se puede continuar sin datos de grafo.");
                    FileHandler.displayFileFormat();
                    return false;
                }
            }
            
            if (graph.getNumCities() == 0) {
                System.out.println("Error: El grafo no contiene ciudades válidas.");
                return false;
            }
            
            // Initialize Floyd-Warshall algorithm
            floyd = new Floyd(graph);
            floyd.executeFloyd(Graph.NORMAL); // Use normal weather by default
            
            System.out.println("Sistema inicializado con " + graph.getNumCities() + " ciudades.");
            System.out.println("Ciudades disponibles: " + graph.getCities());
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al inicializar el sistema: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Display main menu
     */
    private void displayMenu() {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Encontrar ruta más corta entre ciudades");
        System.out.println("2. Mostrar centro del grafo");
        System.out.println("3. Modificar grafo");
        System.out.println("4. Mostrar información del grafo");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción (1-5): ");
    }
    
    /**
     * Get menu choice from user
     */
    private int getMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Find and display shortest route between two cities
     */
    private void findShortestRoute() {
        System.out.println("\n=== ENCONTRAR RUTA MÁS CORTA ===");
        System.out.println("Ciudades disponibles: " + graph.getCities());
        
        System.out.print("Ingrese ciudad origen: ");
        String fromCity = scanner.nextLine().trim();
        
        System.out.print("Ingrese ciudad destino: ");
        String toCity = scanner.nextLine().trim();
        
        if (!graph.getCities().contains(fromCity)) {
            System.out.println("Error: Ciudad origen '" + fromCity + "' no encontrada.");
            return;
        }
        
        if (!graph.getCities().contains(toCity)) {
            System.out.println("Error: Ciudad destino '" + toCity + "' no encontrada.");
            return;
        }
        
        double distance = floyd.getShortestDistance(fromCity, toCity);
        List<String> path = floyd.getShortestPath(fromCity, toCity);
        
        if (distance == Double.MAX_VALUE || path.isEmpty()) {
            System.out.println("No existe ruta entre " + fromCity + " y " + toCity);
        } else {
            System.out.println("\nRuta más corta encontrada:");
            System.out.printf("Distancia total: %.1f horas\n", distance);
            System.out.println("Ruta: " + String.join(" -> ", path));
            
            // Show intermediate cities
            if (path.size() > 2) {
                System.out.println("Ciudades intermedias: " + 
                    String.join(", ", path.subList(1, path.size() - 1)));
            }
        }
    }
    
    /**
     * Display graph center
     */
    private void displayGraphCenter() {
        System.out.println("\n=== CENTRO DEL GRAFO ===");
        String center = floyd.calculateGraphCenter();
        
        if (center != null) {
            System.out.println("El centro del grafo es: " + center);
            
            // Show distances from center to all other cities
            System.out.println("\nDistancias desde el centro:");
            var distances = floyd.getDistancesFrom(center);
            for (var entry : distances.entrySet()) {
                if (entry.getValue() != Double.MAX_VALUE) {
                    System.out.printf("%s -> %s: %.1f horas\n", 
                        center, entry.getKey(), entry.getValue());
                }
            }
        } else {
            System.out.println("No se pudo determinar el centro del grafo.");
        }
    }
    
    /**
     * Modify graph (add/remove edges, change weather conditions)
     */
    private void modifyGraph() {
        System.out.println("\n=== MODIFICAR GRAFO ===");
        System.out.println("1. Interrumpir tráfico entre ciudades");
        System.out.println("2. Establecer nueva conexión");
        System.out.println("3. Cambiar condición climática");
        System.out.print("Seleccione opción (1-3): ");
        
        int choice = getMenuChoice();
        
        switch (choice) {
            case 1:
                interruptTraffic();
                break;
            case 2:
                addNewConnection();
                break;
            case 3:
                changeWeatherCondition();
                break;
            default:
                System.out.println("Opción inválida.");
                return;
        }
        
        // Recalculate shortest paths and center
        floyd.executeFloyd(Graph.NORMAL);
        System.out.println("Rutas recalculadas exitosamente.");
    }
    
    /**
     * Interrupt traffic between two cities
     */
    private void interruptTraffic() {
        System.out.println("Ciudades disponibles: " + graph.getCities());
        System.out.print("Ciudad origen: ");
        String from = scanner.nextLine().trim();
        System.out.print("Ciudad destino: ");
        String to = scanner.nextLine().trim();
        
        if (graph.getCities().contains(from) && graph.getCities().contains(to)) {
            graph.removeEdge(from, to);
            System.out.println("Tráfico interrumpido entre " + from + " y " + to);
        } else {
            System.out.println("Una o ambas ciudades no existen.");
        }
    }
    
    /**
     * Add new connection between cities
     */
    private void addNewConnection() {
        System.out.println("Ciudades disponibles: " + graph.getCities());
        System.out.print("Ciudad origen: ");
        String from = scanner.nextLine().trim();
        System.out.print("Ciudad destino: ");
        String to = scanner.nextLine().trim();
        
        try {
            System.out.print("Tiempo con clima normal: ");
            double normalTime = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Tiempo con lluvia: ");
            double rainTime = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Tiempo con nieve: ");
            double snowTime = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Tiempo con tormenta: ");
            double stormTime = Double.parseDouble(scanner.nextLine().trim());
            
            graph.addEdge(from, to, normalTime, rainTime, snowTime, stormTime);
            System.out.println("Conexión establecida entre " + from + " y " + to);
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese valores numéricos válidos.");
        }
    }
    
    /**
     * Change weather condition for existing connection
     */
    private void changeWeatherCondition() {
        System.out.println("Ciudades disponibles: " + graph.getCities());
        System.out.print("Ciudad origen: ");
        String from = scanner.nextLine().trim();
        System.out.print("Ciudad destino: ");
        String to = scanner.nextLine().trim();
        
        if (!graph.getCities().contains(from) || !graph.getCities().contains(to)) {
            System.out.println("Una o ambas ciudades no existen.");
            return;
        }
        
        System.out.println("Condiciones climáticas:");
        System.out.println("1. Normal");
        System.out.println("2. Lluvia");
        System.out.println("3. Nieve");
        System.out.println("4. Tormenta");
        System.out.print("Seleccione condición (1-4): ");
        
        int weatherChoice = getMenuChoice();
        if (weatherChoice < 1 || weatherChoice > 4) {
            System.out.println("Opción inválida.");
            return;
        }
        
        try {
            System.out.print("Nuevo tiempo de viaje: ");
            double newTime = Double.parseDouble(scanner.nextLine().trim());
            
            graph.updateWeatherCondition(from, to, weatherChoice - 1, newTime);
            System.out.println("Condición climática actualizada.");
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un valor numérico válido.");
        }
    }
    
    /**
     * Display graph information
     */
    private void displayGraphInfo() {
        System.out.println("\n=== INFORMACIÓN DEL GRAFO ===");
        System.out.println("Número de ciudades: " + graph.getNumCities());
        System.out.println("Ciudades: " + graph.getCities());
        
        // Display adjacency matrix for normal weather
        graph.displayMatrix(Graph.NORMAL);
        
        // Display shortest distance matrix
        floyd.displayDistanceMatrix();
        
        // Save current state
        try {
            FileHandler.saveGraphToFile(graph, "logistica_current.txt");
            System.out.println("Estado actual guardado en 'logistica_current.txt'");
        } catch (IOException e) {
            System.out.println("Error al guardar el estado actual: " + e.getMessage());
        }
    }
}