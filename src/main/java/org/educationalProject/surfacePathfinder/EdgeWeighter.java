package org.educationalProject.surfacePathfinder;
/**
* takes in some triangles, adds all the satisfactory triangle edges to the graph
*/
public class EdgeWeighter {
	private static double COST_MULTIPLIER;
	
	public static void setParams(double multiplier){
		COST_MULTIPLIER = multiplier;
	}
	
	public static double edgeWeight(Point a, Point b){
		return Math.abs(a.alt-b.alt) * COST_MULTIPLIER +
				Math.sqrt(
					(a.x-b.x)*(a.x-b.x)+
					(a.y-b.y)*(a.y-b.y)+
					(a.alt-b.alt)*(a.alt-b.alt)
				);
	}
	
}
