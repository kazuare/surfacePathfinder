package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.util.*;


public class AStarPathFind {

    private WeightedGraph<Point, DefaultWeightedEdge> graph;
    private PriorityQueue<DistancePoint> unSettledNodes;
    private Set<Point> settledNodes;
<<<<<<< Updated upstream
    private PriorityQueue<DistancePoint> unSettledNodes;
    private Map<Point, Point> predecessors;
    private Map<Point, Double> gScore;
    private Map<Point, Double> fScore;
=======
    private Map<Point, Double> gScore;
    private Map<Point, Double> hScore;
    private Map<Point, Double> fScore;
    private Map<Point, Point> predecessors;
    private List<Point> shortestPath;
>>>>>>> Stashed changes
    private Point source;
    private Point destination;

    public static Comparator<DistancePoint> comparator = new Comparator<DistancePoint>() {
        @Override
        public int compare(DistancePoint a, DistancePoint b) {
            return (int)Math.signum(a.distance - b.distance);
        }
    };

    public List<Point> getShortestPath(WeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination){
        initialize(graph, source, destination);
        findPath();
        retrievePath();
        return shortestPath;
    }
    private void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination){
        this.graph = graph;
        this.source = source;
        this.destination = destination;

        settledNodes = new HashSet<Point>();
        unSettledNodes = new PriorityQueue<DistancePoint>(comparator);
        gScore = new HashMap<Point, Double>();
<<<<<<< Updated upstream
=======
        hScore = new HashMap<Point, Double>();
>>>>>>> Stashed changes
        fScore = new HashMap<Point, Double>();
        predecessors = new HashMap<Point, Point>();

        gScore.put(this.source, 0.0);
<<<<<<< Updated upstream
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
=======
        hScore.put(this.source, getHScore(this.source));
        fScore.put(this.source, getGScore(this.source) + getHScore(this.source));
        unSettledNodes.add(new DistancePoint(this.source, getFScore(this.source)));
    }

    private void findPath(){
        while (!unSettledNodes.isEmpty()){
            Point current = unSettledNodes.poll().point;
            if (current.equals(destination))
                return;
            settledNodes.add(current);
            List<Point> neighbors = getNeighbors(current);
            for (Point neighbor : neighbors) {
                if (settledNodes.contains(neighbor))
                    continue;
                Double tentativeScore = getGScore(current) + getDistance(current, neighbor);
                if (tentativeScore >= getGScore(neighbor))
                    continue;
                if(isUnSettled(neighbor)){
                    unSettledNodes.remove(new DistancePoint(neighbor, getFScore(neighbor)));
                    predecessors.remove(neighbor);
                    gScore.remove(neighbor);
                    hScore.remove(neighbor);
                    fScore.remove(neighbor);
                }
                gScore.put(neighbor, tentativeScore);
                hScore.put(neighbor, getHScore(neighbor));
                fScore.put(neighbor, getGScore(neighbor) + getHScore(neighbor));
                predecessors.put(neighbor, current);
                unSettledNodes.add(new DistancePoint(neighbor, getFScore(neighbor)));
>>>>>>> Stashed changes
            }
            gScore.put(neighbor, tentativeScore);
            fScore.put(neighbor, getHeuristic(node, neighbor));
            unSettledNodes.add(new DistancePoint(neighbor, fScore.get(neighbor)));
            predecessors.put(neighbor, node);
        }
    }
<<<<<<< Updated upstream

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
=======
    private Double getDistance(Point src, Point target) {
        if (src.equals(target))
            return 0.0;
        DefaultWeightedEdge e = graph.getEdge(src, target);
        if (e == null)
            return Double.MAX_VALUE;
        else
            return graph.getEdgeWeight(e);
>>>>>>> Stashed changes
    }

    private List<Point> getNeighbors(Point current) {
        List<Point> neighbors = new ArrayList<Point>();
<<<<<<< Updated upstream
        Set<DefaultWeightedEdge> edges = graph.edgesOf(node);
        for (DefaultWeightedEdge edge : edges) {
            Point source = graph.getEdgeSource(edge);
            Point target = graph.getEdgeTarget(edge);
            if (source.equals(node) && !isSettled(target)) {
                neighbors.add(target);
            }else if (target.equals(node) && !isSettled(source)){
                neighbors.add(source);
            }
=======
        Set<DefaultWeightedEdge> edges = graph.edgesOf(current);
        for (DefaultWeightedEdge e : edges) {
            Point src = graph.getEdgeSource(e);
            Point target = graph.getEdgeTarget(e);
            if (src.equals(current))
                neighbors.add(target);
            else
                neighbors.add(src);
>>>>>>> Stashed changes
        }
        return neighbors;
    }

<<<<<<< Updated upstream
    public static Comparator<DistancePoint> comparator = new Comparator<DistancePoint>() {
        @Override
        public int compare(DistancePoint a, DistancePoint b) {
            return (int)Math.signum(a.distance - b.distance);
        }
    };

    private Point getMinimum() {
        Point minimum = unSettledNodes.peek().point;
        return minimum;
=======
    private boolean isUnSettled(Point current) { return fScore.containsKey(current); }
    private Double getGScore(Point current) {
        Double g = gScore.get(current);
        if (g == null)
            return Double.MAX_VALUE;
        else
            return  g;
>>>>>>> Stashed changes
    }
    private Double getFScore(Point current) {
        Double f = fScore.get(current);
        if (f == null)
            return Double.MAX_VALUE;
        else
            return f;
    }
<<<<<<< Updated upstream
    private boolean isUnSettled(Point node) {return fScore.containsKey(node); }

    private List<Point> retrievalPath() {
        List<Point> shortestPath = new LinkedList<Point>();
=======
    private Double getHScore(Point current){
        Double h = hScore.get(current);
        if (h == null)
            return Math.sqrt((destination.x - current.x) * (destination.x - current.x)
                    + (destination.y - current.y) * (destination.y - current.y)
                    + (destination.alt - current.alt) * (destination.alt - current.alt));

        return h;
    }
    private void retrievePath() {
        shortestPath = new LinkedList<Point>();
>>>>>>> Stashed changes
        Point step = this.destination;
        if (predecessors.get(step) == null) {
            shortestPath = null;
            return;
        }
        shortestPath.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            shortestPath.add(step);
        }
        Collections.reverse(shortestPath);
    }
    public Double getLengthOfPath() {
        Double length = 0.0;
        if (shortestPath == null)
            return 0.0;

        for (int j = 0; j < shortestPath.size() - 1; j++) {
            DefaultWeightedEdge e = graph.getEdge(shortestPath.get(j), shortestPath.get(j + 1));
            length += (double) graph.getEdgeWeight(e);
        }
        return length;
    }
}
