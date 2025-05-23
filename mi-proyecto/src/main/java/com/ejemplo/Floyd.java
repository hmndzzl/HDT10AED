package com.ejemplo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del algoritmo de Floyd para encontrar las rutas más cortas
 * entre todos los pares de vértices en un grafo ponderado.
 */
public class Floyd {
    private double[][] distance;
    private int[][] next;
    private Graph graph;
    private int numCities;
    private static final double INFINITY = Double.MAX_VALUE;
    
    /**
     * Constructor que inicializa el algoritmo con un grafo dado.
     * 
     * @param graph El grafo sobre el cual se ejecutará el algoritmo.
     */
    public Floyd(Graph graph) {
        this.graph = graph;
        this.numCities = graph.getNumCities();
        this.distance = new double[numCities][numCities];
        this.next = new int[numCities][numCities];
    }
    
    /**
     * Ejecuta el algoritmo de Floyd para una condición climática específica.
     * 
     * @param weatherCondition Condición climática para la cual se calcularán las rutas.
     */
    public void executeFloyd(int weatherCondition) {
        initializeMatrices(weatherCondition);
        
        // Algoritmo principal de Floyd
        for (int k = 0; k < numCities; k++) {
            for (int i = 0; i < numCities; i++) {
                for (int j = 0; j < numCities; j++) {
                    if (distance[i][k] != INFINITY && distance[k][j] != INFINITY) {
                        double newDistance = distance[i][k] + distance[k][j];
                        if (newDistance < distance[i][j]) {
                            distance[i][j] = newDistance;
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Inicializa las matrices de distancia y siguiente nodo.
     * 
     * @param weatherCondition Condición climática para inicializar la matriz de adyacencia.
     */
    private void initializeMatrices(int weatherCondition) {
        double[][] adjMatrix = graph.getAdjacencyMatrix(weatherCondition);
        
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                distance[i][j] = adjMatrix[i][j];
                if (i != j && adjMatrix[i][j] != INFINITY) {
                    next[i][j] = j;
                } else {
                    next[i][j] = -1;
                }
            }
        } 
    }
    /**
     * Obtiene la distancia más corta entre dos ciudades.
     * 
     * @param fromCity Ciudad de origen.
     * @param toCity Ciudad destino.
     * @return La distancia más corta o infinito si no existe ruta.
     */
    public double getShortestDistance(String fromCity, String toCity) {
        Integer fromIndex = graph.getCityIndex(fromCity);
        Integer toIndex = graph.getCityIndex(toCity);
        
        if (fromIndex == null || toIndex == null) {
            return INFINITY;
        }
        
        return distance[fromIndex][toIndex];
    }
    
    /**
     * Obtiene la ruta más corta entre dos ciudades.
     * 
     * @param fromCity Ciudad de origen.
     * @param toCity Ciudad destino.
     * @return Lista de nombres de ciudades que forman la ruta más corta.
     */
    public List<String> getShortestPath(String fromCity, String toCity) {
        Integer fromIndex = graph.getCityIndex(fromCity);
        Integer toIndex = graph.getCityIndex(toCity);
        
        if (fromIndex == null || toIndex == null || distance[fromIndex][toIndex] == INFINITY) {
            return new ArrayList<>();
        }
        
        List<String> path = new ArrayList<>();
        int current = fromIndex;
        path.add(graph.getCityName(current));
        
        while (current != toIndex) {
            current = next[current][toIndex];
            if (current == -1) {
                return new ArrayList<>(); // No existe ruta
            }
            path.add(graph.getCityName(current));
        }
        
        return path;
    }
    
    /**
     * Calcula el centro del grafo.
     * El centro es el vértice que minimiza la distancia máxima a cualquier otro vértice.
     * 
     * @return Nombre de la ciudad que es el centro del grafo o null si el grafo está vacío.
     */
    public String calculateGraphCenter() {
        if (numCities == 0) {
            return null;
        }
        
        double minMaxDistance = INFINITY;
        String centerCity = null;
        
        for (int i = 0; i < numCities; i++) {
            double maxDistance = 0;
            
            for (int j = 0; j < numCities; j++) {
                if (i != j && distance[i][j] != INFINITY) {
                    maxDistance = Math.max(maxDistance, distance[i][j]);
                }
            }
            
            if (maxDistance < minMaxDistance) {
                minMaxDistance = maxDistance;
                centerCity = graph.getCityName(i);
            }
        }
        
        return centerCity;
    }
    
    /**
     * Muestra la matriz de distancias más cortas.
     */
    public void displayDistanceMatrix() {
        System.out.println("\nMatriz de Distancias Más Cortas:");
        
        // Imprimir encabezado
        System.out.print("\t\t");
        for (int j = 0; j < numCities; j++) {
            System.out.printf("%-12s", graph.getCityName(j));
        }
        System.out.println();
        
        // Imprimir matriz
        for (int i = 0; i < numCities; i++) {
            System.out.printf("%-12s", graph.getCityName(i));
            for (int j = 0; j < numCities; j++) {
                if (distance[i][j] == INFINITY) {
                    System.out.printf("%-12s", "∞");
                } else {
                    System.out.printf("%-12.1f", distance[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Obtiene todas las distancias más cortas desde una ciudad específica.
     * 
     * @param fromCity Ciudad de origen.
     * @return Mapa con ciudades destino y sus distancias desde la ciudad de origen.
     */
    public Map<String, Double> getDistancesFrom(String fromCity) {
        Integer fromIndex = graph.getCityIndex(fromCity);
        if (fromIndex == null) {
            return new HashMap<>();
        }
        
        Map<String, Double> distances = new HashMap<>();
        for (int i = 0; i < numCities; i++) {
            if (i != fromIndex) {
                distances.put(graph.getCityName(i), distance[fromIndex][i]);
            }
        }
        
        return distances;
    }
    
    /**
     * Verifica si existe una ruta entre dos ciudades.
     * 
     * @param fromCity Ciudad de origen.
     * @param toCity Ciudad destino.
     * @return true si existe ruta, false en caso contrario.
     */
    public boolean hasPath(String fromCity, String toCity) {
        return getShortestDistance(fromCity, toCity) != INFINITY;
    }
}
