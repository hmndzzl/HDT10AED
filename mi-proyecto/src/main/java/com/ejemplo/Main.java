// Algotrimos Y Estructuras de Datos
// Sección 10
// Hugo Méndez - 241265
// Arodi Chávez - 241112

// Hoja De Trabajo 10 - Algoritmo de Floyd
package com.ejemplo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Programa principal para el sistema de rutas logísticas usando el algoritmo de Floyd.
 */
public class Main {
    private Graph graph;
    private Floyd floyd;
    private Scanner scanner;
    private static final String DEFAULT_FILENAME = "logistica.txt";
    
    /**
     * Constructor que inicializa el escáner para entrada de usuario.
     */
    public Main() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Método principal que inicia la ejecución del programa.
     * 
     * @param args Argumentos de línea de comandos (no usados).
     */
    public static void main(String[] args) {
        Main system = new Main();
        system.run();
    }
    
    /**
     * Bucle principal del programa.
     */
    public void run() {
        System.out.println("=== Sistema de Rutas Logísticas ===");
        System.out.println("Implementación del Algoritmo Floyd");
        System.out.println("=====================================\n");
        
        // Inicializa el sistema
        if (!initializeSystem()) {
            return;
        }
        
        // Bucle del menú principal
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
     * Inicializa el sistema cargando los datos del grafo.
     * 
     * @return true si la inicialización fue exitosa, false en caso contrario.
     */
    private boolean initializeSystem() {
        try {
            // Intenta cargar archivo existente
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
            
            // Inicializa el algoritmo Floyd
            floyd = new Floyd(graph);
            floyd.executeFloyd(Graph.NORMAL); // Usa clima normal por defecto
            
            System.out.println("Sistema inicializado con " + graph.getNumCities() + " ciudades.");
            System.out.println("Ciudades disponibles: " + graph.getCities());
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al inicializar el sistema: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Muestra el menú principal.
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
     * Obtiene la opción del menú ingresada por el usuario.
     * 
     * @return Número de opción o -1 si la entrada es inválida.
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
     * Encuentra y muestra la ruta más corta entre dos ciudades.
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
            
            // Mostrar ciudades intermedias
            if (path.size() > 2) {
                System.out.println("Ciudades intermedias: " + 
                    String.join(", ", path.subList(1, path.size() - 1)));
            }
        }
    }
    
    /**
     * Muestra el centro del grafo.
     */
    private void displayGraphCenter() {
        System.out.println("\n=== CENTRO DEL GRAFO ===");
        String center = floyd.calculateGraphCenter();
        
        if (center != null) {
            System.out.println("El centro del grafo es: " + center);
            
            // Mostrar distancias desde el centro a todas las demás ciudades
            System.out.println("\nDistancias desde el centro:");
            Map<String, Double> distances = floyd.getDistancesFrom(center);
            for (Map.Entry<String, Double> entry : distances.entrySet()) {
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
     * Modifica el grafo (agregar/eliminar aristas, cambiar condiciones climáticas).
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
        
        // Recalcula rutas y centro
        floyd.executeFloyd(Graph.NORMAL);
        System.out.println("Rutas recalculadas exitosamente.");
    }
    
    /**
     * Interrumpe el tráfico entre dos ciudades.
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
     * Establece una nueva conexión entre ciudades.
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
     * Cambia la condición climática para una conexión existente.
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
     * Muestra la información del grafo.
     */
    private void displayGraphInfo() {
        System.out.println("\n=== INFORMACIÓN DEL GRAFO ===");
        System.out.println("Número de ciudades: " + graph.getNumCities());
        System.out.println("Ciudades: " + graph.getCities());
        
        // Muestra la matriz de adyacencia para clima normal
        graph.displayMatrix(Graph.NORMAL);
        
        // Muestra la matriz de distancias más cortas
        floyd.displayDistanceMatrix();
        
        // Guarda el estado actual
        try {
            FileHandler.saveGraphToFile(graph, "logistica_current.txt");
            System.out.println("Estado actual guardado en 'logistica_current.txt'");
        } catch (IOException e) {
            System.out.println("Error al guardar el estado actual: " + e.getMessage());
        }
    }
}
