package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.EdgeWeighter;
import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.Triangulator;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class DomainBasedJdiemkeTriangulator extends JdiemkeTriangulator implements OnlineTriangulator{
	DomainBasedJdiemkeTriangulator(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, ArrayList<Point> points,
			HashSet<Point> processedPoints, double radius) {
		super(graph, points, processedPoints, radius);
	}
	private int domainNumber;
	
	protected ArrayList<Point> setDomain(Point center){
		//delete later, debug stuff
		centers.add(center);
		
		domainNumber++;
		ArrayList<Point> result = new ArrayList<Point>();
		int size = points.size();
		for(int i = 0 ; i < size; i++){
			double dx = center.x - points.get(i).x;
			double dy = center.y - points.get(i).y;
			if(dx*dx + dy*dy < r2){
				if(points.get(i).domains == null)
					points.get(i).domains = new ArrayList<Integer>();
				points.get(i).domains.add(domainNumber);
				result.add(points.get(i));
			}
		}
		return result;
	}
	
	protected void manageEdgeAddition(EdgeWithDistance e, ArrayList<EdgeWithDistance> nearbyEdges){
		for(EdgeWithDistance oldEdge : nearbyEdges)
			if(oldEdge.badlyIntersects(e))				
				if(e.hull || !oldEdge.hull){	
					return;
				}else if(oldEdge.equals(e)){
					oldEdge.hull = false;
					return;
				}
		
		DefaultWeightedEdge edge = graph.addEdge(e.a, e.b);
		if(edge != null){			
			graph.setEdgeWeight(edge, e.length);	
			edges.add(e);	
			nearbyEdges.add(e);
			removedEdges.add(e);
		}		
	} 
	
	protected void manageEdgeAddition(Point a, Point b, ArrayList<EdgeWithDistance> nearbyEdges, ArrayList<Point> hull){
		EdgeWithDistance e = new EdgeWithDistance(a,b, EdgeWeighter.edgeWeight(a, b));
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
	
	protected void manageEdgeAddition(Point a, Point b, ArrayList<EdgeWithDistance> nearbyEdges, ArrayList<Point> hull, ArrayList<Point> affectedPoints){
		EdgeWithDistance e = new EdgeWithDistance(a,b,EdgeWeighter.edgeWeight(a, b));
		e.hull = e.isInHull(hull);	
		EdgeWithDistance hullIntersection = null;
		for(EdgeWithDistance oldEdge : nearbyEdges)
			if(oldEdge.badlyIntersects(e))				
				if(e.hull || !oldEdge.hull){	
					if(!affectedPoints.contains(e.a))
						affectedPoints.add(e.a);
					if(!affectedPoints.contains(e.b))
						affectedPoints.add(e.b);
					return;
				}else if(oldEdge.equals(e)){
					oldEdge.hull = false;
					return;
				}else{
					hullIntersection = oldEdge;			
				}
		
		if(hullIntersection != null){
			if(!affectedPoints.contains(hullIntersection.a))
				affectedPoints.add(hullIntersection.a);
			
			if(!affectedPoints.contains(hullIntersection.b))
				affectedPoints.add(hullIntersection.b);
			
			graph.removeEdge(hullIntersection.a, hullIntersection.b);
			edges.remove(hullIntersection);
		}
		
		DefaultWeightedEdge edge = graph.addEdge(a, b);
		if(edge != null){			
			graph.setEdgeWeight(edge, e.length);	
			edges.add(e);			
			nearbyEdges.add(e);
		}		
	} 
	
	@Override
	public SimpleWeightedGraph<Point, DefaultWeightedEdge> init(Point center) {	
		
		try {						
			ArrayList<Point> neighbours = setDomain(center);
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
			ArrayList<Point> neighbours = setDomain(center);
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
			
			ArrayList<Point> affectedPoints = new ArrayList<Point>();
			for(Triangle2D t : soup){
				manageEdgeAddition((Point)t.a, (Point)t.b, nearbyEdges, hull, affectedPoints);
				manageEdgeAddition((Point)t.c, (Point)t.b, nearbyEdges, hull, affectedPoints);
				manageEdgeAddition((Point)t.a, (Point)t.c, nearbyEdges, hull, affectedPoints);
			}
			
			int size = affectedPoints.size();
			ArrayList<EdgeWithDistance> possibleEdges = new ArrayList<EdgeWithDistance>();
			for(int i = 1; i < size; i++)
				for(int j = 0; j < i; j++)
					if(domainIntersection(affectedPoints.get(i),affectedPoints.get(j)))
						possibleEdges.add(
							new EdgeWithDistance(
								affectedPoints.get(i), 
								affectedPoints.get(j),
								(affectedPoints.get(i).x-affectedPoints.get(j).x)*(affectedPoints.get(i).x-affectedPoints.get(j).x)+
								(affectedPoints.get(i).y-affectedPoints.get(j).y)*(affectedPoints.get(i).y-affectedPoints.get(j).y)
							)
						);
			
			int maxIndex = possibleEdges.size() - 1;
			outerloop:
			for(int i = maxIndex; i >= 0; i--)
				for(int j = 0; j < edges.size(); j++)
					if(possibleEdges.get(i).badlyIntersects(edges.get(j))){
						possibleEdges.remove(i);
						continue outerloop;
					}

			for(int i = 0 ; i < possibleEdges.size(); i++){
				boolean swaps = false;
				for(int j = 0 ; j < possibleEdges.size() - 1; j++)
					if(possibleEdges.get(j).length > possibleEdges.get(j+1).length){
						EdgeWithDistance temp = possibleEdges.get(j);
						possibleEdges.set(j, possibleEdges.get(j+1));
						possibleEdges.set(j+1, temp);
					}				
				if(!swaps)
					break;
			}
			
			for(int i = 0; i < possibleEdges.size(); i++)
				manageEdgeAddition(possibleEdges.get(i), nearbyEdges);
			
				
			setProcessedPoints(neighbours,hull);
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		
		return graph;
	}
	
	public boolean domainIntersection(Point a, Point b){
		for(Integer x : a.domains)
			for(Integer y : b.domains)
				if(y.equals(x))
					return true;
		return false;
	}

	public void visualizeDebug(){
		JdiemkeTriangulatorVisualizer vis = new JdiemkeTriangulatorVisualizer();
        vis.setData(points,edges,removedEdges,centers,radius);
        SwingWindow.start(vis, 700, 700, "internal graph");	
	}
	
}

