package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.lang.reflect.WildcardType;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class AStarThread  implements Runnable {
    private WeightedGraph<Point, DefaultWeightedEdge> graph;
    private CopyOnWriteArrayList<Point> settledNodes;
    private PriorityQueue<DistancePoint> unSettledNodes;
    private Map<Point, Double> gScore;
    private Map<Point, Double> hScore;
    private Map<Point, Double> fScore;
    private Map<Point, Point> predecessors;
    private List<Point> shortestPath;
    private final Point source;
    private final Point destination;
    private AtomicBoolean stop;
    private AtomicInteger stopPoint;

    AStarThread(WeightedGraph<Point, DefaultWeightedEdge> graph, Point source, Point destination,
                CopyOnWriteArrayList<Point> settledNodes,
                ArrayList<Point> shortestPath,
                AtomicInteger stopPoint, AtomicBoolean stop){
        this.graph = graph;
        this.source = source;
        this.destination = destination;
        this.settledNodes = settledNodes;
        this.shortestPath = shortestPath;
        this.stop = stop;
        this.stopPoint = stopPoint;
    }

    @Override
    public void run(){
        initialize();
        Point target = findPath();
        System.out.println(target.toString());
        retrievePath(target);
    }


    public static Comparator<DistancePoint> comparator = new Comparator<DistancePoint>() {
        @Override
        public int compare(DistancePoint a, DistancePoint b) {
            return (int)Math.signum(a.distance - b.distance);
        }
    };

    private void initialize(){

        unSettledNodes = new PriorityQueue<DistancePoint>(comparator);
        gScore = new HashMap<Point, Double>();
        hScore = new HashMap<Point, Double>();
        fScore = new HashMap<Point, Double>();
        predecessors = new HashMap<Point, Point>();

        gScore.put(this.source, 0.0);
        hScore.put(this.source, getHScore(this.source));
        fScore.put(this.source, getGScore(this.source) + getHScore(this.source));
        unSettledNodes.add(new DistancePoint(this.source, getFScore(this.source)));
    }

    private Point findPath(){
        while (!unSettledNodes.isEmpty()){

            Point current = unSettledNodes.poll().point;
            settledNodes.add(current);

            if(stop.get() && stopPoint.get() != -1) {
                Point tmp = settledNodes.get(stopPoint.get());
                return tmp;
            }

            List<Point> neighbors = getNeighbors(current);
            for (Point neighbor : neighbors) {
                if(stop.get() && stopPoint.get() != -1) {
                    Point tmp = settledNodes.get(stopPoint.get());
                    return tmp;
                }
                if (settledNodes.contains(neighbor))
                    continue;
                Double tentativeScore = getGScore(current) + getDistance(current, neighbor);
                if (tentativeScore >= getGScore(neighbor))
                    continue;
                if(isUnSettled(neighbor)){
                    unSettledNodes.remove(new DistancePoint(neighbor, getFScore(neighbor)));
                    gScore.remove(neighbor);
                    hScore.remove(neighbor);
                    fScore.remove(neighbor);
                }
                gScore.put(neighbor, tentativeScore);
                hScore.put(neighbor, getHScore(neighbor));
                fScore.put(neighbor, getGScore(neighbor) + getHScore(neighbor));
                predecessors.put(neighbor, current);
                unSettledNodes.add(new DistancePoint(neighbor, getFScore(neighbor)));
            }
        }
        return source;
    }
    private Double getDistance(Point src, Point target) {
        if (src.equals(target))
            return 0.0;
        DefaultWeightedEdge e = graph.getEdge(src, target);
        if (e == null)
            return Double.MAX_VALUE;
        else
            return graph.getEdgeWeight(e);
    }

    private List<Point> getNeighbors(Point current) {

        List<Point> neighbors = new ArrayList<Point>();
        Set<DefaultWeightedEdge> edges = graph.edgesOf(current);
        for (DefaultWeightedEdge e : edges) {
            Point src = graph.getEdgeSource(e);
            Point target = graph.getEdgeTarget(e);
            if (src.equals(current))
                neighbors.add(target);
            if(target.equals(current))
                neighbors.add(src);
        }
        return neighbors;
    }

    private boolean isUnSettled(Point current) { return fScore.containsKey(current); }
    private Double getGScore(Point current) {
        Double g = gScore.get(current);
        if (g == null)
            return Double.MAX_VALUE;
        else
            return  g;
    }
    private Double getFScore(Point current) {
        Double f = fScore.get(current);
        if (f == null)
            return Double.MAX_VALUE;
        else
            return f;
    }
    private Double getHScore(Point current){
        Double h = hScore.get(current);
        if (h == null)
            return Math.sqrt((destination.x - current.x) * (destination.x - current.x)
                    + (destination.y - current.y) * (destination.y - current.y)
                    + (destination.alt - current.alt) * (destination.alt - current.alt));

        return h;
    }
    public void retrievePath(Point target) {
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
        Collections.reverse(shortestPath);
    }
}
