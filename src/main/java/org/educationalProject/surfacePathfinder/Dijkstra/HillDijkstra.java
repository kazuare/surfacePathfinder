package org.educationalProject.surfacePathfinder.Dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class HillDijkstra extends Dijkstra{
	
	private double gamma = 0.8;
	
	private double XYDistance(Point a, Point b){
		return Math.sqrt(
				(a.x-b.x) * (a.x-b.x)
				+
				(a.y-b.y) * (a.y-b.y)
			);
	}
	private double XYAltDistance(Point a, Point b){
		return Math.sqrt(
				(a.x-b.x) * (a.x-b.x)
				+
				(a.y-b.y) * (a.y-b.y)
				+
				(a.alt-b.alt) * (a.alt-b.alt)
			);
	}
	public double hillness(Route route){
		ArrayList<Double> angles = getAngles(route);
		return 1 - gamma * Math.cos((maxAngle(angles)+avgAngle(angles))/2) ;
		
	}
	public ArrayList<Double> getAngles(Route route){
		ArrayList<Double> angles = new ArrayList<Double>();
		if(route.vertices.size() < 2)
			return angles;
		
		if(route.vertices.size() == 2){
			Point a = route.vertices.get(0);
			Point b = route.vertices.get(1);
			
			double angle1 = Math.acos(XYDistance(a,b)/XYAltDistance(a,b));
			if(a.alt < b.alt)
				angle1 = -angle1;
			
			angles.add(Math.abs(angle1));
			
			return angles;
		}
		for(int i = 1; i < route.vertices.size()-1; i++){
			Point a = route.vertices.get(i - 1);
			Point b = route.vertices.get(i);
			Point c = route.vertices.get(i + 1);
			
			double angle1 = Math.acos(XYDistance(a,b)/XYAltDistance(a,b));
			if(a.alt < b.alt)
				angle1 = -angle1;
			double angle2 = Math.acos(XYDistance(b,c)/XYAltDistance(b,c));
			if(b.alt > c.alt)
				angle2 = -angle2;
			angles.add(Math.abs(angle1 + angle2));
		}
		return angles;
			
	}
	public double maxAngle(ArrayList<Double> angles){
		double maxAngle = 0;
		for(double angle : angles)
			if(angle > maxAngle)
				maxAngle = angle;
		return maxAngle;
	}
	public double avgAngle(ArrayList<Double> angles){
		if(angles.size() == 0)
			return 0;
		double sum = 0;
		for(double angle : angles)
			sum+=angle;
		return sum/angles.size();
	}
	
	public Point findMin(HashMap<Point,Route> routes){
		Point minId = null;
		
		for(Point node : notVisited){
			if(minId == null)
				minId = node;
			else{ 
				if(routes.get(node).hillness == -1 )
					routes.get(node).hillness = hillness(routes.get(node));
				if(routes.get(minId).hillness == -1 )
					routes.get(minId).hillness = hillness(routes.get(minId));
				
				if(	routes.get(minId).hillness * routes.get(minId).length > routes.get(node).hillness * routes.get(node).length)
					minId = node;
			
			}
		}
		
		return minId;
		
	}
	
}




