package org.educationalProject.surfacePathfinder;

import static org.lwjgl.opengl.GL11.*;
import io.github.jdiemke.triangulation.Triangle2D;

public class DecolorizedMapVisualizer extends MapVisualizer {

	protected void drawTriangles(){
		glLineWidth(1f);
        glBegin(GL_LINES);
        
		for(Triangle2D triangle : triangles){
			
			Point a = (Point)triangle.a;
			Point b = (Point)triangle.b;
			Point c = (Point)triangle.c;
			
	        float color = normalizeColor(a.alt, minAlt, maxAlt);
			glColor3f(1, color, 0);	
	        glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY));
	          
	        color = normalizeColor(b.alt, minAlt, maxAlt);
			glColor3f(1, color, 0);	
	        glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));  
	        glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));
	          
	        color = normalizeColor(c.alt, minAlt, maxAlt);
			glColor3f(1, color, 0);	
	        glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY));
	        glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY));
	          
	        color = normalizeColor(a.alt, minAlt, maxAlt);
			glColor3f(1, color, 0);	
	        glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY));
	        
		}
		glEnd();
	}
	
}
