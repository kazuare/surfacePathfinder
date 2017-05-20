package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
/**
* Visualizes top down map image.
* Subclasses can draw either filled triangles or not filled
*/
public abstract class MapVisualizer extends Visualizer {
	protected List<Point> path = null;
	protected List<Triangle2D> triangles;
	protected WeightedGraph<Point, DefaultWeightedEdge> graph;
	protected SceneParams sceneParams;
	/**
	* Sets data that needs to be visualized
	*/
	public void setData(List<Triangle2D> triangles, List<Point> pathCoords, WeightedGraph<Point, DefaultWeightedEdge> graph){
		this.triangles = triangles;
		this.path = pathCoords;		
		this.graph = graph;
		dataSet = true;
	}
	
	protected abstract void drawContent( GL2 gl2 );
	
	/**
	* translates map width into screen width
	*/
	protected float normalizeWidth(double data){
		return (float) (width * (data - sceneParams.getMinX())/(sceneParams.getMaxX() - sceneParams.getMinX()));
	}
	/**
	* translates map height into screen height
	*/
	protected float normalizeHeight(double data){
		return (float) (height * (data - sceneParams.getMinY())/(sceneParams.getMaxY() - sceneParams.getMinY()));
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
		return (float) ((data - sceneParams.getMinAlt())/(sceneParams.getMaxAlt() - sceneParams.getMinAlt()));
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
	    
	    sceneParams = new SceneParams();
	    sceneParams.setWidthAndHeight(width, height);
		sceneParams.findExtremes(graph.vertexSet());			
		
		drawContent(gl2);
		
		if(path != null)	
			drawPath(gl2);
	}
}
