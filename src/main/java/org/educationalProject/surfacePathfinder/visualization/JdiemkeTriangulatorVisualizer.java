package org.educationalProject.surfacePathfinder.visualization;

import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.Point;
import org.educationalProject.surfacePathfinder.onlineTriangulation.JdiemkeTriangulator;
import org.educationalProject.surfacePathfinder.onlineTriangulation.JdiemkeTriangulator.EdgeWithDistance;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Vector2D;
/**
* visualizator for internal use in JdiemkeTriangulator
*/
public class JdiemkeTriangulatorVisualizer extends GraphVisualizer {
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
	
	@Override
	public void setData(WeightedGraph<Point, DefaultWeightedEdge> graph){
		dataSet = false;
	}
	
	@Override
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

	@Override
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
}
