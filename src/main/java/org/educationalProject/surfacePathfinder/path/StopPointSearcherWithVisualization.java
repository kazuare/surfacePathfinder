package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPointSearcherWithVisualization extends  StopPointSearcher {
    private WeightedGraph<Point, DefaultWeightedEdge> graph;
    private ConcurrentHashMap<Point, Point> edgesSource;
    private ConcurrentHashMap<Point, Point> edgesDestination;
    private MainDemoWindow demo;
    public StopPointSearcherWithVisualization(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                              ConcurrentHashMap<Point, Point> edgesSource,
                                              ConcurrentHashMap<Point, Point> edgesDestination,
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

    private void MergeGraphs() {
        HashMap<Point, Point> edgesTmp = new HashMap<Point, Point>();
        Iterator it = edgesSource.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            edgesTmp.put((Point)pair.getKey(), (Point)pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        it = edgesDestination.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            edgesTmp.put((Point)pair.getKey(), (Point)pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        it = edgesTmp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            graph.addEdge((Point)pair.getKey(), (Point)pair.getValue(), new DefaultWeightedEdge());
            it.remove(); // avoids a ConcurrentModificationException
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        int oldSizeNodes1 = 0;
        int oldSizeNodes2 = 0;

        while(!stop.get()) {
            findStopPoint(oldSizeNodes1, oldSizeNodes2);
            MergeGraphs();
            demo.setGraph(graph);

        }
    }
}
