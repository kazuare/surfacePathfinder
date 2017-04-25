package org.educationalProject.surfacePathfinder;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
* takes in some triangles, adds all the satisfactory triangle edges to the graph
*/
public class EdgeValidator {
	private static double COS_THRESHOLD;
	
	public static void setParams(double threshold){
		COS_THRESHOLD = threshold;
	}
	
	public static boolean judge(Point a, Point b){
		if(
			Math.sqrt(
				(a.x-b.x)*(a.x-b.x)+
				(a.y-b.y)*(a.y-b.y)
			)
			/
			Math.sqrt(
				(a.x-b.x)*(a.x-b.x)+
				(a.y-b.y)*(a.y-b.y)+
				(a.alt-b.alt)*(a.alt-b.alt)
			)
			<= 
			COS_THRESHOLD
		)
			return false;
		return true;
	}
	
}
