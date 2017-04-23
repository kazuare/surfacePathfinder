package org.educationalProject.surfacePathfinder;

import org.educationalProject.surfacePathfinder.test.SerejaTest;
import org.educationalProject.surfacePathfinder.test.YegorTest;

/*
 * Main class that uses other classes to get job done. 
 * */
public class App {
	public static void main(String[] args) {
		//SerejaTest serejaTest = new SerejaTest();
		//serejaTest.test();
		YegorTest.setup();
		YegorTest.partialTriangulationExample();;
	}

}
