package org.educationalProject.surfacePathfinder;
import io.github.jdiemke.triangulation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
public class Triangulator {
	public static List<Triangle2D> triangulate(ArrayList<Vector2D> points) throws NotEnoughPointsException{
	    
		DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(points);
	    delaunayTriangulator.triangulate();
	    
	    List<Triangle2D> triangleSoup = delaunayTriangulator.getTriangles();
	    
	    return triangleSoup;
	}
}
