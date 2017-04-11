package org.educationalProject.surfacePathfinder;

import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import java.util.List;
import java.util.Vector;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public abstract class MapVisualizer extends Visualizer {
	protected List<Triangle2D> triangles;
	protected Vector<Vector2D> path;
	
	protected abstract void drawTriangles();
	
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
	
	public void run() {
		init();
		loop();
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	protected float normalizeCoords(double data, double min, double max){
		return (float) (2*(data - min)/(max - min)-1);
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
	
	protected void drawPath(){
		glColor3f(1f, 1f, 1f);
		glLineWidth(4f);
        glBegin(GL_LINES);
          int size = path.size();
          for(int i = 0; i < size-1; i++){
        	  glVertex2f(normalizeCoords(path.get(i).x,minX,maxX), normalizeCoords(path.get(i).y,minY,maxY));
          	  glVertex2f(normalizeCoords(path.get(i+1).x,minX,maxX), normalizeCoords(path.get(i+1).y,minY,maxY));
          }
        glEnd();
	}
	
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			if(!done){
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
				
				findExtremes();				
				
				drawTriangles();
				
				drawPath();
				
				glfwSwapBuffers(window); // swap the color buffers
				done = true;
			}
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			
		}
	}
}
