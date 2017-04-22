package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.TrianglesToGraphConverter;
import org.educationalProject.surfacePathfinder.Triangulator;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class UnsafeJdiemkeTriangulator extends JdiemkeTriangulator implements OnlineTriangulator{
	UnsafeJdiemkeTriangulator(SimpleWeightedGraph<Point, DefaultWeightedEdge> graph, Vector<Point> points,
			HashSet<Point> processedPoints, double radius) {
		super(graph, points, processedPoints, radius);
	}
	

	
	protected void manageEdgeAddition(Point a, Point b, Vector<EdgeWithDistance> nearbyEdges, Vector<Point> hull){
		EdgeWithDistance e = new EdgeWithDistance(a,b,TrianglesToGraphConverter.edgeWeight(a, b));
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
			setProcessedPoints(neighbours,hull);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		
		
		return graph;
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
			setProcessedPoints(neighbours,hull);
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		
		return graph;
	}
	


	public void visualizeDebug(){
		JdiemkeTriangulatorVisualizer vis = new JdiemkeTriangulatorVisualizer();
        vis.setData(points,edges,removedEdges,centers,radius);
        SwingWindow.start(vis, 700, 700, "internal graph");	
	}
	
}
