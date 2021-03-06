package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GraphProxy implements WeightedGraph<Point, DefaultWeightedEdge>,UndirectedGraph<Point, DefaultWeightedEdge>{
	private double radius;
	private SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
	private OnlineTriangulator triangulator;
	private HashSet<Point> processedPoints;
	private boolean firstUpdate = true;
	
	public GraphProxy(double radius, ArrayList<Point> points, String className){
		processedPoints = new HashSet<Point>((int)(points.size()*1.5));
		this.radius = radius;
		this.graph = new SimpleWeightedGraph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		if(className.equals("UnsafeJdiemkeTriangulator"))
			triangulator = new UnsafeJdiemkeTriangulator(graph, points, processedPoints, radius);
		else if(className.equals("ModifiedJdiemke"))
			triangulator = new ModifiedJdiemke(graph, points, processedPoints, radius);
		else
			System.out.println("Invalid triangulator name!");
		
	}
	public GraphProxy(double radius, ArrayList<Point> points, MainDemoWindow demo){
		processedPoints = new HashSet<Point>();
		this.radius = radius;
		this.graph = new SimpleWeightedGraph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		triangulator = new ModifiedJdiemkeWithVisualization(graph, points, processedPoints, radius, demo);
	}
	private boolean notProcessed(Point p){
		return !processedPoints.contains(p);
	}
	
	private void update(Point vertex){
		if(firstUpdate){
			triangulator.init(vertex);
			firstUpdate = false;
		}else{
			triangulator.update(vertex);
		}
	}
	
	//only the needed methods will be overridden
	@Override
	public DefaultWeightedEdge getEdge(Point sourceVertex, Point targetVertex) {
		if(notProcessed(sourceVertex))
			update(sourceVertex);
		if(notProcessed(targetVertex))
			update(targetVertex);
		return graph.getEdge(sourceVertex, targetVertex);
	}
	
	@Override
	public Set<Point> vertexSet() {
		return graph.vertexSet();
	}
	@Override
	public int degreeOf(Point vertex) {
		if(notProcessed(vertex))
			update(vertex);
		return graph.degreeOf(vertex);
	}
	
	@Override
	public Point getEdgeSource(DefaultWeightedEdge e) {
		return graph.getEdgeSource(e);
	}
	@Override
	public Point getEdgeTarget(DefaultWeightedEdge e) {
		return graph.getEdgeTarget(e);
	}
	@Override
	public double getEdgeWeight(DefaultWeightedEdge e) {
		return graph.getEdgeWeight(e);
	}
	
	@Override
	public Set<DefaultWeightedEdge> edgesOf(Point vertex) {
		if(notProcessed(vertex))
			update(vertex);
		
		return graph.edgesOf(vertex);
	}
	@Override
	public boolean containsEdge(Point sourceVertex, Point targetVertex) {
		if(notProcessed(sourceVertex))
			update(sourceVertex);
		if(notProcessed(targetVertex))
			update(targetVertex);
		return graph.containsEdge(sourceVertex, targetVertex);
	}
	/*
	 * Danger : undefined results
	 * */
	@Override
	public boolean containsEdge(DefaultWeightedEdge e) {
		return graph.containsEdge(e);
	}
	@Override
	public boolean containsVertex(Point v) {
		return graph.containsVertex(v);
	}

	
	
	/**
	 * returns only current edges
	 * */
	public Set<DefaultWeightedEdge> edgeSet() {
		return graph.edgeSet();
	}
	/**
	 * returns only current edges
	 * */
	public Set<DefaultWeightedEdge> getAllEdges(Point sourceVertex, Point targetVertex) {
		return graph.getAllEdges(sourceVertex, targetVertex);
	}
	
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> getGraphClone(){
		return (SimpleWeightedGraph<Point, DefaultWeightedEdge>) graph.clone();
	}
	
	@Override
	public EdgeFactory getEdgeFactory() {return null;}
	@Override
	public boolean removeAllEdges(Collection edges) {return false;}	
	@Override
	public boolean removeAllVertices(Collection vertices) {return false;}	
	@Override
	public DefaultWeightedEdge addEdge(Point sourceVertex, Point targetVertex) {return null;}
	@Override
	public boolean addEdge(Point sourceVertex, Point targetVertex, DefaultWeightedEdge e) {return graph.addEdge(sourceVertex, targetVertex, e);}
	@Override
	public boolean addVertex(Point v) {return false;}
	@Override
	public Set<DefaultWeightedEdge> removeAllEdges(Point sourceVertex, Point targetVertex) {return null;}
	@Override
	public DefaultWeightedEdge removeEdge(Point sourceVertex, Point targetVertex) {return null;}
	@Override
	public boolean removeEdge(DefaultWeightedEdge e) {return graph.removeEdge(e);}
	@Override
	public boolean removeVertex(Point v) {return false;}
	@Override
	public void setEdgeWeight(DefaultWeightedEdge e, double weight) {}

	
}
