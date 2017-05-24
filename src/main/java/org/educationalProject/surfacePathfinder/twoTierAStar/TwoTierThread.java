package org.educationalProject.surfacePathfinder.twoTierAStar;

import java.util.ArrayList;
import java.util.List;

import org.educationalProject.surfacePathfinder.EuclidianEuristicWithAltitude;
import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import io.github.jdiemke.triangulation.Vector2D;

public class TwoTierThread implements Runnable{
	ArrayList<Point> points; 
	Point a; 
	Point b;
	double TRIANGULATION_RADIUS;
	double ALTITUDE_MULTIPLIER;
	List<Point> partialNodes;
	TwoTierThread(ArrayList<Vector2D> points, Point a, Point b, double TRIANGULATION_RADIUS, double ALTITUDE_MULTIPLIER){
		this.points = (ArrayList<Point>)(ArrayList<? extends Vector2D>)points;
		this.a = a;
		this.b = b;
		this.TRIANGULATION_RADIUS = TRIANGULATION_RADIUS;
		this.ALTITUDE_MULTIPLIER = ALTITUDE_MULTIPLIER;
	}

	@Override
	public void run() {

        GraphProxy partialGraph;
        AStarShortestPath<Point,DefaultWeightedEdge> partialAStar; 
		partialGraph = new GraphProxy(
			1.5*TRIANGULATION_RADIUS, 
			points, 
	    	"ModifiedJdiemke"
	    );
	            
		partialAStar = new AStarShortestPath<Point,DefaultWeightedEdge>(
			partialGraph,
			new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
	    ); 
	            
	    partialNodes = partialAStar.getPath(a, b).getVertexList();
	            
		
	}
	
	
}
