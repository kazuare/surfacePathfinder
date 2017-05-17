package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Vector2D;

public class DemonstrationVisualizer extends MapVisualizer{
	public List<Point> points;
	
	public void setData(List<Point> points){
		this.points = points;
		dataSet = true;
	}

	@Override
	protected void findExtremes(){		
		for(Point a : points){			
			minX = Math.min(minX, a.x);			
			minY = Math.min(minY, a.y);
			minAlt = Math.min(minAlt, a.alt);			
			maxX = Math.max(maxX, a.x);			
			maxY = Math.max(maxY, a.y);			
			maxAlt = Math.max(maxAlt, a.alt);
		}		
	}
	
	protected void drawContent( GL2 gl2 ){
		gl2.glPointSize(1.5f);
		gl2.glBegin(GL.GL_POINTS);
        
		for(Point a: points){
			gl2.glColor3f(1, normalizeColor(a.alt), 0);	
			gl2.glVertex2f(
					normalizeWidth(a.x), 
					normalizeHeight(a.y)
				);      	        
		}	
	
		gl2.glEnd();
	}
	
	public void display( GL2 gl2 ){
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	    gl2.glLoadIdentity();
	        
		findExtremes();				
			
		drawContent(gl2);
			
	}
	
	public void showPoints(){
		
	}
}
