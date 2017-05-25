package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class AStarThread extends AStar implements Runnable {
    protected AtomicBoolean stop;
    protected Point stopPoint;
    protected CopyOnWriteArrayList<VisitedVertex> visitedVertices;


    public AStarThread(WeightedGraph<Point, DefaultWeightedEdge> graph,
                          Point source, Point destination,
                          ArrayList<Point> shortestPath,
                          AtomicBoolean stop, Point stopPoint,
                          CopyOnWriteArrayList<VisitedVertex> visitedVertices){
        initialize(graph, source, destination, shortestPath,
                stop, stopPoint, visitedVertices);
    }

    @Override
    public void run(){
        Point target = findPath();
        retrievePath(target);
    }

    protected void initialize(WeightedGraph<Point, DefaultWeightedEdge> graph,
                              Point source, Point destination,
                              ArrayList<Point> shortestPath,
                              AtomicBoolean stop, Point stopPoint,
                              CopyOnWriteArrayList<VisitedVertex> visitedVertices){
        super.initialize(graph, source,destination);
        this.shortestPath = shortestPath;
        this.stop = stop;
        this.stopPoint = stopPoint;
        this.visitedVertices = visitedVertices;
    }

    protected void visitNextNode() {
        Point current = unSettledNodes.poll().point;
        settledNodes.add(current);
        if (current.equals(destination)) {
            stopPoint = destination;
            stop.set(true);
            return;
        }
        VisitedVertex visitedVertex = new VisitedVertex(current, gScore.get(current), fScore.get(current));

        if(stop.get())
            return;

        List<Point> neighbors = getNeighbors(current);
        for (Point neighbor : neighbors) {
            if(stop.get())
                return;
            if (addNeighbor(current, neighbor))
                visitedVertex.addNeighbour(neighbor, getDistance(current, neighbor));
        }
        visitedVertices.add(visitedVertex);
    }

    protected Point findPath(){
        while (!unSettledNodes.isEmpty()){
            visitNextNode();
            if(stop.get()) {
                return settledNodes.get(settledNodes.indexOf(stopPoint));
            }
        }
        return source;
    }
}
