package org.educationalProject.surfacePathfinder;

import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class DecolorizedMapVisualizer extends MapVisualizer{

	protected void drawTriangles( GL2 gl2 ){
		gl2.glLineWidth(2f);
		gl2.glBegin(gl2.GL_LINES);
        
		for(Triangle2D triangle : triangles){
			
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
	        float color = normalizeColor(a.alt);
	        gl2.glColor3f(1, color, 0);	
	        gl2.glVertex2f(
	        	normalizeWidth(a.x),
	        	normalizeHeight(a.y)
	        );
	          
	        color = normalizeColor(b.alt);
	        gl2.glColor3f(1, color, 0);	
	        gl2.glVertex2f(
	        	normalizeWidth(b.x), 
	        	normalizeHeight(b.y)
	        );  
	        gl2.glVertex2f(
	        	normalizeWidth(b.x), 
	        	normalizeHeight(b.y)
	        );
	          
	        color = normalizeColor(c.alt);
	        gl2.glColor3f(1, color, 0);	
	        gl2.glVertex2f(
	        	normalizeWidth(c.x), 
	        	normalizeHeight(c.y)
	        );
	        gl2.glVertex2f(
	        	normalizeWidth(c.x), 
	        	normalizeHeight(c.y)
	        );
	          
	        color = normalizeColor(a.alt);
	        gl2.glColor3f(1, color, 0);	
	        gl2.glVertex2f(
	        	normalizeWidth(a.x), 
	        	normalizeHeight(a.y)
	        );
	        
		}
		gl2.glEnd();
	}
}
