package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class DrawingUtils {
	
	public static float normalizeX(SceneParams sceneParams, double data){
		float result =(float)(sceneParams.getWidth()*(data - sceneParams.getMinX())/(sceneParams.getMaxX() - sceneParams.getMinX())); 
		//add offset if needed
		if(sceneParams.getCenterOffsetCoef()==1)
			result -= sceneParams.getWidth()/2*sceneParams.getCenterOffsetCoef();
		return result;
	}
	public static float normalizeY(SceneParams sceneParams, double data){
		float result =(float)(sceneParams.getHeight()*(data - sceneParams.getMinY())/(sceneParams.getMaxY() - sceneParams.getMinY())); 
		//add offset if needed
		if(sceneParams.getCenterOffsetCoef()==1)
			result -= sceneParams.getHeight()/2*sceneParams.getCenterOffsetCoef();
		return result;
	}
	public static float normalizeAlt(SceneParams sceneParams, double data){
		return (float) ((data - sceneParams.getMinAlt())/(sceneParams.getMaxAlt() - sceneParams.getMinAlt()));
	}
	public static void drawPoint(GL2 gl2, SceneParams sceneParams, Vector2D a){
		drawPoint(gl2, sceneParams, (Point)a);
	}
	public static void drawPoint(GL2 gl2, SceneParams sceneParams, Point a){
		if(sceneParams.dimensions == 2){
			gl2.glVertex2f(
				normalizeX(sceneParams, a.x), 
				normalizeY(sceneParams, a.y)
			); 
		}else if(sceneParams.dimensions == 3){
			gl2.glVertex3f(
				DrawingUtils.normalizeX(sceneParams, a.x), 
				DrawingUtils.normalizeAlt(sceneParams, a.alt),
				DrawingUtils.normalizeY(sceneParams, a.y) 
			); 
		}
	}
	public static void drawColoredPoint(GL2 gl2, SceneParams sceneParams, Point a){
		if(sceneParams.dimensions == 2){
			gl2.glColor3f(1, normalizeAlt(sceneParams, a.alt), 0);	
			drawPoint(gl2, sceneParams, a);
		}else{
			Point color = ColorLevels.getColor1(DrawingUtils.normalizeAlt(sceneParams, a.alt));
    		gl2.glColor3f((float)color.x, (float)color.y, (float)color.alt);
			drawPoint(gl2, sceneParams, a);
		}
	}
	public static void drawGraph(GL2 gl2, SceneParams sceneParams, WeightedGraph<Point, DefaultWeightedEdge> graph, float lineWidth){
		gl2.glLineWidth(lineWidth);
		gl2.glBegin(GL.GL_LINES);
        
		for(DefaultWeightedEdge edge : graph.edgeSet()){			
			drawColoredPoint(gl2, sceneParams, graph.getEdgeSource(edge));
			drawColoredPoint(gl2, sceneParams, graph.getEdgeTarget(edge));	      	        
		}
		
		gl2.glEnd();
	}
	public static void drawPath(GL2 gl2, SceneParams sceneParams, List<Point> path, float lineWidth){
        gl2.glColor3f( 1, 1, 1 );
		gl2.glLineWidth(lineWidth);
		gl2.glBegin( GL.GL_LINES );
		
		int size = path.size();
        for(int i = 0; i < size-1; i++){
        	drawPoint(gl2, sceneParams, path.get(i));
        	drawPoint(gl2, sceneParams, path.get(i+1));
        }
        
        gl2.glEnd();
		
	}
	
	public static void drawSurface(GL2 gl2, SceneParams sceneParams, Iterable<Triangle2D> triangles){
		gl2.glBegin(GL.GL_TRIANGLES);	
		for(Triangle2D triangle : triangles){

			drawColoredPoint(gl2, sceneParams, (Point)triangle.a);
			drawColoredPoint(gl2, sceneParams, (Point)triangle.b);
			drawColoredPoint(gl2, sceneParams, (Point)triangle.c);
			
		}
		gl2.glEnd();		
	}
	
	public static void drawSurfaceEdges(GL2 gl2, SceneParams sceneParams, Iterable<Triangle2D> triangles, float lineWidth){
		gl2.glLineWidth(lineWidth);
	    gl2.glBegin( GL2.GL_LINES );   	      				
	    gl2.glColor3f(0, 0, 0);
	    for(Triangle2D t : triangles){
	    	drawPoint(gl2, sceneParams, (Point) t.a);
	    	drawPoint(gl2, sceneParams, (Point) t.b);
	    	drawPoint(gl2, sceneParams, (Point) t.b);
	    	drawPoint(gl2, sceneParams, (Point) t.c);
	    	drawPoint(gl2, sceneParams, (Point) t.c);
	    	drawPoint(gl2, sceneParams, (Point) t.a);
		}	      
	      
	    gl2.glEnd(); 		
	}
}
