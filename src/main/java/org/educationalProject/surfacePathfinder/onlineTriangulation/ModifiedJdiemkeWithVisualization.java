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

    private double maxOfAnyCoordinate = 0;
    private Triangle2D superTriangle;
    private MainDemoWindow demo;

    public ModifiedJdiemkeWithVisualization(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph,ArrayList<Point> points,
                           HashSet<Point> processedPoints, double radius, MainDemoWindow demo) {
        super(graph, points, processedPoints, radius);
        this.demo = demo;
    }

    public void setMaxCoordinate(Set<Point> points){
        for (Point p : points)
            maxOfAnyCoordinate = Math.max(Math.max(p.x, p.y), maxOfAnyCoordinate);
        maxOfAnyCoordinate *= 48;
    }

    @Override
    public SimpleWeightedGraph<Point, DefaultWeightedEdge> init(Point center){
        setMaxCoordinate(graph.vertexSet());
        initTriangulation();
        return update(center);
    };
    @Override
    public SimpleWeightedGraph<Point, DefaultWeightedEdge> update(Point center){
        int oldSize = graph.edgeSet().size();

        ArrayList<Point> neighbours = getNearbyPoints(center);

        Set<DefaultWeightedEdge> toRemove = new HashSet<DefaultWeightedEdge>(graph.edgeSet());
        for(DefaultWeightedEdge e : toRemove)
            graph.removeEdge(e);

        for(Point p : neighbours)
            addPoint(p);

        for(Triangle2D triangle : triangleSoup.getTriangles()){
            if(triangle.hasVertex(superTriangle.a))
                continue;
            if(triangle.hasVertex(superTriangle.b))
                continue;
            if(triangle.hasVertex(superTriangle.c))
                continue;

            Point a = (Point)triangle.a;
            Point b = (Point)triangle.b;
            Point c = (Point)triangle.c;

            DefaultWeightedEdge e = graph.addEdge(a, b);
            if(e!=null)
                graph.setEdgeWeight(e, EdgeWeighter.edgeWeight(a, b));

            e = null;
            e = graph.addEdge(a, c);
            if(e!=null)
                graph.setEdgeWeight(e, EdgeWeighter.edgeWeight(a, c));

            e = null;
            e = graph.addEdge(c, b);
            if(e!=null)
                graph.setEdgeWeight(e, EdgeWeighter.edgeWeight(c, b));

        }

        try {
            setProcessedPoints(neighbours, QuickHull.findHull(neighbours));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (oldSize < graph.edgeSet().size()) {
            displayGraph();
        }
        return graph;
    }

    public void initTriangulation(){
        Vector2D p1 = new Vector2D(0d, maxOfAnyCoordinate);
        Vector2D p2 = new Vector2D(maxOfAnyCoordinate, 0d);
        Vector2D p3 = new Vector2D(-maxOfAnyCoordinate, -maxOfAnyCoordinate);

        superTriangle = new Triangle2D(p1, p2, p3);

        triangleSoup.add(superTriangle);
    }


    public void displayGraph() {
        demo.setGraph((SimpleWeightedGraph<Point, DefaultWeightedEdge>) graph.clone());
        double start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 100);
    }
}
