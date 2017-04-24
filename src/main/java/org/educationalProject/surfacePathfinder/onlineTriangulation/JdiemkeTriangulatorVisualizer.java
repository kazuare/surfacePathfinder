package org.educationalProject.surfacePathfinder.onlineTriangulation;

import java.util.List;
import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.visualization.Visualizer;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
/**
* visualizator for internal use in JdiemkeTriangulator
*/
public class JdiemkeTriangulatorVisualizer extends Visualizer {
	protected ArrayList<JdiemkeTriangulator.EdgeWithDistance> graph;
	protected ArrayList<JdiemkeTriangulator.EdgeWithDistance> removedEdges;
	protected ArrayList<Point> centers;
	protected ArrayList<Point> points;
	double radius;
	/**
	* Sets data that needs to be visualized
	*/
	public void setData(ArrayList<Point> points, ArrayList<JdiemkeTriangulator.EdgeWithDistance> graph, ArrayList<JdiemkeTriangulator.EdgeWithDistance> removedEdges,  ArrayList<Point> centers, double radius){
		this.points = points;
		this.graph = graph;
		this.centers = centers;
		this.radius = radius;
		this.removedEdges = removedEdges;
		dataSet = true;
	}
	
	protected void drawContent( GL2 gl2 ){
		
		gl2.glLineWidth(2f);		
		gl2.glBegin(GL.GL_LINES);
		gl2.glColor3f(1f, 1f, 1f);	
		for(JdiemkeTriangulator.EdgeWithDistance edge : removedEdges){
			 drawPoint(gl2, edge.a);
		     drawPoint(gl2, edge.b);	    
		}
		
		
		gl2.glEnd();
		
		gl2.glLineWidth(1f);		
		gl2.glBegin(GL.GL_LINES);
        
		for(JdiemkeTriangulator.EdgeWithDistance edge : graph){
			 drawColoredPoint(gl2, edge.a);
		     drawColoredPoint(gl2, edge.b);	    
		}
		
		
		gl2.glEnd();
		
		gl2.glLineWidth(1.4f);	
		double DEG2RAD = Math.PI/90;
		float totalColor = 0;
		float colorAddition = 1f/centers.size();
		gl2.glBegin(GL.GL_LINES);
			for(Point center : centers){
				gl2.glColor3f(0, 0.3f, totalColor+=colorAddition);	
				for (int i = 0; i < 360; i++){
					double degInRad = i*DEG2RAD;
					gl2.glVertex2f(
							normalizeWidth(Math.cos(degInRad)*radius + center.x), 
							normalizeHeight(Math.sin(degInRad)*radius + center.y)
						); 
					gl2.glVertex2f(
							normalizeWidth(Math.cos(degInRad+DEG2RAD)*radius + center.x), 
							normalizeHeight(Math.sin(degInRad+DEG2RAD)*radius + center.y)
						); 
				}
			}
		gl2.glEnd();
		
		
		gl2.glLineWidth(1.4f);	
		gl2.glBegin(GL.GL_LINES);
			for(Point center : centers){
				gl2.glColor3f(0, 1f, 0);	
				for (int i = 0; i < 360; i++){
					double degInRad = i*DEG2RAD;
					gl2.glVertex2f(
							normalizeWidth(Math.cos(degInRad)*0.01 + center.x), 
							normalizeHeight(Math.sin(degInRad)*0.01 + center.y)
						); 
					gl2.glVertex2f(
							normalizeWidth(Math.cos(degInRad+DEG2RAD)*0.01 + center.x), 
							normalizeHeight(Math.sin(degInRad+DEG2RAD)*0.01 + center.y)
						); 
				}
			}
		gl2.glEnd();
	}
	
	protected double maxX = Double.NEGATIVE_INFINITY;
	protected double minX = Double.POSITIVE_INFINITY;
	protected double maxY = Double.NEGATIVE_INFINITY;
	protected double minY = Double.POSITIVE_INFINITY;
	protected double maxAlt = Double.NEGATIVE_INFINITY;
	protected double minAlt = Double.POSITIVE_INFINITY;
	
	/**
	* translates map width into screen width
	*/
	protected float normalizeWidth(double data){
		return (float) (width * (data - minX)/(maxX - minX));
	}
	/**
	* translates map height into screen height
	*/
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
		for(Point a : points){
			minX = Math.min(minX, a.x);			
			minY = Math.min(minY, a.y);
			minAlt = Math.min(minAlt, a.alt);			
			maxX = Math.max(maxX, a.x);			
			maxY = Math.max(maxY, a.y);			
			maxAlt = Math.max(maxAlt, a.alt);
			
		}
			
	}
	
	
	
	public void display( GL2 gl2 ){
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	    gl2.glLoadIdentity();
	        
		findExtremes();				
			
		drawContent(gl2);
			
	}
}
