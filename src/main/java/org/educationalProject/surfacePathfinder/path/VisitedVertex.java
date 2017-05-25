package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class VisitedVertex {
    public Point vertex;
    public Double distance;
    public Double heuristic;
    public Map<Point, Double> neighbours;

    public VisitedVertex(Point vertex, Double distanceToVertex, Double heuristic) {
        this.vertex = vertex;
        this.distance = distanceToVertex;
        this.heuristic = heuristic;
        this.neighbours = new HashMap<Point, Double>();
    }
    public void addNeighbour(Point neighbour, Double edgeLength) {
        neighbours.put(neighbour, edgeLength);
    }

}
