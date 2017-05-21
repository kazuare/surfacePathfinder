package org.educationalProject.surfacePathfinder.path;


import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AStarThreadWithVisualization extends AStarThread {
    ConcurrentHashMap<Point, Point> edges;

    public AStarThreadWithVisualization(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                         Point source, Point destination,
                                         CopyOnWriteArrayList<Point> settledNodes,
                                         ArrayList<Point> shortestPath,
                                         AtomicInteger stopPoint, AtomicBoolean stop,
                                         ConcurrentHashMap<Point, Point> edges) {
        super(graph, source, destination, settledNodes, shortestPath, stopPoint, stop);
        this.edges = edges;
    }
    private void MergeGraph() {
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            edges.put(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
        }
    }
    protected Point findPath(){
        while (!unSettledNodes.isEmpty()){
            visitNode();
            MergeGraph();
            if(stop.get() && stopPoint.get() != -1) {
                Point tmp = settledNodes.get(stopPoint.get());
                return tmp;
            }
        }
        return source;
    }

}
