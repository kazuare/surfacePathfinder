package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.EdgeWeighter;
import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GreedyTriangulator implements OnlineTriangulator{
	private SimpleWeightedGraph<Point, DefaultWeightedEdge> graph;
	private double radius;
	private double r2;
	private ArrayList<Point> points;
	private ArrayList<EdgeWithDistance> edges;
	private HashSet<Point> processedPoints;
	GreedyTriangulator(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, ArrayList<Point> points, HashSet<Point> processedPoints, double radius){
		this.processedPoints = processedPoints;
		this.points = points;
		this.graph = graph;
		for(Point v : points)
			graph.addVertex(v);
		this.radius = radius;
		r2 = radius * radius;
	}
	
	private ArrayList<Point> getNearbyPoints(Point center){
		ArrayList<Point> result = new ArrayList<Point>();
		int size = points.size();
		for(int i = 0 ; i < size; i++){
			double dx = center.x - points.get(i).x;
			double dy = center.y - points.get(i).y;
			if(dx*dx + dy*dy < r2)
				result.add(points.get(i));
		}
		return result;
	}
	
	public static int compareDoubles(double d1, double d2){
		if(d1 > d2)
			return -1;
		if(d1 < d2)
			return 1;
		return 0;
	}
	
	@Override
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> init(Point center) {		
		try {
			
			ArrayList<Point> neighbours = getNearbyPoints(center);
			ArrayList<Point> hull = QuickHull.findHull(neighbours);
			
			int size = neighbours.size();
			
			Comparator<EdgeWithDistance> cmp = getEdgeComparator();
			
			TreeSet<EdgeWithDistance> sortedEdges = new TreeSet<EdgeWithDistance>(cmp);
			for(int i = 1; i < size; i++)
				for(int j = 0; j < i; j++)
					sortedEdges.add(
							new EdgeWithDistance(
									neighbours.get(i), 
									neighbours.get(j),
									(neighbours.get(i).x-neighbours.get(j).x)*(neighbours.get(i).x-neighbours.get(j).x)+
									(neighbours.get(i).y-neighbours.get(j).y)*(neighbours.get(i).y-neighbours.get(j).y)
									)
						);

			
			edges = removeBadEdges(sortedEdges, hull);
			
			for(EdgeWithDistance e : edges)
				e.fixed = true;

			int edgesSize = edges.size();
			for(int i = 0; i < edgesSize; i++){
				Point a = edges.get(i).a;
				Point b = edges.get(i).b;
				
				DefaultWeightedEdge e = graph.addEdge(a, b);
				graph.setEdgeWeight(e, EdgeWeighter.edgeWeight(a, b));
			}			
			
			setProcessedPoints(neighbours,hull);
			
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		
		return graph;
	}

	@Override
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> update(Point center) {
		try {
			ArrayList<Point> neighbours = getNearbyPoints(center);
			ArrayList<Point> hull = QuickHull.findHull(neighbours);
			
			Comparator<EdgeWithDistance> cmp = getEdgeComparator();
			TreeSet<EdgeWithDistance> sortedEdges = new TreeSet<EdgeWithDistance>(cmp);
			
			for(EdgeWithDistance e : edges)
				if(neighbours.contains(e.a) || neighbours.contains(e.b))
					sortedEdges.add(e);

			int size = neighbours.size();
			for(int i = 1; i < size; i++)
				for(int j = 0; j < i; j++){
					EdgeWithDistance current = new EdgeWithDistance(
						neighbours.get(i), 
						neighbours.get(j),
						(neighbours.get(i).x-neighbours.get(j).x)*(neighbours.get(i).x-neighbours.get(j).x)+
						(neighbours.get(i).y-neighbours.get(j).y)*(neighbours.get(i).y-neighbours.get(j).y)
					);
					if(!sortedEdges.contains(current))
						sortedEdges.add(current);
				}
					
			ArrayList<EdgeWithDistance> goodEdges = removeBadEdges(sortedEdges, hull);
			for(EdgeWithDistance edge : goodEdges){
				if(!edge.fixed){
					edge.fixed = true;
					edges.add(edge);
					
					Point a = edge.a;
					Point b = edge.b;		
					
					DefaultWeightedEdge e = graph.addEdge(a, b);
					graph.setEdgeWeight(e, EdgeWeighter.edgeWeight(a, b));
				}
			}
			
			setProcessedPoints(neighbours,hull);
			
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		
		return graph;
	}
	
	private ArrayList<EdgeWithDistance> removeBadEdges(TreeSet<EdgeWithDistance> tree, ArrayList<Point> hull ){
		
		ArrayList<EdgeWithDistance> edges = new ArrayList<EdgeWithDistance>(tree.size());
		
		Iterator<EdgeWithDistance> it = tree.descendingIterator();

		while(it.hasNext()){
			EdgeWithDistance edge = it.next();
			
			if(edge.isInHull(hull))
				continue;
			
			int edgesSize = edges.size();
			int k = 0;
			boolean toAdd = false;
			for(; k < edgesSize; k++)
				if(edge.badlyIntersects(edges.get(k))){
					break;
				}
			if(k == edgesSize)
				toAdd = true;
			if(toAdd)
				edges.add(k, edge);	
			
		}	
		return edges;
	}
	
	private void setProcessedPoints(ArrayList<Point> neighbours, ArrayList<Point> hull ){
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
	
	private class EdgeWithDistance{
		public Point a;
		public Point b;
		public double length;
		public boolean fixed;
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

	public static Comparator<EdgeWithDistance> getEdgeComparator(){
		 return (e1, e2) -> {
			int compare = 0;
			
			if(e1.fixed && !e2.fixed)
				return 1;
			if(!e1.fixed && e2.fixed)
				return -1;
			
			compare = compareDoubles(e1.length, e2.length);
			if(compare != 0)
				return compare;
			
			compare = compareDoubles(e1.a.x, e2.a.x);
			if(compare != 0)
				return compare;
			
			compare = compareDoubles(e1.a.y, e2.a.y);
			if(compare != 0)
				return compare;
			
			compare = compareDoubles(e1.b.x, e2.b.x);
			if(compare != 0)
				return compare;
			
			compare = compareDoubles(e1.b.y, e2.b.y);
			if(compare != 0)
				return compare;
			
			return 0;
		};
	}
	
}
