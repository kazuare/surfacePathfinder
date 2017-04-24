package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public abstract class JdiemkeTriangulator implements OnlineTriangulator{
	protected SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
	protected double radius;
	protected double r2;
	protected ArrayList<Point> points;
	protected ArrayList<Point> centers;
	protected ArrayList<EdgeWithDistance> removedEdges;
	protected ArrayList<EdgeWithDistance> edges;
	protected HashSet<Point> processedPoints;

	@Override
	public abstract SimpleWeightedGraph<Point, DefaultWeightedEdge> init(Point center);
	@Override
	public abstract SimpleWeightedGraph<Point, DefaultWeightedEdge> update(Point center);
	
	JdiemkeTriangulator(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, ArrayList<Point> points, HashSet<Point> processedPoints, double radius){
		this.edges = new ArrayList<EdgeWithDistance>();
		this.processedPoints = processedPoints;
		this.points = points;
		this.graph = graph;
		for(Point v : points)
			graph.addVertex(v);
		this.radius = radius;
		r2 = radius * radius;
		//delete later, debug stuff
		centers = new ArrayList<Point>();
		removedEdges = new ArrayList<EdgeWithDistance>();
	}
	
	protected ArrayList<Point> getNearbyPoints(Point center){
		//delete later, debug stuff
		centers.add(center);
		
		ArrayList<Point> result = new ArrayList<Point>();
		int size = points.size();
		for(int i = 0 ; i < size; i++){
			//not sure if correct
			//if(processedPoints.contains(points.get(i)))
			//	continue;
			
			double dx = center.x - points.get(i).x;
			double dy = center.y - points.get(i).y;
			if(dx*dx + dy*dy < r2)
				result.add(points.get(i));
		}
		return result;
	}
	
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
	
	protected class EdgeWithDistance{
		public Point a;
		public Point b;
		public double length;
		public boolean hull = false;
		@Override
		public int hashCode(){
			return (int)(10000*(a.x*a.y+b.x*b.y));
			
		}
		public EdgeWithDistance(Point a, Point b, double d){
			this.a = a; 
			this.b = b;
			this.length = d;
		}
		public boolean badlyIntersects(EdgeWithDistance e){
			if(this.equals(e))
				return true;
			if(
				a.equals(e.a)||
				a.equals(e.b)||
				b.equals(e.a)||
				b.equals(e.b)
			)return false;
				
			return Line2D.linesIntersect(a.x, a.y, b.x, b.y, e.a.x, e.a.y, e.b.x, e.b.y);
			
		}		
		public boolean equals(EdgeWithDistance e){
			return a.equals(e.a)&&b.equals(e.b) || b.equals(e.a)&&a.equals(e.b);
		}
		public boolean isInHull(ArrayList<Point> hull){
			boolean inHull = false;
			
			for(int i = 0; i < hull.size()-1; i++){
				if( hull.get(i).equals(a) && hull.get(i+1).equals(b) ){
					inHull = true;
					break;
				}
				if( hull.get(i).equals(b) && hull.get(i+1).equals(a) ){
					inHull = true;
					break;
				}
			}
			
			if( hull.get(0).equals(a) && hull.get(hull.size()-1).equals(b) )
				inHull = true;
			if( hull.get(0).equals(b) && hull.get(hull.size()-1).equals(a) )
				inHull = true;
			
			return inHull;
		}
	}

	public void visualizeDebug(){
		JdiemkeTriangulatorVisualizer vis = new JdiemkeTriangulatorVisualizer();
        vis.setData(points,edges,removedEdges,centers,radius);
        SwingWindow.start(vis, 700, 700, "internal graph");	
	}
	
}
