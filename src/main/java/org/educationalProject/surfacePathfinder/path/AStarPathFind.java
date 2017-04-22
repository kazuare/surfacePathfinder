package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.util.*;


public class AStarPathFind {

    private SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
    private Set<Point> settledNodes;
    private PriorityQueue<DistancePoint> unSettledNodes;
    private Map<Point, Point> predecessors;
    private Map<Point, Double> gScore;
    private Map<Point, Double> fScore;
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
        gScore = new HashMap<Point, Double>();
        fScore = new HashMap<Point, Double>();
        predecessors = new HashMap<Point, Point>();

        gScore.put(this.source, 0.0);
        fScore.put(this.source, getHeuristic(this.source, this.source));
        unSettledNodes.add(new DistancePoint(this.source, fScore.get(this.source)));
    }

    private void findPath() {
        while (unSettledNodes.size() > 0) {
            Point current = unSettledNodes.poll().point;
            if (isSettled(current))
                continue;
            if (current.equals(destination))
                return;
            settledNodes.add(current);
            findMinimalDistances(current);
        }
    }

    private void findMinimalDistances(Point node) {
        List<Point> adjacentNodes = getNeighbors(node);

        for (Point neighbor : adjacentNodes) {
            double tentativeScore = getDistance(node) + getDistance(node, neighbor);
            if (isSettled(neighbor))
                continue;
            if(tentativeScore > getDistance(neighbor))
                continue;

            if (isUnSettled(neighbor)) {
                predecessors.remove(neighbor);
                gScore.remove(neighbor);
                fScore.remove(neighbor);
            }
            gScore.put(neighbor, tentativeScore);
            fScore.put(neighbor, getHeuristic(node, neighbor));
            unSettledNodes.add(new DistancePoint(neighbor, fScore.get(neighbor)));
            predecessors.put(neighbor, node);
        }
    }

    private Double getHeuristic(Point current, Point next) {
        Double g = gScore.get(current);
        Double h = (Double)Math.sqrt((next.x - destination.x) * (next.x - destination.x)
                        + (next.y - destination.y) * (next.y - destination.y)
                        + (next.alt - destination.alt) * (next.alt - destination.alt))
                        + Math.abs(next.alt - destination.alt);

        return g + h;
    }
    private Double getDistance(Point node, Point target) {
        if (node.equals(target))
            return 0.0;
        DefaultWeightedEdge e = graph.getEdge(node, target);
        return (Double)graph.getEdgeWeight(e);
    }
    private Double getDistance(Point node) {
        Double g = gScore.get(node);
        if (g == null) {
            return Double.MAX_VALUE;
        } else {
            return g;
        }
    }

    private List<Point> getNeighbors(Point node) {
        List<Point> neighbors = new ArrayList<Point>();
        Set<DefaultWeightedEdge> edges = graph.edgesOf(node);
        for (DefaultWeightedEdge edge : edges) {
            Point source = graph.getEdgeSource(edge);
            Point target = graph.getEdgeTarget(edge);
            if (source.equals(node) && !isSettled(target)) {
                neighbors.add(target);
            }else if (target.equals(node) && !isSettled(source)){
                neighbors.add(source);
            }
        }
        return neighbors;
    }

    public static Comparator<DistancePoint> comparator = new Comparator<DistancePoint>() {
        @Override
        public int compare(DistancePoint a, DistancePoint b) {
            return (int)Math.signum(a.distance - b.distance);
        }
    };

    private Point getMinimum() {
        Point minimum = unSettledNodes.peek().point;
        return minimum;
    }

    private boolean isSettled(Point node) {
        return settledNodes.contains(node);
    }
    private boolean isUnSettled(Point node) {return fScore.containsKey(node); }

    private List<Point> retrievalPath() {
        List<Point> shortestPath = new LinkedList<Point>();
        Point step = this.destination;
        if (predecessors.get(step) == null) {
            return null;
        }
        shortestPath.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            shortestPath.add(step);
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }
}
