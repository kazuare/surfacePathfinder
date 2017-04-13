package org.educationalProject.surfacePathfinder.Dijkstra;

import java.util.ArrayList;

import org.educationalProject.surfacePathfinder.Point;

public class Route {
	public double length = 0;
	public double hillness = -1;
	public ArrayList<Point> vertices;
	public Route(){
		vertices = new ArrayList<Point>();
	}
	private Route(boolean copyFlag){}
	public void append(Point vertice){
		vertices.add(vertice);
	}
	public void increaseLength(double distance){
		length += distance;
	}
	@SuppressWarnings("unchecked")
	public Route copy(){
		Route x = new Route(true);
		x.length = this.length;
		x.vertices = (ArrayList<Point>) this.vertices.clone();
		return x;
		
	}
	@Override
	public String toString(){
		String str = ">Length: " + length + "\n >>Path: ";
		for(int i = 0; i < vertices.size(); i++)
			str += vertices.get(i) + " "; 
		return str;
	}
}
