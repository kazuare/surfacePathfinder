package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import io.github.jdiemke.triangulation.Triangle2D;
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
	
	public void display( GL2 gl2 ){
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
	    gl2.glLoadIdentity();
	    
	    sceneParams = new SceneParams();
	    sceneParams.setWidthAndHeight(width, height);
		sceneParams.findExtremes(graph.vertexSet());			
		
		drawContent(gl2);
		
		if(path != null)	
			DrawingUtils.drawPath(gl2, sceneParams, path, 3);
	}
}
