package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;

import io.github.jdiemke.triangulation.Triangle2D;

public class SceneParams {
	protected double maxX = Double.NEGATIVE_INFINITY;
	protected double minX = Double.POSITIVE_INFINITY;
	protected double maxY = Double.NEGATIVE_INFINITY;
	protected double minY = Double.POSITIVE_INFINITY;
	protected double maxAlt = Double.NEGATIVE_INFINITY;
	protected double minAlt = Double.POSITIVE_INFINITY;
	
	protected float height = 0;
	protected float width = 0;
	
	protected boolean centerOffset = false;
	
	public void findExtremes(List<Triangle2D> triangles){
		
		for(Triangle2D t : triangles){
			Point a = (Point)t.a;			
			minX = Math.min(minX, a.x);			
			minY = Math.min(minY, a.y);
			minAlt = Math.min(minAlt, a.alt);			
			maxX = Math.max(maxX, a.x);			
			maxY = Math.max(maxY, a.y);			
			maxAlt = Math.max(maxAlt, a.alt);
			
			a = (Point)t.b;			
			minX = Math.min(minX, a.x);			
			minY = Math.min(minY, a.y);
			minAlt = Math.min(minAlt, a.alt);			
			maxX = Math.max(maxX, a.x);			
			maxY = Math.max(maxY, a.y);			
			maxAlt = Math.max(maxAlt, a.alt);

			a = (Point)t.c;			
			minX = Math.min(minX, a.x);			
			minY = Math.min(minY, a.y);
			minAlt = Math.min(minAlt, a.alt);			
			maxX = Math.max(maxX, a.x);			
			maxY = Math.max(maxY, a.y);			
			maxAlt = Math.max(maxAlt, a.alt);
		}			
		
	}	

	public void findExtremes(Iterable<Point> points){
		for(Point a : points){			
			minX = Math.min(minX, a.x);			
			minY = Math.min(minY, a.y);
			minAlt = Math.min(minAlt, a.alt);			
			maxX = Math.max(maxX, a.x);			
			maxY = Math.max(maxY, a.y);			
			maxAlt = Math.max(maxAlt, a.alt);
		}	
		
	}
	
	public void setWidthAndHeight(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public void setCenterOffset(boolean val){
		centerOffset = val;
	}
	
	public int getCenterOffsetCoef(){
		if(centerOffset)
			return 1;
		return 0;
	}
	
	public double getMinX(){
		return minX;
	}
	public double getMaxX(){
		return maxX;
	}
	public double getMinY(){
		return minY;
	}
	public double getMaxY(){
		return maxY;
	}
	public double getMinAlt(){
		return minAlt;
	}
	public double getMaxAlt(){
		return maxAlt;
	}
	public float getHeight(){
		return height;
	}
	public float getWidth(){
		return width;
	}

}
