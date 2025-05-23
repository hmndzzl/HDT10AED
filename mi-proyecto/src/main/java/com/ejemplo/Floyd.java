package com.ejemplo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of Floyd-Warshall algorithm for finding shortest paths
 * between all pairs of vertices in a weighted graph
 */
public class Floyd {
    private double[][] distance;
    private int[][] next;
    private Graph graph;
    private int numCities;
    private static final double INFINITY = Double.MAX_VALUE;
    
    public Floyd(Graph graph) {
        this.graph = graph;
        this.numCities = graph.getNumCities();
        this.distance = new double[numCities][numCities];
        this.next = new int[numCities][numCities];
    }
    
    /**
     * Execute Floyd-Warshall algorithm for a specific weather condition
     */
    public void executeFloyd(int weatherCondition) {
        initializeMatrices(weatherCondition);
        
        // Floyd-Warshall main algorithm
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
     * Initialize distance and next matrices
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
     * Get shortest distance between two cities
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
     * Get shortest path between two cities
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
                return new ArrayList<>(); // No path exists
            }
            path.add(graph.getCityName(current));
        }
        
        return path;
    }
    
    /**
     * Calculate the center of the graph
     * The center is the vertex that minimizes the maximum distance to any other vertex
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
     * Display the distance matrix
     */
    public void displayDistanceMatrix() {
        System.out.println("\nShortest Distance Matrix:");
        
        // Print header
        System.out.print("\t\t");
        for (int j = 0; j < numCities; j++) {
            System.out.printf("%-12s", graph.getCityName(j));
        }
        System.out.println();
        
        // Print matrix
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
     * Get all shortest distances from a specific city
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
     * Check if there's a path between two cities
     */
    public boolean hasPath(String fromCity, String toCity) {
        return getShortestDistance(fromCity, toCity) != INFINITY;
    }
}