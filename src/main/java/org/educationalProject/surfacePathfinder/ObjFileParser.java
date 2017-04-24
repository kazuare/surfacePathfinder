package org.educationalProject.surfacePathfinder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import io.github.jdiemke.triangulation.Vector2D;
/**
* Parses obj file and returns found vertices
*/
public class ObjFileParser {
	
	private static BufferedReader getReader(String address) throws IOException{
		FileInputStream stream =  new FileInputStream(address);
		return new BufferedReader(
			new InputStreamReader(stream, "UTF-8")
		);		
	}
	
	private static void fillListWithPoints(BufferedReader reader, ArrayList<Vector2D> list) throws NumberFormatException, IOException{
		String a;
		while((a = reader.readLine()) != null)
			if(a.charAt(0) == 'v'){
				String[] components = a.split(" ");
				list.add( new Point(
					Double.valueOf(components[1]),
					Double.valueOf(components[2]),
					Double.valueOf(components[3])
				));
			}
	}
	
	public static ArrayList<Vector2D> getPoints(String address) throws IOException{
		
		BufferedReader reader = getReader(address);
		
		ArrayList<Vector2D> points = new ArrayList<Vector2D>();
		
		fillListWithPoints(reader, points);
		
		reader.close();
		return points;
	}
	
	public static ArrayList<Point> getPoints2(String address) throws IOException{
		
		BufferedReader reader = getReader(address);
		
		ArrayList<Vector2D> points = new ArrayList<Vector2D>();
		
		fillListWithPoints(reader, points);
		
		reader.close();
		return (ArrayList<Point>)(ArrayList<? extends Vector2D>)points;
	}
	
}
