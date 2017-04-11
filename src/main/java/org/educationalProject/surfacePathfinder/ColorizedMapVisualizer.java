package org.educationalProject.surfacePathfinder;

import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;

public class ColorizedMapVisualizer extends MapVisualizer{

	protected void drawTriangles( GL2 gl2 ){
		gl2.glBegin(gl2.GL_TRIANGLES);	
		for(Triangle2D triangle : triangles){

			drawColoredPoint(gl2, (Point)triangle.a);
			drawColoredPoint(gl2, (Point)triangle.b);
			drawColoredPoint(gl2, (Point)triangle.c);
			
		}
		gl2.glEnd();

		gl2.glLineWidth(1);
		gl2.glBegin(gl2.GL_LINES);
		gl2.glColor3f(0.5f, 0, 0.5f);
		for(Triangle2D triangle : triangles){
			
			drawPoint(gl2, triangle.a);
			drawPoint(gl2, triangle.b);
			drawPoint(gl2, triangle.b);
			drawPoint(gl2, triangle.c);
			drawPoint(gl2, triangle.c);
			drawPoint(gl2, triangle.a);
			
		}
		gl2.glEnd();
	}
}
