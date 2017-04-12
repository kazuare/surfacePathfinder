package org.educationalProject.surfacePathfinder;

import io.github.jdiemke.triangulation.Vector2D;

public class Point extends Vector2D {
	public double alt;
	
	public Point(double x, double y, double alt, int id){
		super(x, y);
		this.alt = alt;
	}

	public boolean equals(Point b){
		if (this == b)
			return true;
		return (x == b.x) && (y == b.y);
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point b = (Point)obj;
		return (x == b.x) && (y == b.y);
	}
	
}
