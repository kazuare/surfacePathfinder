package org.educationalProject.surfacePathfinder;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Vector;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class MapVisualizer {
	private List<Triangle2D> triangles;
	private Vector<Vector2D> path;
	private long window;
	boolean done = false;
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(600, 600, "Path visualization", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}
	
	public void run(List<Triangle2D> triangles, Vector<Vector2D> pathCoords) {
		this.triangles = triangles;
		this.path = pathCoords;
		init();
		loop();
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	private float normalizeCoords(double data, double min, double max){
		return (float) (2*(data - min)/(max - min)-1);
	}
	private float normalizeColor(double data, double min, double max){
		return (float) ((data - min)/(max - min));
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
				
				
				double maxX = Double.NEGATIVE_INFINITY;
				double minX = Double.POSITIVE_INFINITY;
				double maxY = Double.NEGATIVE_INFINITY;
				double minY = Double.POSITIVE_INFINITY;
				double maxAlt = Double.NEGATIVE_INFINITY;
				double minAlt = Double.POSITIVE_INFINITY;
				
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
				
				for(Triangle2D triangle : triangles){
					
					Point a = (Point)triangle.a;
					Point b = (Point)triangle.b;
					Point c = (Point)triangle.c;
					
					glBegin(GL_TRIANGLES);	
					  float color;
					  
					  color = normalizeColor(a.alt, minAlt, maxAlt);
					  glColor3f(1, color, 0);						  
					  glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY)); 

					  color = normalizeColor(b.alt, minAlt, maxAlt);
					  glColor3f(1, color, 0);						  
					  glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));  

					  color = normalizeColor(c.alt, minAlt, maxAlt);
					  glColor3f(1, color, 0);						  
					  glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY)); 
					glEnd();
					
					glColor3f(0.5f, 0, 0.5f);
					glLineWidth(1f);
			        glBegin(GL_LINES);
			          glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY)); 
			          glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));  
			          glVertex2f(normalizeCoords(b.x,minX,maxX), normalizeCoords(b.y,minY,maxY));
			          glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY));
			          glVertex2f(normalizeCoords(c.x,minX,maxX), normalizeCoords(c.y,minY,maxY));
			          glVertex2f(normalizeCoords(a.x,minX,maxX), normalizeCoords(a.y,minY,maxY));
			        glEnd();
				}
				glColor3f(1f, 1f, 1f);
				glLineWidth(4f);
		        glBegin(GL_LINES);
		          int size = path.size();
		          for(int i = 0; i < size-1; i++){
		        	  glVertex2f(normalizeCoords(path.get(i).x,minX,maxX), normalizeCoords(path.get(i).y,minY,maxY));
		          	  glVertex2f(normalizeCoords(path.get(i+1).x,minX,maxX), normalizeCoords(path.get(i+1).y,minY,maxY));
		          }
		        glEnd();
				
				glfwSwapBuffers(window); // swap the color buffers
				done = true;
			}
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			
		}
	}
}
