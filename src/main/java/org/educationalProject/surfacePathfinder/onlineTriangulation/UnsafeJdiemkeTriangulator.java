package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.EdgeWeighter;
import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.Triangulator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class UnsafeJdiemkeTriangulator extends JdiemkeTriangulator implements OnlineTriangulator{
	UnsafeJdiemkeTriangulator(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, ArrayList<Point> points,
			HashSet<Point> processedPoints, double radius) {
		super(graph, points, processedPoints, radius);
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
	
	protected void manageEdgeAddition(Point a, Point b, ArrayList<EdgeWithDistance> nearbyEdges, ArrayList<Point> hull){
		EdgeWithDistance e = new EdgeWithDistance(a,b,EdgeWeighter.edgeWeight(a, b));
		e.hull = e.isInHull(hull);	
		EdgeWithDistance hullIntersection = null;
		for(EdgeWithDistance oldEdge : nearbyEdges)
			if(oldEdge.badlyIntersects(e))				
				if(e.hull || !oldEdge.hull){	
					return;
				}else if(oldEdge.equals(e)){
					oldEdge.hull = false;
					return;
				}else{
					hullIntersection = oldEdge;			
				}
		
		if(hullIntersection != null){
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
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> init(Point center) {	
		
		try {						
			ArrayList<Point> neighbours = getNearbyPoints(center);
			ArrayList<Point> hull = QuickHull.findHull(neighbours);
			
			//this is equivalent to (at least I hope so):
			//ArrayList<Vector2D> neighboursVector = new ArrayList<Vector2D>(neighbours.size());
			//for(Point x : neighbours)
			//	neighboursVector.add(x);
			ArrayList<Vector2D> neighboursVector = (ArrayList<Vector2D>)(ArrayList<? extends Vector2D>)neighbours;
			
			List<Triangle2D> soup = Triangulator.triangulate(neighboursVector);
			
			for(Triangle2D t : soup){
				manageEdgeAddition((Point)t.a, (Point)t.b, edges, hull);
				manageEdgeAddition((Point)t.c, (Point)t.b, edges, hull);
				manageEdgeAddition((Point)t.a, (Point)t.c, edges, hull);
			}			
			setProcessedPoints(neighbours,hull);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		
		
		return graph;
	}
	
	@Override
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> update(Point center) {
		try {
			ArrayList<Point> neighbours = getNearbyPoints(center);
			ArrayList<Point> hull = QuickHull.findHull(neighbours);
			
			//this is equivalent to (at least I hope so):
			//ArrayList<Vector2D> neighboursVector = new ArrayList<Vector2D>(neighbours.size());
			//for(Point x : neighbours)
			//	neighboursVector.add(x);
			ArrayList<Vector2D> neighboursVector = (ArrayList<Vector2D>)(ArrayList<? extends Vector2D>)neighbours;
			
			List<Triangle2D> soup = Triangulator.triangulate(neighboursVector);
			
			ArrayList<EdgeWithDistance> nearbyEdges = new ArrayList<EdgeWithDistance>();
			for(EdgeWithDistance e : edges)
				if(neighbours.contains(e.a) || neighbours.contains(e.b))
					nearbyEdges.add(e);
			
			for(Triangle2D t : soup){
				manageEdgeAddition((Point)t.a, (Point)t.b, nearbyEdges, hull);
				manageEdgeAddition((Point)t.c, (Point)t.b, nearbyEdges, hull);
				manageEdgeAddition((Point)t.a, (Point)t.c, nearbyEdges, hull);
			}
			setProcessedPoints(neighbours,hull);
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		
		return graph;
	}
	

}
