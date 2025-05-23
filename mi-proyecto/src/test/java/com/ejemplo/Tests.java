package com.ejemplo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas JUnit para la implementación del grafo y el algoritmo de Floyd-Warshall.
 */
public class Tests {
    /**
     * Instancia del grafo para las pruebas.
     */
    private Graph graph;
    
    /**
     * Instancia del algoritmo de Floyd para las pruebas.
     */
    private Floyd floyd;
    
    /**
     * Configuración inicial antes de cada prueba.
     * Crea un grafo de prueba con varias aristas y ejecuta el algoritmo de Floyd.
     */
    @BeforeEach
    void setUp() {
        graph = new Graph();
        // Crear un grafo de prueba
        graph.addEdge("A", "B", 5, 7, 9, 15);
        graph.addEdge("A", "C", 3, 4, 6, 10);
        graph.addEdge("B", "C", 2, 3, 4, 8);
        graph.addEdge("B", "D", 6, 8, 10, 20);
        graph.addEdge("C", "D", 7, 9, 12, 25);
        
        floyd = new Floyd(graph);
        floyd.executeFloyd(Graph.NORMAL);
    }
    
    /**
     * Prueba para agregar ciudades al grafo.
     */
    @Test
    @DisplayName("Test adding cities to graph")
    void testAddCity() {
        Graph testGraph = new Graph();
        testGraph.addCity("TestCity");
        
        assertTrue(testGraph.getCities().contains("TestCity"));
        assertEquals(1, testGraph.getNumCities());
        assertEquals("TestCity", testGraph.getCityName(0));
        assertEquals(Integer.valueOf(0), testGraph.getCityIndex("TestCity"));
    }
    
    /**
     * Prueba para agregar aristas con múltiples condiciones climáticas.
     */
    @Test
    @DisplayName("Test adding edges with multiple weather conditions")
    void testAddEdge() {
        Graph testGraph = new Graph();
        testGraph.addEdge("City1", "City2", 10, 15, 20, 30);
        
        assertTrue(testGraph.hasEdge("City1", "City2", Graph.NORMAL));
        assertEquals(10.0, testGraph.getEdgeWeight("City1", "City2", Graph.NORMAL));
        assertEquals(15.0, testGraph.getEdgeWeight("City1", "City2", Graph.RAIN));
        assertEquals(20.0, testGraph.getEdgeWeight("City1", "City2", Graph.SNOW));
        assertEquals(30.0, testGraph.getEdgeWeight("City1", "City2", Graph.STORM));
    }
    
    /**
     * Prueba para eliminar aristas del grafo.
     */
    @Test
    @DisplayName("Test removing edges")
    void testRemoveEdge() {
        assertTrue(graph.hasEdge("A", "B", Graph.NORMAL));
        
        graph.removeEdge("A", "B");
        
        assertFalse(graph.hasEdge("A", "B", Graph.NORMAL));
        assertEquals(Double.MAX_VALUE, graph.getEdgeWeight("A", "B", Graph.NORMAL));
    }
    
    /**
     * Prueba para actualizar las condiciones climáticas de una arista.
     */
    @Test
    @DisplayName("Test updating weather conditions")
    void testUpdateWeatherCondition() {
        double originalTime = graph.getEdgeWeight("A", "B", Graph.RAIN);
        graph.updateWeatherCondition("A", "B", Graph.RAIN, 12.5);
        
        assertEquals(12.5, graph.getEdgeWeight("A", "B", Graph.RAIN));
        assertNotEquals(originalTime, graph.getEdgeWeight("A", "B", Graph.RAIN));
    }
    
    /**
     * Prueba para calcular la distancia más corta usando Floyd-Warshall.
     */
    @Test
    @DisplayName("Test Floyd-Warshall shortest distance calculation")
    void testFloydShortestDistance() {
        // El camino directo A -> B debería ser 5
        assertEquals(5.0, floyd.getShortestDistance("A", "B"));
        
        // El camino A -> C debería ser 3 (directo)
        assertEquals(3.0, floyd.getShortestDistance("A", "C"));
        
        // El camino A -> D debería ser 11 (A->C->B->D = 3+2+6 = 11, o A->B->D = 5+6 = 11)
        double distanceAD = floyd.getShortestDistance("A", "D");
        assertTrue(distanceAD <= 11.0);
    }
    
