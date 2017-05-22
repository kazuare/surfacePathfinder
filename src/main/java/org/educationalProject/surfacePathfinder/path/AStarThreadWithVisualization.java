package org.educationalProject.surfacePathfinder.path;


import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AStarThreadWithVisualization extends AStarThread {
    private CopyOnWriteArraySet<EdgeWithVertexes> edges;
    private int oldGraphEdgeSetSize = 0;

    public AStarThreadWithVisualization(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                         Point source, Point destination,
                                         CopyOnWriteArrayList<Point> settledNodes,
                                         ArrayList<Point> shortestPath,
                                         AtomicInteger stopPoint, AtomicBoolean stop,
                                         CopyOnWriteArraySet<EdgeWithVertexes> edges) {
        super(graph, source, destination, settledNodes, shortestPath, stopPoint, stop);
        this.edges = edges;
    }
    private void MergeGraph() {
        synchronized (edges) {
            if (graph.edgeSet().size() == oldGraphEdgeSetSize)
                return;
            oldGraphEdgeSetSize = graph.edgeSet().size();
            for (EdgeWithVertexes edge : edges) {
                if (!graph.containsEdge(edge.edge))
                    edges.remove(edge);
            }
            for (DefaultWeightedEdge edge : graph.edgeSet()) {
                edges.add(new EdgeWithVertexes(graph.getEdgeSource(edge),
                        graph.getEdgeTarget(edge),
                        edge));
            }
            edges.notifyAll();
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
