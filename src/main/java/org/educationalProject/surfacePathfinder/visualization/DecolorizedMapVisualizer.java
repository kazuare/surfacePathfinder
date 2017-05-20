package org.educationalProject.surfacePathfinder.visualization;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class DecolorizedMapVisualizer extends MapVisualizer{

	public void setData(WeightedGraph<Point, DefaultWeightedEdge> graph, List<Point> pathCoords){
		this.graph = graph;
		this.path = pathCoords;		
		dataSet = true;
	}
	
	public void setData(WeightedGraph<Point, DefaultWeightedEdge> graph){
		this.graph = graph;
		dataSet = true;
	}
	
	protected void drawContent( GL2 gl2 ){
		DrawingUtils.drawGraph(gl2, sceneParams, graph, 1);
	}
}
