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
 * JUnit tests for Graph and Floyd-Warshall algorithm implementation
 */
public class Tests {
    private Graph graph;
    private Floyd floyd;
    
    @BeforeEach
    void setUp() {
        graph = new Graph();
        // Create a test graph
        graph.addEdge("A", "B", 5, 7, 9, 15);
        graph.addEdge("A", "C", 3, 4, 6, 10);
        graph.addEdge("B", "C", 2, 3, 4, 8);
        graph.addEdge("B", "D", 6, 8, 10, 20);
        graph.addEdge("C", "D", 7, 9, 12, 25);
        
        floyd = new Floyd(graph);
        floyd.executeFloyd(Graph.NORMAL);
    }
    
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
    
    @Test
    @DisplayName("Test removing edges")
    void testRemoveEdge() {
        assertTrue(graph.hasEdge("A", "B", Graph.NORMAL));
        
        graph.removeEdge("A", "B");
        
        assertFalse(graph.hasEdge("A", "B", Graph.NORMAL));
        assertEquals(Double.MAX_VALUE, graph.getEdgeWeight("A", "B", Graph.NORMAL));
    }
    
    @Test
    @DisplayName("Test updating weather conditions")
    void testUpdateWeatherCondition() {
        double originalTime = graph.getEdgeWeight("A", "B", Graph.RAIN);
        graph.updateWeatherCondition("A", "B", Graph.RAIN, 12.5);
        
        assertEquals(12.5, graph.getEdgeWeight("A", "B", Graph.RAIN));
        assertNotEquals(originalTime, graph.getEdgeWeight("A", "B", Graph.RAIN));
    }
    
    @Test
    @DisplayName("Test Floyd-Warshall shortest distance calculation")
    void testFloydShortestDistance() {
        // Direct path A -> B should be 5
        assertEquals(5.0, floyd.getShortestDistance("A", "B"));
        
        // Path A -> C should be 3 (direct)
        assertEquals(3.0, floyd.getShortestDistance("A", "C"));
        
        // Path A -> D should be 5 (A->C->B->D = 3+2+6 = 11, but A->B->D = 5+6 = 11)
        // Actually, shortest should be A->C->B->D = 3+2+6 = 11
        double distanceAD = floyd.getShortestDistance("A", "D");
        assertTrue(distanceAD <= 11.0);
    }
    
    @Test
    @DisplayName("Test Floyd-Warshall shortest path reconstruction")
    void testFloydShortestPath() {
        List<String> path = floyd.getShortestPath("A", "D");
        
        assertFalse(path.isEmpty());
        assertEquals("A", path.get(0));
        assertEquals("D", path.get(path.size() - 1));
        
        // Verify path continuity
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            assertTrue(graph.hasEdge(current, next, Graph.NORMAL),
                "Edge should exist between " + current + " and " + next);
        }
    }
    
    @Test
    @DisplayName("Test path existence")
    void testHasPath() {
        assertTrue(floyd.hasPath("A", "D"));
        assertTrue(floyd.hasPath("A", "B"));
        assertTrue(floyd.hasPath("A", "C"));
        
        // Test non-existent path (if we remove all connections to a node)
        Graph isolatedGraph = new Graph();
        isolatedGraph.addEdge("X", "Y", 5, 7, 9, 15);
        isolatedGraph.addCity("Z"); // Z is isolated
        
        Floyd isolatedFloyd = new Floyd(isolatedGraph);
        isolatedFloyd.executeFloyd(Graph.NORMAL);
        
        assertFalse(isolatedFloyd.hasPath("X", "Z"));
        assertFalse(isolatedFloyd.hasPath("Z", "X"));
    }
    
    @Test
    @DisplayName("Test graph center calculation")
    void testCalculateGraphCenter() {
        String center = floyd.calculateGraphCenter();
        assertNotNull(center);
        assertTrue(graph.getCities().contains(center));
        
        // The center should be one of the cities in the graph
        // and should minimize the maximum distance to other nodes
    }
    
    @Test
    @DisplayName("Test adjacency matrix retrieval")
    void testGetAdjacencyMatrix() {
        double[][] matrix = graph.getAdjacencyMatrix(Graph.NORMAL);
        
        assertEquals(graph.getNumCities(), matrix.length);
        assertEquals(graph.getNumCities(), matrix[0].length);
        
        // Test diagonal should be 0
        for (int i = 0; i < matrix.length; i++) {
            assertEquals(0.0, matrix[i][i]);
        }
        
        // Test known edge
        int indexA = graph.getCityIndex("A");
        int indexB = graph.getCityIndex("B");
        assertEquals(5.0, matrix[indexA][indexB]);
    }
    
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
    
    @Test
    @DisplayName("Test weather condition boundaries")
    void testWeatherConditionBoundaries() {
        // Test invalid weather condition
        graph.updateWeatherCondition("A", "B", -1, 100.0);
        assertNotEquals(100.0, graph.getEdgeWeight("A", "B", Graph.NORMAL));
        
        graph.updateWeatherCondition("A", "B", 5, 100.0);
        assertNotEquals(100.0, graph.getEdgeWeight("A", "B", Graph.NORMAL));
        
        // Test valid weather conditions
        for (int weather = 0; weather < Graph.NUM_WEATHER_CONDITIONS; weather++) {
            double testValue = 50.0 + weather;
            graph.updateWeatherCondition("A", "B", weather, testValue);
            assertEquals(testValue, graph.getEdgeWeight("A", "B", weather));
        }
    }
    
    @Test
    @DisplayName("Test empty graph")
    void testEmptyGraph() {
        Graph emptyGraph = new Graph();
        Floyd emptyFloyd = new Floyd(emptyGraph);
        
        assertEquals(0, emptyGraph.getNumCities());
        assertTrue(emptyGraph.getCities().isEmpty());
        assertNull(emptyFloyd.calculateGraphCenter());
    }
    
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