package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class AStarThreadWithVisualization extends AStarThread {
    private MainDemoWindow demo;
    private int index;

    public AStarThreadWithVisualization(WeightedGraph<Point, DefaultWeightedEdge> graph,
                                           Point source, Point destination,
                                           ArrayList<Point> shortestPath,
                                           AtomicBoolean stop, Point stopPoint,
                                           CopyOnWriteArrayList<VisitedVertex> visitedVertices,
                                           MainDemoWindow demo, int index) {
        super(graph, source, destination, shortestPath, stop, stopPoint, visitedVertices);
        this.demo = demo;
        this.index = index;
    }
    private int containsVertex(CopyOnWriteArrayList<VisitedVertex> visitedVertices, Point p){
        for (int i = 0; i < visitedVertices.size(); i++) {
            if (p.equals(visitedVertices.get(i).vertex)) {
                return i;
            }
        }
        return -1;
    }
    protected Point findPath(){
        int oldSizeNodes = 0;

        while (!unSettledNodes.isEmpty()){
            visitNextNode();
            if(stop.get() ) {
                return settledNodes.get(settledNodes.indexOf(stopPoint));
            }
            drawGraph(oldSizeNodes);
        }
        return source;
    }

    private void drawGraph(int oldSizeNodes) {
        if (graph.edgeSet().size() == oldSizeNodes)
            return;
        oldSizeNodes = graph.edgeSet().size();
        if(graph instanceof GraphProxy){
            if (index == 1)
                demo.setGraph(((GraphProxy) graph).getGraphClone());
            else
                demo.setGraph2(((GraphProxy) graph).getGraphClone());

        }
    }
}
