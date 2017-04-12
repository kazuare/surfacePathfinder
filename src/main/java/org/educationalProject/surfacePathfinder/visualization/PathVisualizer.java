package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Vector2D;
/**
* Visualizes path altitude change
*/
public class PathVisualizer extends Visualizer {
	protected List<Point> path;
	protected List<Vector2D> allPoints;
	public void setData(List<Point> pathCoords, List<Vector2D> allPoints){
		this.path = pathCoords;	
		this.allPoints = allPoints;
		dataSet = true;
	}
	
	protected double maxAlt = Double.NEGATIVE_INFINITY;
	protected double minAlt = Double.POSITIVE_INFINITY;
	protected double totalXYPathLength = 0;
	
	protected float normalizeAlt(double data){
		return (float) (height * (data - minAlt)/(maxAlt - minAlt));
	}
	
	protected double getXYDistance(Point a, Point b){
		double xDistance = a.x - b.x;
		double yDistance = a.y - b.y;
		return Math.sqrt(xDistance*xDistance + yDistance*yDistance);
	}

	protected void findExtremes(){
		for(Vector2D p : allPoints){			
			minAlt = Math.min(minAlt, ((Point)p).alt);			
			maxAlt = Math.max(maxAlt, ((Point)p).alt);
		}
		for(int i = 1; i < path.size(); i++)
			totalXYPathLength += getXYDistance(path.get(i-1),path.get(i));
	}
	
	protected void drawPath( GL2 gl2 ){
		
        gl2.glColor3f( 1, 1, 1 );

		gl2.glLineWidth(1.5f);
		gl2.glBegin( GL.GL_LINES );
		
		double currentXYPathLength = 0;
		int size = path.size();
        for(int i = 1; i < size; i++){
        	
        	gl2.glVertex2f(
        		(float) (currentXYPathLength / totalXYPathLength * width), 
            	normalizeAlt(path.get(i-1).alt)
            ); 
        	
        	currentXYPathLength += getXYDistance(path.get(i-1),path.get(i));   
        	
        	gl2.glVertex2f(
            	(float) (currentXYPathLength / totalXYPathLength * width), 
                normalizeAlt(path.get(i).alt)
            ); 
        }
        
        gl2.glEnd();
	}
	
	public void display( GL2 gl2 ){
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	    gl2.glLoadIdentity();
	    totalXYPathLength = 0;
	    		
		findExtremes();				
			
		drawPath(gl2);	
	}
	
}
