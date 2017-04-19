package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.util.Vector;

import org.educationalProject.surfacePathfinder.Point;

public class QuickHull{
	public static Vector<Point> findHull(Vector<Point> points) throws Exception{
		
		Vector<Point> hull = new Vector<Point>();
		if (points.size() < 3)
            	throw new Exception("Not enough points!");
		
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		Point min = null;
		Point max = null;
		for(Point a : points){
			if(minX > a.x){
				minX = a.x;
				min = a;
			}
			if(maxX < a.x){
				maxX = a.x;
				max = a;
			}
		}
		hull.add(min);
		hull.add(max);
		
        Vector<Point> right = new Vector<Point>();
        Vector<Point> left = new Vector<Point>();

        for(Point a : points){
        	if(a.equals(min) || a.equals(max))
        		continue;
        	if (allocation(min, max, a) == -1)
        		left.add(a);
        	if (allocation(min, max, a) == 1)
        		right.add(a);
        }

        hullRec(max, min, left, hull);
        hullRec(min, max, right, hull);
        
        return hull;
    }

    private static double dist(Point a, Point b, Point p){
        return Math.abs( (b.x - a.x) * (a.y - p.y) - (b.y - a.y) * (a.x - p.x) );
    }

    private static int allocation(Point a, Point b, Point p){
        return (int)Math.signum( (b.x-a.x)*(p.y-a.y) - (p.x-a.x)*(b.y-a.y) );
    }

    private static void hullRec(Point a, Point b, Vector<Point> points, Vector<Point> hull){

        if (points.size() == 0)
            return;
        
        int pos = hull.indexOf(b);

        if (points.size() == 1){
            Point p = points.get(0);
            points.remove(p);
            hull.add(pos, p);
            return;
        }

        double dist = Double.NEGATIVE_INFINITY;        
        Point target = null;
        
        for( Point p : points )
        	if(dist(a, b, p) > dist){
        		target = p;
        		dist = dist(a, b, p);
        	}
        
        points.remove(target);
        hull.add(pos, target);

        Vector<Point> l1 = new Vector<Point>();
        for(Point p : points)
        	if (allocation(a, target, p) == 1)
                l1.add(p);
        
        Vector<Point> l2 = new Vector<Point>();
        for(Point p : points)
        	if (allocation(target, b, p) == 1)
                l2.add(p);        
       
        hullRec(a, target, l1, hull);

        hullRec(target, b, l2, hull); 

    }
}
