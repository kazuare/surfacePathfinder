package org.educationalProject.surfacePathfinder;

import java.util.List;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public abstract class MapVisualizer extends Visualizer {
	protected List<Point> path;
	protected List<Triangle2D> triangles;
	protected SimpleWeightedGraph<Point,DefaultWeightedEdge> graph;

	public void setData(List<Triangle2D> triangles, List<Point> pathCoords, SimpleWeightedGraph<Point,DefaultWeightedEdge> graph){
		this.triangles = triangles;
		this.path = pathCoords;		
		this.graph = graph;
		dataSet = true;
	}
	
	protected abstract void drawContent( GL2 gl2 );
	
	protected double maxX = Double.NEGATIVE_INFINITY;
	protected double minX = Double.POSITIVE_INFINITY;
	protected double maxY = Double.NEGATIVE_INFINITY;
	protected double minY = Double.POSITIVE_INFINITY;
	protected double maxAlt = Double.NEGATIVE_INFINITY;
	protected double minAlt = Double.POSITIVE_INFINITY;
	
	protected float normalizeWidth(double data){
		return (float) (width * (data - minX)/(maxX - minX));
	}
	
	protected float normalizeHeight(double data){
		return (float) (height * (data - minY)/(maxY - minY));
	}
	
	protected void drawPoint(GL2 gl2, Vector2D a){
		gl2.glVertex2f(
			normalizeWidth(a.x), 
			normalizeHeight(a.y)
		); 
	}
	protected void drawColoredPoint(GL2 gl2, Point a){
		gl2.glColor3f(1, normalizeColor(a.alt), 0);	
		drawPoint(gl2, a);
	}
	
	protected float normalizeColor(double data){
		return (float) ((data - minAlt)/(maxAlt - minAlt));
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
	
	protected void drawPath( GL2 gl2 ){
		
        gl2.glColor3f( 1, 1, 1 );

		gl2.glLineWidth(3);
		gl2.glBegin( GL.GL_LINES );
		
		int size = path.size();
        for(int i = 0; i < size-1; i++){
        	drawPoint(gl2, path.get(i));
        	drawPoint(gl2, path.get(i+1));
        }
        
        gl2.glEnd();
	}
	
	public void display( GL2 gl2 ){
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	    gl2.glLoadIdentity();
	        
		findExtremes();				
			
		drawContent(gl2);
			
		drawPath(gl2);
	}
}
