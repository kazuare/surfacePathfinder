package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.EdgeWeighter;
import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.JdiemkeTriangulator.EdgeWithDistance;
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.Online2DVisualizer;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.educationalProject.surfacePathfinder.visualization.SwingWithUpdate;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.Edge2D;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.TriangleSoup;
import io.github.jdiemke.triangulation.Vector2D;

public class ModifiedJdiemkeWithVisualization extends DelaunayTriangulator implements OnlineTriangulator{
    protected SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
    protected double radius;
    protected double r2;
    protected ArrayList<Point> centers;
    protected ArrayList<EdgeWithDistance> removedEdges;
    protected ArrayList<EdgeWithDistance> edges;
    protected HashSet<Point> processedPoints;
    protected HashSet<Vector2D> addedPoints;
    private double maxOfAnyCoordinate = 0;
    private Triangle2D superTriangle;
    private Online2DVisualizer vis2D;
    private SwingWithUpdate swingWindow;

    public ModifiedJdiemkeWithVisualization(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph,ArrayList<Point> points,
                           HashSet<Point> processedPoints, double radius) {
        super(new ArrayList<Vector2D>());
        this.graph = graph;
        for(Point p: points)
            graph.addVertex(p);
        centers = new ArrayList<Point>();
        removedEdges = new ArrayList<EdgeWithDistance>();
        this.radius = radius;
        r2 = radius*radius;
        addedPoints = new HashSet<Vector2D>();
        this.processedPoints = processedPoints;
        vis2D = new Online2DVisualizer();
        vis2D.setData(graph, null);
        swingWindow = new SwingWithUpdate(vis2D, 700, 700, "test");
    }

    public void setMaxCoordinate(Set<Point> points){
        for (Point p : points)
            maxOfAnyCoordinate = Math.max(Math.max(p.x, p.y), maxOfAnyCoordinate);
        maxOfAnyCoordinate *= 48;
    }

    protected ArrayList<Point> getNearbyPoints(Point center){
        //delete later, debug stuff
        centers.add(center);

        ArrayList<Point> result = new ArrayList<Point>();
        for(Point p : graph.vertexSet()){
            double dx = center.x - p.x;
            double dy = center.y - p.y;
            if(dx*dx + dy*dy < r2)
                result.add(p);
        }
        return result;
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
    };


    protected void setProcessedPoints(ArrayList<Point> neighbours, ArrayList<Point> hull ){
        HashSet<Point> touchesHull = new HashSet<Point>();
        for( Point x: hull )
            for(DefaultWeightedEdge y: graph.edgesOf(x)){
                touchesHull.add(graph.getEdgeSource(y));
                touchesHull.add(graph.getEdgeTarget(y));
            }
        for( Point x : neighbours )
            if(!touchesHull.contains(x))
                processedPoints.add(x);
    }



    public void initTriangulation(){
        Vector2D p1 = new Vector2D(0d, maxOfAnyCoordinate);
        Vector2D p2 = new Vector2D(maxOfAnyCoordinate, 0d);
        Vector2D p3 = new Vector2D(-maxOfAnyCoordinate, -maxOfAnyCoordinate);

        superTriangle = new Triangle2D(p1, p2, p3);

        triangleSoup.add(superTriangle);
    }

    public void addPoint(Vector2D p){

        if(addedPoints.contains(p))
            return;

        Triangle2D triangle = triangleSoup.findContainingTriangle(p);

        if (triangle == null) {
            /**
             * If no containing triangle exists, then the vertex is not
             * inside a triangle (this can also happen due to numerical
             * errors) and lies on an edge. In order to find this edge we
             * search all edges of the triangle soup and select the one
             * which is nearest to the point we try to add. This edge is
             * removed and four new edges are added.
             */
            Edge2D edge = triangleSoup.findNearestEdge(p);

            Triangle2D first = triangleSoup.findOneTriangleSharing(edge);
            Triangle2D second = triangleSoup.findNeighbour(first, edge);

            Vector2D firstNoneEdgeVertex = first.getNoneEdgeVertex(edge);
            Vector2D secondNoneEdgeVertex = second.getNoneEdgeVertex(edge);

            triangleSoup.remove(first);
            triangleSoup.remove(second);

            Triangle2D triangle1 = new Triangle2D(edge.a, firstNoneEdgeVertex, p);
            Triangle2D triangle2 = new Triangle2D(edge.b, firstNoneEdgeVertex, p);
            Triangle2D triangle3 = new Triangle2D(edge.a, secondNoneEdgeVertex, p);
            Triangle2D triangle4 = new Triangle2D(edge.b, secondNoneEdgeVertex, p);

            triangleSoup.add(triangle1);
            triangleSoup.add(triangle2);
            triangleSoup.add(triangle3);
            triangleSoup.add(triangle4);

            legalizeEdge(triangle1, new Edge2D(edge.a, firstNoneEdgeVertex), p);
            legalizeEdge(triangle2, new Edge2D(edge.b, firstNoneEdgeVertex), p);
            legalizeEdge(triangle3, new Edge2D(edge.a, secondNoneEdgeVertex), p);
            legalizeEdge(triangle4, new Edge2D(edge.b, secondNoneEdgeVertex), p);
        } else {
            /**
             * The vertex is inside a triangle.
             */
            Vector2D a = triangle.a;
            Vector2D b = triangle.b;
            Vector2D c = triangle.c;

            triangleSoup.remove(triangle);

            Triangle2D first = new Triangle2D(a, b, p);
            Triangle2D second = new Triangle2D(b, c, p);
            Triangle2D third = new Triangle2D(c, a, p);

            triangleSoup.add(first);
            triangleSoup.add(second);
            triangleSoup.add(third);

            legalizeEdge(first, new Edge2D(a, b), p);
            legalizeEdge(second, new Edge2D(b, c), p);
            legalizeEdge(third, new Edge2D(c, a), p);
        }

        addedPoints.add(p);
    }

    public void finish(){
        triangleSoup.removeTrianglesUsing(superTriangle.a);
        triangleSoup.removeTrianglesUsing(superTriangle.b);
        triangleSoup.removeTrianglesUsing(superTriangle.c);
    }

    public void displayGraph()
    {
        swingWindow.display();
    }
}
