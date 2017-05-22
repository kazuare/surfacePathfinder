package org.educationalProject.surfacePathfinder.path;


import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
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
    private MainDemoWindow demo;
    private int index;

    public AStarThreadWithVisualization(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                         Point source, Point destination,
                                         CopyOnWriteArrayList<Point> settledNodes,
                                         ArrayList<Point> shortestPath,
                                         AtomicInteger stopPoint, AtomicBoolean stop,
                                         MainDemoWindow demo, int index,
                                        ConcurrentHashMap<Point, Double> distances) {
        super(graph, source, destination, settledNodes, shortestPath, stopPoint, stop, distances);
        this.demo = demo;
        this.index = index;
    }

    protected Point findPath(){
        int oldSizeNodes = 0;

        while (!unSettledNodes.isEmpty()){
            visitNode();
            if(stop.get() && stopPoint.get() != -1) {
                Point tmp = settledNodes.get(stopPoint.get());
                return tmp;
            }
            if (graph.edgeSet().size() == oldSizeNodes)
                continue;
            oldSizeNodes = graph.edgeSet().size();
            if(graph instanceof GraphProxy){
                if (index == 1)
                    demo.setGraph(((GraphProxy) graph).getGraphClone());
                else
                    demo.setGraph2(((GraphProxy) graph).getGraphClone());

            }

        }
        return source;
    }

}
