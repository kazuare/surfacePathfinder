package org.educationalProject.surfacePathfinder;

import static org.lwjgl.opengl.GL11.*;
import io.github.jdiemke.triangulation.Triangle2D;

public class ColorizedMapVisualizer extends MapVisualizer {

	protected void drawTriangles(){
		glBegin(GL_TRIANGLES);	
		float color;
		for(Triangle2D triangle : triangles){
			
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
			color = normalizeColor(a.alt, minAlt, maxAlt);
			glColor3f(1, color, 0);						  
			glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY)); 

			color = normalizeColor(b.alt, minAlt, maxAlt);
			glColor3f(1, color, 0);						  
			glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));  

			color = normalizeColor(c.alt, minAlt, maxAlt);
			glColor3f(1, color, 0);						  
			glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY)); 
			
		}
		glEnd();

        glBegin(GL_LINES);
		glColor3f(0.5f, 0, 0.5f);
		glLineWidth(1f);
		for(Triangle2D triangle : triangles){
			
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
	        glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY)); 
	        glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));  
	        glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));
	        glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY));
	        glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY));
	        glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY));
		}
        glEnd();
	}
	
}
