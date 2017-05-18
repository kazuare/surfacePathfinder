package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.EdgeWeighter;
import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.JdiemkeTriangulator.EdgeWithDistance;
import org.educationalProject.surfacePathfinder.visualization.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.Edge2D;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.TriangleSoup;
import io.github.jdiemke.triangulation.Vector2D;

public class ModifiedJdiemkeWithVisualization extends ModifiedJdiemke implements OnlineTriangulator{

    private MainDemoWindow demo;

    public ModifiedJdiemkeWithVisualization(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph,ArrayList<Point> points,
                           HashSet<Point> processedPoints, double radius, MainDemoWindow demo) {
        super(graph, points, processedPoints, radius);
        this.demo = demo;
    }


    @Override
    public SimpleWeightedGraph<Point, DefaultWeightedEdge> update(Point center){
        int oldSize = graph.edgeSet().size();
        graph = super.update(center);
        if (oldSize < graph.edgeSet().size()) {
            displayGraph();
        }
        return graph;
    }

    public void displayGraph() {
        demo.setGraph((SimpleWeightedGraph<Point, DefaultWeightedEdge>) graph.clone());
        double start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 100);
    }
}
