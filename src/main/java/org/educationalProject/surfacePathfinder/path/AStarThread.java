package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.lang.reflect.WildcardType;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class AStarThread  implements Runnable {
    private static WeightedGraph<Point, DefaultWeightedEdge> graph;
    private volatile ArrayList<Point> settledNodes;
    private volatile ArrayList<Point> anotherThreadSettledNodes;
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
                ArrayList<Point> settledNodes, ArrayList<Point> anotherThreadSettledNodes,
                ArrayList<Point> shortestPath, AtomicInteger stopPoint, AtomicBoolean stop){
        this.graph = graph;
        this.source = source;
        this.destination = destination;
        this.settledNodes = settledNodes;
        this.anotherThreadSettledNodes = anotherThreadSettledNodes;
        this.shortestPath = shortestPath;
        this.stop = stop;
        this.stopPoint = stopPoint;
    }

    @Override
    public void run(){
        initialize();
        Point target = findPath();
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
            if(stop.get()) {
                Point tmp = settledNodes.get(stopPoint.get());
                System.out.println("Get stopped. stoppoint: " + tmp.x + " " + tmp.y + " " + tmp.alt);
                /*if (!settledNodes.contains(stopPoint))
                    System.out.println("No stoppoint in settled nodes");*/
                return tmp;
            }

            if (anotherThreadSettledNodes.contains(current) || destination.equals(current)) {
                int index = anotherThreadSettledNodes.indexOf(current);
                System.out.println("index " + index);
                System.out.println("currentpoint: " + current.x + " " + current.y + " " + current.alt);
                if (!stop.getAndSet(true)) {
                    stopPoint.set(index);
                    return current;
                }
            }
            /*if (destination.equals(current)) {
                /*if (anotherThreadSettledNodes.contains(current)) {
                    //System.out.println("source: " + source.x + " " + source.y + " " + source.alt);
                    //System.out.println("destination: " + destination.x + " " + destination.y + " " + destination.alt);
                    //System.out.println("currentpoint: " + current.x + " " + current.y + " " + current.alt);
                    System.out.println("stoppoint: " + stopPoint.x + " " + stopPoint.y + " " + stopPoint.alt);
                    System.out.println("Contains current. Size1 " + anotherThreadSettledNodes.size() + ". Size2 " + settledNodes.size());
                    if (predecessors.get(current) == null) {
                        System.out.println("Null-path current");
                    }
                }*/
                //if(destination.equals(current))
                  //  System.out.println("Found destination. Size1 " + anotherThreadSettledNodes.size() + ". Size2 " + settledNodes.size());
                  /*  stop.set(true);
                    stopPoint.x = current.x;
                    stopPoint.y = current.y;
                    stopPoint.alt = current.alt;
                    System.out.println("stoppoint: " + stopPoint.x + " " + stopPoint.y + " " + stopPoint.alt);
                    return current;
            }*/

            List<Point> neighbors = getNeighbors(current);
            for (Point neighbor : neighbors) {
                if(stop.get()) {
                    Point tmp = settledNodes.get(stopPoint.get());
                    System.out.println("Get stopped. stoppoint: " + tmp.x + " " + tmp.y + " " + tmp.alt);
                    /*if (predecessors.get(stopPoint) == null)
                        System.out.println("Null-path stoppoint");
                    if (!settledNodes.contains(stopPoint))
                        System.out.println("No stoppoint in settled nodes");*/
                    return tmp;
                }

                if (settledNodes.contains(neighbor))
                    continue;
                Double tentativeScore = getGScore(current) + getDistance(current, neighbor);
                if (tentativeScore >= getGScore(neighbor))
                    continue;
                if(isUnSettled(neighbor)){
                    unSettledNodes.remove(new DistancePoint(neighbor, getFScore(neighbor)));
                    //predecessors.remove(neighbor);
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
            else
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
        System.out.println("Start retrieve path");
        Point step = target;
        if (predecessors.get(step) == null) {
            System.out.println("Null-path");
            shortestPath = null;
            return;
        } else {
            System.out.println("We have path-start");
        }
        shortestPath.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            shortestPath.add(step);
        }
        Collections.reverse(shortestPath);
    }
}
