package org.educationalProject.surfacePathfinder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import io.github.jdiemke.triangulation.Vector2D;
/**
* Parses obj file and returns found vertices
*/
public class ObjFileParser {
	public static Vector<Vector2D> getPoints(String address) throws IOException{
		FileInputStream stream =  new FileInputStream(address);
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(stream, "UTF-8")
		);		
		
		Vector<Vector2D> points = new Vector<Vector2D>();
		
		String a;
		int currentId = 0;
		while((a = reader.readLine()) != null)
			if(a.charAt(0) == 'v'){
				String[] components = a.split(" ");
				points.add( new Point(
					Double.valueOf(components[1]),
					Double.valueOf(components[2]),
					Double.valueOf(components[3]),
					currentId++
				));
			}
		reader.close();
		return points;
	}
	public static Vector<Point> getPoints2(String address) throws IOException{
		FileInputStream stream =  new FileInputStream(address);
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(stream, "UTF-8")
		);		
		
		Vector<Point> points = new Vector<Point>();
		
		String a;
		int currentId = 0;
		while((a = reader.readLine()) != null)
			if(a.charAt(0) == 'v'){
				String[] components = a.split(" ");
				points.add( new Point(
					Double.valueOf(components[1]),
					Double.valueOf(components[2]),
					Double.valueOf(components[3]),
					currentId++
				));
			}
		reader.close();
		return points;
	}
}
