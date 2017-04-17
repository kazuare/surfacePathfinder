package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;


public class AStarPathFind {

    private SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
    private Set<Point> settledNodes;
    private Set<DistancePoint> unSettledNodes;
    private Map<Point, Point> predecessors;
    private Map<Point, Double> distance;
    private Point source;
    private Point destination;

    public List<Point> getShortestPath(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination) {
        this.init(graph, source, destination);
        this.findPath();
        return this.retrievalPath();
    }

    private void init(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination) {
        this.graph = graph;
        this.source = source;
        this.destination = destination;
        settledNodes = new HashSet<Point>();
        unSettledNodes = new PriorityQueue<DistancePoint>(comparator);
        distance = new HashMap<Point, Double>();
        predecessors = new HashMap<Point, Point>();

        distance.put(source, getHeuristic(this.source, this.source));
        unSettledNodes.add(new DistancePoint(source, getHeuristic(this.source, this.source)));
    }

    public static Comparator<DistancePoint> comparator = new Comparator<DistancePoint>() {
        @Override
        public int compare(DistancePoint a, DistancePoint b) {
            return (int)(a.distance - b.distance);
        }
    };

    private void findPath() {
        while (unSettledNodes.size() > 0) {
            Point current = getMinimum(unSettledNodes);
            if (current.equals(destination))
                return;
            settledNodes.add(current);
            unSettledNodes.remove(current);
            findMinimalDistances(current);
        }
    }

    private void findMinimalDistances(Point node) {
        List<Point> adjacentNodes = getNeighbors(node);

        for (Point neighbor : adjacentNodes) {
            if (isSettled(neighbor))
                continue;
            if (!isUnSettled(neighbor)) {
                unSettledNodes.add(new DistancePoint(neighbor, getHeuristic(node, neighbor)));
                distance.put(neighbor, getHeuristic(node, neighbor));
                predecessors.put(neighbor, node);
                continue;
            } else if(getDistance(node) + getDistance(node, neighbor) > distance.get(neighbor)) {
                continue;
            } else {
                unSettledNodes.remove(new DistancePoint(neighbor, 0.0));
                unSettledNodes.add(new DistancePoint(neighbor, getHeuristic(node, neighbor)));
                predecessors.put(neighbor, node);
                distance.remove(neighbor);
                distance.put(neighbor, getHeuristic(node, neighbor));
            }
        }
    }

    private Double getHeuristic(Point current, Point next) {
        Double g = getDistance(current, next) + getDistance(current);
        Double h = Math.abs(next.alt - destination.alt) +
                Math.sqrt((next.x - destination.x) * (next.x - destination.x)
                        + (next.y - destination.y) * (next.y - destination.y)
                        + (next.alt - destination.alt)*(next.alt - destination.alt));
        return g + h;
    }
    private Double getDistance(Point node, Point target) {
        if (node.equals(target))
            return 0.0;
        DefaultWeightedEdge e = graph.getEdge(node, target);
        return (Double)graph.getEdgeWeight(e);
    }
    private Double getDistance(Point node) {
        Double d = distance.get(node);
        if (d == null) {
            return Double.MAX_VALUE;
        } else {
            return d;
        }
    }

    private List<Point> getNeighbors(Point node) {
        List<Point> neighbors = new ArrayList<Point>();
        Set<DefaultWeightedEdge> edges = graph.edgesOf(node);
        for (DefaultWeightedEdge edge : edges) {
            if (graph.getEdgeSource(edge).equals(node) && !isSettled(graph.getEdgeTarget(edge))) {
                neighbors.add(graph.getEdgeTarget(edge));
            }
            if (graph.getEdgeTarget(edge).equals(node) && !isSettled(graph.getEdgeSource(edge))){
                neighbors.add(graph.getEdgeSource(edge));
            }
        }
        return neighbors;
    }

    private Point getMinimum(PriorityQueue<DistancePoint> vertexes) {
        Point minimum = vertexes.poll().point;
        return minimum;
    }

    private boolean isSettled(Point node) {
        return settledNodes.contains(node);
    }
    private boolean isUnSettled(Point node) { return  unSettledNodes.contains(node); }

    private List<Point> retrievalPath() {
        List<Point> shortestPath = new LinkedList<Point>();
        Point step = this.destination;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        shortestPath.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            shortestPath.add(step);
        }
        // Put it into the correct order
        Collections.reverse(shortestPath);
        return shortestPath;
    }
}
