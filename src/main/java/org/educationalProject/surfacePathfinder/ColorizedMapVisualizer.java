package org.educationalProject.surfacePathfinder;

import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class ColorizedMapVisualizer extends MapVisualizer{

	protected void drawTriangles( GL2 gl2, int width, int height ){
		gl2.glBegin(gl2.GL_TRIANGLES);	
		float color;
		for(Triangle2D triangle : triangles){
			
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
			color = normalizeColor(a.alt, minAlt, maxAlt);
			gl2.glColor3f(1, color, 0);						  
			gl2.glVertex2f(
				normalizeWidth(a.x,minX,maxX,width), 
				normalizeHeight(a.y,minY,maxY,height)
			); 

			color = normalizeColor(b.alt, minAlt, maxAlt);
			gl2.glColor3f(1, color, 0);						  
			gl2.glVertex2f(
				normalizeWidth(b.x,minX,maxX,width), 
				normalizeHeight(b.y,minY,maxY,height)
			);  

			color = normalizeColor(c.alt, minAlt, maxAlt);
			gl2.glColor3f(1, color, 0);						  
			gl2.glVertex2f(
				normalizeWidth(c.x,minX,maxX,width), 
				normalizeHeight(c.y,minY,maxY,height)
			); 
			
		}
		gl2.glEnd();

		gl2.glLineWidth(1);
		gl2.glBegin(gl2.GL_LINES);
		gl2.glColor3f(0.5f, 0, 0.5f);
		for(Triangle2D triangle : triangles){
			
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
			gl2.glVertex2f(
	        	normalizeWidth(a.x,minX,maxX,width), 
	        	normalizeHeight(a.y,minY,maxY,height)
	        ); 
			gl2.glVertex2f(
	        	normalizeWidth(b.x,minX,maxX,width), 
	        	normalizeHeight(b.y,minY,maxY,height)
	        );  
			gl2.glVertex2f(
	        	normalizeWidth(b.x,minX,maxX,width), 
	        	normalizeHeight(b.y,minY,maxY,height)
	        );
			gl2.glVertex2f(
	        	normalizeWidth(c.x,minX,maxX,width), 
	        	normalizeHeight(c.y,minY,maxY,height)
	        );
			gl2.glVertex2f(
	        	normalizeWidth(c.x,minX,maxX,width), 
	        	normalizeHeight(c.y,minY,maxY,height)
	        );
			gl2.glVertex2f(
	        	normalizeWidth(a.x,minX,maxX,width), 
	        	normalizeHeight(a.y,minY,maxY,height)
	        );
		}
		gl2.glEnd();
	}
}
