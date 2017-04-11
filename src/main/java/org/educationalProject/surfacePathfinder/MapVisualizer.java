package org.educationalProject.surfacePathfinder;

import java.util.List;
import java.util.Vector;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public abstract class MapVisualizer extends Visualizer {
	protected List<Triangle2D> triangles;
	protected Vector<Vector2D> path;
	
	protected abstract void drawTriangles( GL2 gl2, int width, int height );
	
	protected double maxX = Double.NEGATIVE_INFINITY;
	protected double minX = Double.POSITIVE_INFINITY;
	protected double maxY = Double.NEGATIVE_INFINITY;
	protected double minY = Double.POSITIVE_INFINITY;
	protected double maxAlt = Double.NEGATIVE_INFINITY;
	protected double minAlt = Double.POSITIVE_INFINITY;
	
	public void setData(List<Triangle2D> triangles, Vector<Vector2D> pathCoords){
		this.triangles = triangles;
		this.path = pathCoords;		
		dataSet = true;
	}
	
	protected float normalizeWidth(double data, double min, double max, int width){
		return (float) (width * (data - min)/(max - min));
	}
	
	protected float normalizeHeight(double data, double min, double max, int height){
		return (float) (height * (data - min)/(max - min));
	}
	
	protected float normalizeColor(double data, double min, double max){
		return (float) ((data - min)/(max - min));
	}

	protected void findExtremes(){
		for(Triangle2D triangle : triangles){
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
			minX = Math.min(minX, a.x);
			minX = Math.min(minX, b.x);
			minX = Math.min(minX, c.x);
			
			minY = Math.min(minY, a.y);
			minY = Math.min(minY, b.y);
			minY = Math.min(minY, c.y);
			
			minAlt = Math.min(minAlt, a.alt);
			minAlt = Math.min(minAlt, b.alt);
			minAlt = Math.min(minAlt, c.alt);
			
			maxX = Math.max(maxX, a.x);
			maxX = Math.max(maxX, b.x);
			maxX = Math.max(maxX, c.x);
			
			maxY = Math.max(maxY, a.y);
			maxY = Math.max(maxY, b.y);
			maxY = Math.max(maxY, c.y);
			
			maxAlt = Math.max(maxAlt, a.alt);
			maxAlt = Math.max(maxAlt, b.alt);
			maxAlt = Math.max(maxAlt, c.alt);
		}
	}
	
	protected void drawPath( GL2 gl2, int width, int height ){
		
        gl2.glColor3f( 1, 1, 1 );

		gl2.glLineWidth(3);
		gl2.glBegin( GL.GL_LINES );
		
		int size = path.size();
        for(int i = 0; i < size-1; i++){
        	gl2.glVertex2f(
        		normalizeWidth(path.get(i).x, minX, maxX, width),
        		normalizeHeight(path.get(i).y, minY, maxY, height)
        	);
        	gl2.glVertex2f(
        		normalizeWidth(path.get(i+1).x, minX, maxX, width), 
        		normalizeHeight(path.get(i+1).y, minY, maxY, height)
        	);
        }
        
        gl2.glEnd();
	}
	
	public void display( GL2 gl2, int width, int height ){
		if(!done){
			gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	        gl2.glLoadIdentity();
	        
			findExtremes();				
			
			drawTriangles(gl2, width, height);
			
			drawPath(gl2, width, height);
			
		}
		
        
 
	}
}
