package org.educationalProject.surfacePathfinder;

import org.educationalProject.surfacePathfinder.test.SerejaTest;
import org.educationalProject.surfacePathfinder.test.YegorTest;

/*
 * Main class that uses other classes to get job done. 
 * */
public class App {

	//addition punishment in our distances 
	static final double ALTITUDE_MULTIPLIER = 16;
	//is used to determene whether the edge is "bad" and should not be included
	static final double COS_THRESHOLD = 0.7;
	
	public static void main(String[] args) {
		//SerejaTest serejaTest = new SerejaTest();
		//serejaTest.test();
		YegorTest yegorTest = new YegorTest();
		yegorTest.test();
	}

}
