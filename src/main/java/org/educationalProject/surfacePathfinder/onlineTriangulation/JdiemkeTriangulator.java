package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.TrianglesToGraphConverter;
import org.educationalProject.surfacePathfinder.Triangulator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class JdiemkeTriangulator implements OnlineTriangulator{
	private SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
	private double radius;
	private double r2;
	private Vector<Point> points;
	private Vector<EdgeWithDistance> edges;
	private HashSet<Point> processedPoints;
	
	JdiemkeTriangulator(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, Vector<Point> points, HashSet<Point> processedPoints, double radius){
		this.edges = new Vector<EdgeWithDistance>();
		this.processedPoints = processedPoints;
		this.points = points;
		this.graph = graph;
		for(Point v : points)
			graph.addVertex(v);
		this.radius = radius;
		r2 = radius * radius;
	}
	
	private Vector<Point> getNearbyPoints(Point center){
		Vector<Point> result = new Vector<Point>();
		int size = points.size();
		for(int i = 0 ; i < size; i++){
			double dx = center.x - points.get(i).x;
			double dy = center.y - points.get(i).y;
			if(dx*dx + dy*dy < r2)
				result.add(points.get(i));
		}
		return result;
	}
	
	@Override
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> init(Point center) {	
		
		try {						
			Vector<Point> neighbours = getNearbyPoints(center);
			Vector<Point> hull = QuickHull.findHull(neighbours);
			
			//this is equivalent to (at least I hope so):
			//Vector<Vector2D> neighboursVector = new Vector<Vector2D>(neighbours.size());
			//for(Point x : neighbours)
			//	neighboursVector.add(x);
			Vector<Vector2D> neighboursVector = (Vector<Vector2D>)(Vector<? extends Vector2D>)neighbours;
			
			List<Triangle2D> soup = Triangulator.triangulate(neighboursVector);
			
			TrianglesToGraphConverter.setParams(0.5, 16);
			for(Triangle2D t : soup){
				manageEdgeAddition((Point)t.a, (Point)t.b, edges, hull);
				manageEdgeAddition((Point)t.c, (Point)t.b, edges, hull);
				manageEdgeAddition((Point)t.a, (Point)t.c, edges, hull);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		
		
		return graph;
	}
	
	public void manageEdgeAddition(Point a, Point b, Vector<EdgeWithDistance> nearbyEdges, Vector<Point> hull){
		EdgeWithDistance e = new EdgeWithDistance(a,b,TrianglesToGraphConverter.edgeWeight(a, b));
		//e.hull = e.isInHull(hull);	
		
		EdgeWithDistance hullIntersection = null;
		for(EdgeWithDistance oldEdge : nearbyEdges)
			if(oldEdge.badlyIntersects(e))
				if(e.hull){
					return;
				}else{
					if(!oldEdge.hull){
						return;
					}else{
						hullIntersection = oldEdge;
					}
				}
		if(hullIntersection!=null){
			graph.removeEdge(hullIntersection.a, hullIntersection.b);
			edges.remove(hullIntersection);
		}
				
		DefaultWeightedEdge edge = graph.addEdge(a, b);
		if(edge != null){			
			graph.setEdgeWeight(edge, e.length);	
			edges.add(e);			
		}		
	}
	
	@Override
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> update(Point center) {
		try {
			Vector<Point> neighbours = getNearbyPoints(center);
			Vector<Point> hull = QuickHull.findHull(neighbours);
			
			//this is equivalent to (at least I hope so):
			//Vector<Vector2D> neighboursVector = new Vector<Vector2D>(neighbours.size());
			//for(Point x : neighbours)
			//	neighboursVector.add(x);
			Vector<Vector2D> neighboursVector = (Vector<Vector2D>)(Vector<? extends Vector2D>)neighbours;
			
			List<Triangle2D> soup = Triangulator.triangulate(neighboursVector);
			
			Vector<EdgeWithDistance> nearbyEdges = new Vector<EdgeWithDistance>();
			for(EdgeWithDistance e : edges)
				if(neighbours.contains(e.a) || neighbours.contains(e.b))
					nearbyEdges.add(e);
			
			TrianglesToGraphConverter.setParams(0.5, 16);
			for(Triangle2D t : soup){
				manageEdgeAddition((Point)t.a, (Point)t.b, nearbyEdges, hull);
				manageEdgeAddition((Point)t.c, (Point)t.b, nearbyEdges, hull);
				manageEdgeAddition((Point)t.a, (Point)t.c, nearbyEdges, hull);
			}
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		
		return graph;
	}
	

	
	
	private class EdgeWithDistance{
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
		public boolean isInHull(Vector<Point> hull){
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

	
	
}
