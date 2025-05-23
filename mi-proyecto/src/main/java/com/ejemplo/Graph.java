package com.ejemplo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph implementation using adjacency matrix for directed graphs
 * Supports multiple weather conditions for edge weights
 */
public class Graph {
    private Map<String, Integer> cityIndex;
    private List<String> cities;
    private double[][][] adjacencyMatrix; // [from][to][weather_condition]
    private int numCities;
    private static final int MAX_CITIES = 100;
    
    // Weather condition constants
    public static final int NORMAL = 0;
    public static final int RAIN = 1;
    public static final int SNOW = 2;
    public static final int STORM = 3;
    public static final int NUM_WEATHER_CONDITIONS = 4;
    
    private static final double INFINITY = Double.MAX_VALUE;
    
    public Graph() {
        this.cityIndex = new HashMap<>();
        this.cities = new ArrayList<>();
        this.adjacencyMatrix = new double[MAX_CITIES][MAX_CITIES][NUM_WEATHER_CONDITIONS];
        this.numCities = 0;
        
        // Initialize matrix with infinity
        for (int i = 0; i < MAX_CITIES; i++) {
            for (int j = 0; j < MAX_CITIES; j++) {
                for (int k = 0; k < NUM_WEATHER_CONDITIONS; k++) {
                    adjacencyMatrix[i][j][k] = (i == j) ? 0 : INFINITY;
                }
            }
        }
    }
    
    /**
     * Add a city to the graph
     */
    public void addCity(String cityName) {
        if (!cityIndex.containsKey(cityName)) {
            cityIndex.put(cityName, numCities);
            cities.add(cityName);
            numCities++;
        }
    }
    
    /**
     * Add an edge between two cities with weather-specific weights
     */
    public void addEdge(String from, String to, double normalTime, 
                       double rainTime, double snowTime, double stormTime) {
        addCity(from);
        addCity(to);
        
        int fromIndex = cityIndex.get(from);
        int toIndex = cityIndex.get(to);
        
        adjacencyMatrix[fromIndex][toIndex][NORMAL] = normalTime;
        adjacencyMatrix[fromIndex][toIndex][RAIN] = rainTime;
        adjacencyMatrix[fromIndex][toIndex][SNOW] = snowTime;
        adjacencyMatrix[fromIndex][toIndex][STORM] = stormTime;
    }
    
    /**
     * Remove an edge between two cities
     */
    public void removeEdge(String from, String to) {
        if (cityIndex.containsKey(from) && cityIndex.containsKey(to)) {
            int fromIndex = cityIndex.get(from);
            int toIndex = cityIndex.get(to);
            
            for (int k = 0; k < NUM_WEATHER_CONDITIONS; k++) {
                adjacencyMatrix[fromIndex][toIndex][k] = INFINITY;
            }
        }
    }
    
    /**
     * Update weather condition for a specific edge
     */
    public void updateWeatherCondition(String from, String to, int weatherCondition, double time) {
        if (cityIndex.containsKey(from) && cityIndex.containsKey(to) && 
            weatherCondition >= 0 && weatherCondition < NUM_WEATHER_CONDITIONS) {
            int fromIndex = cityIndex.get(from);
            int toIndex = cityIndex.get(to);
            adjacencyMatrix[fromIndex][toIndex][weatherCondition] = time;
        }
    }
    
    /**
     * Get the adjacency matrix for a specific weather condition
     */
    public double[][] getAdjacencyMatrix(int weatherCondition) {
        double[][] matrix = new double[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                matrix[i][j] = adjacencyMatrix[i][j][weatherCondition];
            }
        }
        return matrix;
    }
    
    /**
     * Get city name by index
     */
    public String getCityName(int index) {
        return (index >= 0 && index < cities.size()) ? cities.get(index) : null;
    }
    
    /**
     * Get city index by name
     */
    public Integer getCityIndex(String cityName) {
        return cityIndex.get(cityName);
    }
    
    /**
     * Get number of cities
     */
    public int getNumCities() {
        return numCities;
    }
    
    /**
     * Get all city names
     */
    public List<String> getCities() {
        return new ArrayList<>(cities);
    }
    
    /**
     * Check if edge exists between two cities
     */
    public boolean hasEdge(String from, String to, int weatherCondition) {
        if (!cityIndex.containsKey(from) || !cityIndex.containsKey(to)) {
            return false;
        }
        int fromIndex = cityIndex.get(from);
        int toIndex = cityIndex.get(to);
        return adjacencyMatrix[fromIndex][toIndex][weatherCondition] != INFINITY;
    }
    
    /**
     * Get edge weight for specific weather condition
     */
    public double getEdgeWeight(String from, String to, int weatherCondition) {
        if (!cityIndex.containsKey(from) || !cityIndex.containsKey(to)) {
            return INFINITY;
        }
        int fromIndex = cityIndex.get(from);
        int toIndex = cityIndex.get(to);
        return adjacencyMatrix[fromIndex][toIndex][weatherCondition];
    }
    
    /**
     * Display adjacency matrix for a specific weather condition
     */
    public void displayMatrix(int weatherCondition) {
        String[] weatherNames = {"Normal", "Rain", "Snow", "Storm"};
        System.out.println("\nAdjacency Matrix (" + weatherNames[weatherCondition] + " weather):");
        
        // Print header
        System.out.print("\t\t");
        for (int j = 0; j < numCities; j++) {
            System.out.printf("%-12s", cities.get(j));
        }
        System.out.println();
        
        // Print matrix
        for (int i = 0; i < numCities; i++) {
            System.out.printf("%-12s", cities.get(i));
            for (int j = 0; j < numCities; j++) {
                double weight = adjacencyMatrix[i][j][weatherCondition];
                if (weight == INFINITY) {
                    System.out.printf("%-12s", "∞");
                } else {
                    System.out.printf("%-12.1f", weight);
                }
            }
            System.out.println();
        }
    }
}
