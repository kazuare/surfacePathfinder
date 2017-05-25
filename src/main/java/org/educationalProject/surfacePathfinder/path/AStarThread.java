package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class AStarThread  implements Runnable {
    protected WeightedGraph<Point, DefaultWeightedEdge> graph;
    protected List<Point> settledNodes;
    protected PriorityQueue<DistancePoint> unSettledNodes;
    protected Map<Point, Double> gScore;
    protected Map<Point, Double> hScore;
    protected Map<Point, Double> fScore;
    protected Map<Point, Point> predecessors;
    protected List<Point> shortestPath;
    protected final Point source;
    protected final Point destination;
    protected AtomicBoolean stop;
    ///
    protected Point stopPoint;
    protected CopyOnWriteArrayList<VisitedVertex> visitedVertices;


    public AStarThread(WeightedGraph<Point, DefaultWeightedEdge> graph,
                          Point source, Point destination,
                          ArrayList<Point> shortestPath,
                          AtomicBoolean stop, Point stopPoint,
                          CopyOnWriteArrayList<VisitedVertex> visitedVertices
    ){
        this.graph = graph;
        this.source = source;
        this.destination = destination;
        this.shortestPath = shortestPath;
        this.stop = stop;
        this.stopPoint = stopPoint;
        this.visitedVertices = visitedVertices;
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

    protected void initialize(){
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
    }
    protected void visitNode() {
        Point current = unSettledNodes.poll().point;
        settledNodes.add(current);

        VisitedVertex visitedVertex = new VisitedVertex(current, gScore.get(current), getHScore(current));
        if(current.equals(destination)) {
            stop.set(true);
            stopPoint = destination;
            return;
        }
        if(stop.get())
            return;

        List<Point> neighbors = getNeighbors(current);
        for (Point neighbor : neighbors) {
            if(stop.get())
                return;

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
                // predecessors.remove(neighbor);
            }
            gScore.put(neighbor, tentativeScore);
            hScore.put(neighbor, getHScore(neighbor));
            fScore.put(neighbor, getGScore(neighbor) + getHScore(neighbor));
            predecessors.put(neighbor, current);
            unSettledNodes.add(new DistancePoint(neighbor, getFScore(neighbor)));
            visitedVertex.addNeighbour(neighbor, getDistance(current, neighbor));
        }
        visitedVertices.add(visitedVertex);
    }
    protected Point findPath(){
        while (!unSettledNodes.isEmpty()){
            visitNode();
            if(stop.get()) {
                return stopPoint;
            }
        }
        return source;
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
            if(target.equals(current))
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
            return  Math.sqrt((destination.x - current.x) * (destination.x - current.x)
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
        Collections.reverse(shortestPath);
    }
}
