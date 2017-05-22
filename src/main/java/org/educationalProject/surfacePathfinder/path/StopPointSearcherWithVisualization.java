package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPointSearcherWithVisualization extends  StopPointSearcher {
    private WeightedGraph<Point, DefaultWeightedEdge> graph;
    private CopyOnWriteArraySet<EdgeWithVertexes> edgesSource;
    private CopyOnWriteArraySet<EdgeWithVertexes> edgesDestination;
    private MainDemoWindow demo;
    private int oldEdgeSourceSize = 0;
    private int oldEdgeDestinationSize = 0;
    public StopPointSearcherWithVisualization(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                              CopyOnWriteArraySet<EdgeWithVertexes> edgesDestination,
                                              CopyOnWriteArraySet<EdgeWithVertexes> edgesSource,
                                              AtomicInteger stopPointSource, AtomicInteger stopPointDestination,
                                              AtomicBoolean stop,
                                              CopyOnWriteArrayList<Point> settledNodesSource,
                                              CopyOnWriteArrayList<Point> settledNodesDestination,
                                              MainDemoWindow demo)
    {
        super(stopPointSource, stopPointDestination, stop, settledNodesSource, settledNodesDestination);
        this.graph = graph;
        this.edgesSource = edgesSource;
        this.edgesDestination = edgesDestination;
        this.demo = demo;
    }

    private void MergeGraphs() throws InterruptedException{
        synchronized (graph) {
             if (oldEdgeSourceSize != edgesSource.size() ||
                    oldEdgeDestinationSize != edgesDestination.size()) {
                oldEdgeSourceSize = edgesSource.size();
                oldEdgeDestinationSize = edgesDestination.size();

                HashSet<DefaultWeightedEdge> edgesToRemove = new HashSet<DefaultWeightedEdge>();
                for (DefaultWeightedEdge edge :graph.edgeSet()) {
                    edgesToRemove.add(edge);
                }
                for (DefaultWeightedEdge edge : edgesToRemove) {
                    graph.removeEdge(edge);
                }
                synchronized (edgesSource) {
                    for (EdgeWithVertexes edge : edgesSource) {
                        graph.addEdge(edge.source, edge.target, edge.edge);
                    }
                    edgesSource.wait();
                }
                synchronized (edgesDestination) {
                     for (EdgeWithVertexes edge : edgesDestination) {
                         graph.addEdge(edge.source, edge.target, edge.edge);
                     }
                     edgesDestination.wait();
                 }
             }
             graph.wait();
        }
    }
    @Override
    public void run() {
        int oldSizeNodes1 = 0;
        int oldSizeNodes2 = 0;

        while(!stop.get()) {
            findStopPoint(oldSizeNodes1, oldSizeNodes2);
            demo.setGraph(graph);
            if (!stop.get())
                try {
                    MergeGraphs();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
        }
    }
}
