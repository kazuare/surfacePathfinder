package org.educationalProject.surfacePathfinder;

import io.github.jdiemke.triangulation.Vector2D;

public class Point extends Vector2D {
	double alt;
	int id;
	
	public Point(double x, double y, double alt, int id){
		super(x, y);
		this.alt = alt;
		this.id = id;
	}
	
	
}
