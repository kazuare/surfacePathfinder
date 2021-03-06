package org.educationalProject.surfacePathfinder.twoTierAStar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.educationalProject.surfacePathfinder.EdgeWeighter;
import org.educationalProject.surfacePathfinder.EuclidianEuristicWithAltitude;
import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Vector2D;

public class TwoTierAStar {
	double COS_THRESHOLD;
	double ALTITUDE_MULTIPLIER;
	double TRIANGULATION_RADIUS;
	ArrayList<Vector2D> points;
	ArrayList<Vector2D> roughPoints;
	int step;
	public TwoTierAStar(ArrayList<Vector2D> p, int pointsInRough, int roughPathStep, double COS_THRESHOLD, double ALTITUDE_MULTIPLIER, double TRIANGULATION_RADIUS){
		this.COS_THRESHOLD = COS_THRESHOLD;
		this.ALTITUDE_MULTIPLIER = ALTITUDE_MULTIPLIER;
		this.TRIANGULATION_RADIUS = TRIANGULATION_RADIUS;
		points = (ArrayList<Vector2D>) p.clone();
		Collections.shuffle(points, new Random(1));
		roughPoints = new ArrayList<Vector2D>(points.subList(0, pointsInRough));
		step = roughPathStep;
	}
	public ArrayList<Point> findPath(Point a, Point b) throws NotEnoughPointsException{
		if(!roughPoints.contains((Vector2D)a))
			roughPoints.add((Vector2D)a);
		
		if(!roughPoints.contains((Vector2D)b))
			roughPoints.add((Vector2D)b);
             
        GraphProxy roughGraph = new GraphProxy(
            	1.5*TRIANGULATION_RADIUS, 
                (ArrayList<Point>)(ArrayList<? extends Vector2D>)roughPoints, 
                "ModifiedJdiemke"
            );
        
        AStarShortestPath<Point,DefaultWeightedEdge> roughAStar =
        	new AStarShortestPath<Point,DefaultWeightedEdge>(
        		roughGraph,
        		new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
        	);        
        List<Point> roughNodes = roughAStar.getPath(a, b).getVertexList();
        
        List<Point> filteredNodes = new ArrayList<Point>();
        for(int i = 0; i < roughNodes.size() - 1; i += step)
        	filteredNodes.add(roughNodes.get(i));
        filteredNodes.add(roughNodes.get(roughNodes.size()-1));
        
        ArrayList<Point> morePrecisePath = getMorePrecisePath(filteredNodes);
        
        DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
        vis.setData(roughGraph, morePrecisePath);
        SwingWindow.start(vis, 700, 700, "final map");
        
        return morePrecisePath;
	}
	
	public ArrayList<Point> getMorePrecisePath(List<Point>filteredNodes){
 
        ArrayList<Point> finalNodes = new ArrayList<Point>();
        
        GraphProxy partialGraph;
        AStarShortestPath<Point,DefaultWeightedEdge> partialAStar; 
        
        for(int i = 0; i < filteredNodes.size() - 1; i++){
        	
            partialGraph = new GraphProxy(
            	1.5*TRIANGULATION_RADIUS, 
                (ArrayList<Point>)(ArrayList<? extends Vector2D>)points, 
                "ModifiedJdiemke"
            );
            
            partialAStar = new AStarShortestPath<Point,DefaultWeightedEdge>(
            	partialGraph,
            	new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
            ); 
            
            List<Point> partialNodes = partialAStar.getPath(filteredNodes.get(i), filteredNodes.get(i+1)).getVertexList();
            
            for(int j = 0; j < partialNodes.size(); j++)
            	if(finalNodes.size()==0 || !finalNodes.get(finalNodes.size()-1).equals(partialNodes.get(j)))
            		finalNodes.add(partialNodes.get(j));
        	
        }
        
        
        return finalNodes;
	}
	
	public static void analyzeResult(List<Point> path, SimpleWeightedGraph<Point,DefaultWeightedEdge> preparedGraph,  Point a, Point b){
		if(path.get(0).equals(a))
			System.out.println("path start is correct");
		else
			System.out.println("path start is _not_ correct");
		
		if(path.get(path.size()-1).equals(b))
			System.out.println("path end is correct");
		else
			System.out.println("path end is _not_ correct");
	
		boolean invalid = false;
		for(int i = 0; i < path.size()-1; i++){
			if(
				!preparedGraph.containsEdge(path.get(i), path.get(i+1))&&
				!preparedGraph.containsEdge(path.get(i+1), path.get(i))
			){
				System.out.println("path is incorrect at index " + i);
				System.out.println(path.get(i) + " to " + path.get(i+1));
				invalid = true;
				break;
			}
		}
		if(!invalid){
			System.out.println("path is OK");
			printPathWeight(path);
		}
		
	}
	
	public static void printPathWeight(List<Point> path){
		double weight = 0;
		for(int i = 0; i < path.size()-1; i++){
			weight += EdgeWeighter.edgeWeight(path.get(i),path.get(i+1));
		}
		System.out.println("weight is " + weight);
	}
}