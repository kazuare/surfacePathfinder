package org.educationalProject.surfacePathfinder;

import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class DecolorizedMapVisualizer extends MapVisualizer{

	protected void drawTriangles( GL2 gl2 ){
		gl2.glLineWidth(2f);
		gl2.glBegin(gl2.GL_LINES);
        
		for(Triangle2D triangle : triangles){
			
	        drawColoredPoint(gl2, (Point)triangle.a);
	        drawColoredPoint(gl2, (Point)triangle.b);
	        drawPoint(gl2, (Point)triangle.b);
	        drawColoredPoint(gl2, (Point)triangle.c);
	        drawPoint(gl2, (Point)triangle.c);
	        drawColoredPoint(gl2, (Point)triangle.a);
	      	        
		}
		gl2.glEnd();
	}
}