    /**
     * Prueba para reconstruir el camino más corto usando Floyd-Warshall.
     */
    @Test
    @DisplayName("Test Floyd-Warshall shortest path reconstruction")
    void testFloydShortestPath() {
        List<String> path = floyd.getShortestPath("A", "D");
        
        assertFalse(path.isEmpty());
        assertEquals("A", path.get(0));
        assertEquals("D", path.get(path.size() - 1));
        
        // Verificar la continuidad del camino
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            assertTrue(graph.hasEdge(current, next, Graph.NORMAL),
                "Debe existir una arista entre " + current + " y " + next);
        }
    }
    
    /**
     * Prueba para verificar la existencia de un camino entre dos nodos.
     */
    @Test
    @DisplayName("Test path existence")
    void testHasPath() {
        assertTrue(floyd.hasPath("A", "D"));
        assertTrue(floyd.hasPath("A", "B"));
        assertTrue(floyd.hasPath("A", "C"));
        
        // Prueba de camino inexistente (si se eliminan todas las conexiones a un nodo)
        Graph isolatedGraph = new Graph();
        isolatedGraph.addEdge("X", "Y", 5, 7, 9, 15);
        isolatedGraph.addCity("Z"); // Z está aislado
        
        Floyd isolatedFloyd = new Floyd(isolatedGraph);
        isolatedFloyd.executeFloyd(Graph.NORMAL);
        
        assertFalse(isolatedFloyd.hasPath("X", "Z"));
        assertFalse(isolatedFloyd.hasPath("Z", "X"));
    }
    
    /**
     * Prueba para calcular el centro del grafo.
     */
    @Test
    @DisplayName("Test graph center calculation")
    void testCalculateGraphCenter() {
        String center = floyd.calculateGraphCenter();
        assertNotNull(center);
        assertTrue(graph.getCities().contains(center));
        
        // El centro debe ser una de las ciudades del grafo
        // y debe minimizar la distancia máxima a otros nodos
    }
    
    /**
     * Prueba para obtener la matriz de adyacencia del grafo.
     */
    @Test
    @DisplayName("Test adjacency matrix retrieval")
    void testGetAdjacencyMatrix() {
        double[][] matrix = graph.getAdjacencyMatrix(Graph.NORMAL);
        
        assertEquals(graph.getNumCities(), matrix.length);
        assertEquals(graph.getNumCities(), matrix[0].length);
        
        // La diagonal debe ser 0
        for (int i = 0; i < matrix.length; i++) {
            assertEquals(0.0, matrix[i][i]);
        }
        
        // Prueba de arista conocida
        int indexA = graph.getCityIndex("A");
        int indexB = graph.getCityIndex("B");
        assertEquals(5.0, matrix[indexA][indexB]);
    }
    
    /**
     * Prueba para operaciones con ciudades inválidas.
     */
    @Test
    @DisplayName("Test invalid city operations")
    void testInvalidCityOperations() {
        assertNull(graph.getCityIndex("NonExistentCity"));
        assertEquals(Double.MAX_VALUE, 
            graph.getEdgeWeight("NonExistent1", "NonExistent2", Graph.NORMAL));
        assertFalse(graph.hasEdge("NonExistent1", "NonExistent2", Graph.NORMAL));
        
        assertEquals(Double.MAX_VALUE, 
            floyd.getShortestDistance("NonExistent1", "NonExistent2"));
        assertTrue(floyd.getShortestPath("NonExistent1", "NonExistent2").isEmpty());
    }
    
    /**
     * Prueba para los límites de las condiciones climáticas.
     */
    @Test
    @DisplayName("Test weather condition boundaries")
    void testWeatherConditionBoundaries() {
        // Prueba condición climática inválida
        graph.updateWeatherCondition("A", "B", -1, 100.0);
        assertNotEquals(100.0, graph.getEdgeWeight("A", "B", Graph.NORMAL));
        
        graph.updateWeatherCondition("A", "B", 5, 100.0);
        assertNotEquals(100.0, graph.getEdgeWeight("A", "B", Graph.NORMAL));
        
        // Prueba condiciones climáticas válidas
        for (int weather = 0; weather < Graph.NUM_WEATHER_CONDITIONS; weather++) {
            double testValue = 50.0 + weather;
            graph.updateWeatherCondition("A", "B", weather, testValue);
            assertEquals(testValue, graph.getEdgeWeight("A", "B", weather));
        }
    }
    
    /**
     * Prueba para grafo vacío.
     */
    @Test
    @DisplayName("Test empty graph")
    void testEmptyGraph() {
        Graph emptyGraph = new Graph();
        Floyd emptyFloyd = new Floyd(emptyGraph);
        
        assertEquals(0, emptyGraph.getNumCities());
        assertTrue(emptyGraph.getCities().isEmpty());
        assertNull(emptyFloyd.calculateGraphCenter());
    }
    
    /**
     * Prueba para grafo con un solo nodo.
     */
    @Test
    @DisplayName("Test single node graph")
    void testSingleNodeGraph() {
        Graph singleGraph = new Graph();
        singleGraph.addCity("OnlyCity");
        Floyd singleFloyd = new Floyd(singleGraph);
        singleFloyd.executeFloyd(Graph.NORMAL);
        
        assertEquals(1, singleGraph.getNumCities());
        assertEquals("OnlyCity", singleFloyd.calculateGraphCenter());
        assertEquals(0.0, singleFloyd.getShortestDistance("OnlyCity", "OnlyCity"));
    }
}
