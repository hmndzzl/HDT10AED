package com.ejemplo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de un grafo dirigido usando matriz de adyacencia.
 * Soporta múltiples condiciones climáticas para los pesos de las aristas.
 */
public class Graph {
    private Map<String, Integer> cityIndex;
    private List<String> cities;
    private double[][][] adjacencyMatrix; // [origen][destino][condición_climática]
    private int numCities;
    private static final int MAX_CITIES = 100;
    
    // Constantes para condiciones climáticas
    public static final int NORMAL = 0;
    public static final int RAIN = 1;
    public static final int SNOW = 2;
    public static final int STORM = 3;
    public static final int NUM_WEATHER_CONDITIONS = 4;
    
    private static final double INFINITY = Double.MAX_VALUE;
    
    /**
     * Constructor que inicializa el grafo.
     */
    public Graph() {
        this.cityIndex = new HashMap<>();
        this.cities = new ArrayList<>();
        this.adjacencyMatrix = new double[MAX_CITIES][MAX_CITIES][NUM_WEATHER_CONDITIONS];
        this.numCities = 0;
        
        // Inicializa la matriz con infinito
        for (int i = 0; i < MAX_CITIES; i++) {
            for (int j = 0; j < MAX_CITIES; j++) {
                for (int k = 0; k < NUM_WEATHER_CONDITIONS; k++) {
                    adjacencyMatrix[i][j][k] = (i == j) ? 0 : INFINITY;
                }
            }
        }
    }
    
    /**
     * Agrega una ciudad al grafo.
     * 
     * @param cityName Nombre de la ciudad a agregar.
     */
    public void addCity(String cityName) {
        if (!cityIndex.containsKey(cityName)) {
            cityIndex.put(cityName, numCities);
            cities.add(cityName);
            numCities++;
        }
    }
    
    /**
     * Agrega una arista entre dos ciudades con pesos específicos para cada condición climática.
     * 
     * @param from Ciudad de origen.
     * @param to Ciudad destino.
     * @param normalTime Tiempo con clima normal.
     * @param rainTime Tiempo con lluvia.
     * @param snowTime Tiempo con nieve.
     * @param stormTime Tiempo con tormenta.
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
     * Elimina una arista entre dos ciudades.
     * 
     * @param from Ciudad de origen.
     * @param to Ciudad destino.
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
     * Actualiza la condición climática para una arista específica.
     * 
     * @param from Ciudad de origen.
     * @param to Ciudad destino.
     * @param weatherCondition Condición climática a actualizar.
     * @param time Nuevo tiempo para la condición climática.
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
     * Obtiene la matriz de adyacencia para una condición climática específica.
     * 
     * @param weatherCondition Condición climática.
     * @return Matriz de adyacencia correspondiente.
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
     * Obtiene el nombre de la ciudad por índice.
     * 
     * @param index Índice de la ciudad.
     * @return Nombre de la ciudad o null si el índice es inválido.
     */
    public String getCityName(int index) {
        return (index >= 0 && index < cities.size()) ? cities.get(index) : null;
    }
    
    /**
     * Obtiene el índice de la ciudad por nombre.
     * 
     * @param cityName Nombre de la ciudad.
     * @return Índice de la ciudad o null si no existe.
     */
    public Integer getCityIndex(String cityName) {
        return cityIndex.get(cityName);
    }
    
    /**
     * Obtiene el número de ciudades en el grafo.
     * 
     * @return Número de ciudades.
     */
    public int getNumCities() {
        return numCities;
    }
    
    /**
     * Obtiene la lista de todas las ciudades.
     * 
     * @return Lista de nombres de ciudades.
     */
    public List<String> getCities() {
        return new ArrayList<>(cities);
    }
    
    /**
     * Verifica si existe una arista entre dos ciudades para una condición climática dada.
     * 
     * @param from Ciudad de origen.
     * @param to Ciudad destino.
     * @param weatherCondition Condición climática.
     * @return true si existe la arista, false en caso contrario.
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
     * Obtiene el peso de la arista para una condición climática específica.
     * 
     * @param from Ciudad de origen.
     * @param to Ciudad destino.
     * @param weatherCondition Condición climática.
     * @return Peso de la arista o infinito si no existe.
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
     * Muestra la matriz de adyacencia para una condición climática específica.
     * 
     * @param weatherCondition Condición climática.
     */
    public void displayMatrix(int weatherCondition) {
        String[] weatherNames = {"Normal", "Lluvia", "Nieve", "Tormenta"};
        System.out.println("\nMatriz de Adyacencia (clima: " + weatherNames[weatherCondition] + "):");
        
        // Imprimir encabezado
        System.out.print("\t\t");
        for (int j = 0; j < numCities; j++) {
            System.out.printf("%-12s", cities.get(j));
        }
        System.out.println();
        
        // Imprimir matriz
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
