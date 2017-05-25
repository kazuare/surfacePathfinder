package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;


public class AStarPathFind {
    protected WeightedGraph<Point, DefaultWeightedEdge> graph;
    protected PriorityQueue<DistancePoint> unSettledNodes;
    protected List<Point> settledNodes;
    protected Map<Point, Double> gScore;
    protected Map<Point, Double> hScore;
    protected Map<Point, Double> fScore;
    protected Map<Point, Point> predecessors;
    protected List<Point> shortestPath;
    protected Point source;
    protected Point destination;
    protected double lengthOfPath = -1.0;

    public static Comparator<DistancePoint> comparator = new Comparator<DistancePoint>() {
        @Override
        public int compare(DistancePoint a, DistancePoint b) {
            return (int)Math.signum(a.distance - b.distance);
        }
    };

    public List<Point> getShortestPath(WeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination){
        initialize(graph, source, destination);
        findPath();
        retrievePath(this.destination);
        return shortestPath;
    }

    protected void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination){
        this.graph = graph;
        this.source = source;
        this.destination = destination;

        settledNodes = new ArrayList<Point>();
        unSettledNodes = new PriorityQueue<DistancePoint>(comparator);
        gScore = new HashMap<Point, Double>();
        hScore = new HashMap<Point, Double>();
        fScore = new HashMap<Point, Double>();
        predecessors = new HashMap<Point, Point>();

        gScore.put(this.source, 0.0);
        hScore.put(this.source, getHScore(this.source));
        fScore.put(this.source, getGScore(this.source) + getHScore(this.source));
        unSettledNodes.add(new DistancePoint(this.source, getFScore(this.source)));
        shortestPath = new ArrayList<Point>();
    }
    protected boolean addNeighbor(Point current, Point neighbor) {
        if (settledNodes.contains(neighbor))
            return false;
        Double tentativeScore = getGScore(current) + getDistance(current, neighbor);
        if (tentativeScore >= getGScore(neighbor))
            return false;
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
        return true;
    }
    protected void visitNextNode() {
        Point current = unSettledNodes.poll().point;
        if (current.equals(destination))
            return;
        settledNodes.add(current);
        List<Point> neighbors = getNeighbors(current);
        for (Point neighbor : neighbors)
            addNeighbor(current, neighbor);
    }

    private void findPath(){
        while (!unSettledNodes.isEmpty()){
            visitNextNode();
        }
    }
    protected Double getDistance(Point src, Point target) {
        if (src.equals(target))
            return 0.0;
        DefaultWeightedEdge e = graph.getEdge(src, target);
        if (e == null)
            return Double.MAX_VALUE;
        else
            return graph.getEdgeWeight(e);
    }

    protected List<Point> getNeighbors(Point current) {
        List<Point> neighbors = new ArrayList<Point>();
        Set<DefaultWeightedEdge> edges = graph.edgesOf(current);
        for (DefaultWeightedEdge e : edges) {
            Point src = graph.getEdgeSource(e);
            Point target = graph.getEdgeTarget(e);
            if (src.equals(current))
                neighbors.add(target);
            else
                neighbors.add(src);
        }
        return neighbors;
    }

    protected boolean isUnSettled(Point current) { return fScore.containsKey(current); }

    protected Double getGScore(Point current) {
        Double g = gScore.get(current);
        if (g == null)
            return Double.MAX_VALUE;
        else
            return  g;
    }

    protected Double getFScore(Point current) {
        Double f = fScore.get(current);
        if (f == null)
            return Double.MAX_VALUE;
        else
            return f;
    }

    protected Double getHScore(Point current){
        Double h = hScore.get(current);
        if (h == null)
            return Math.sqrt((destination.x - current.x) * (destination.x - current.x)
                    + (destination.y - current.y) * (destination.y - current.y)
                    + (destination.alt - current.alt) * (destination.alt - current.alt));

        return h;
    }

    protected void retrievePath(Point target) {
        Point step = target;
        if (predecessors.get(step) == null) {
            shortestPath = null;
            return;
        }
        shortestPath.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            shortestPath.add(step);
        }

        for (int i = 1; i < shortestPath.size(); i++) {
            DefaultWeightedEdge e = graph.getEdge(shortestPath.get(i - 1), shortestPath.get(i));
            lengthOfPath += (double) graph.getEdgeWeight(e);
        }

        Collections.reverse(shortestPath);
    }

    public Double getLengthOfPath() {
        return lengthOfPath;
    }
}