package org.educationalProject.surfacePathfinder.twoTierAStar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.educationalProject.surfacePathfinder.Point;
import io.github.jdiemke.triangulation.Vector2D;

public class ParallelTwoTierAStar extends TwoTierAStar{

	public ParallelTwoTierAStar(ArrayList<Vector2D> p, int pointsInRough, int roughPathStep, double COS_THRESHOLD,
			double ALTITUDE_MULTIPLIER, double TRIANGULATION_RADIUS) {
		super(p, pointsInRough, roughPathStep, COS_THRESHOLD, ALTITUDE_MULTIPLIER, TRIANGULATION_RADIUS);
	}
	
	public ArrayList<Point> getMorePrecisePath(List<Point>filteredNodes){
		 
		ArrayList<TwoTierThread> workers = new ArrayList<TwoTierThread>();
		
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(int i = 0; i < filteredNodes.size() - 1; i++){
            Runnable worker = new TwoTierThread(points, filteredNodes.get(i), filteredNodes.get(i+1),TRIANGULATION_RADIUS,ALTITUDE_MULTIPLIER);
            workers.add((TwoTierThread) worker);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}     

        ArrayList<Point> finalNodes = new ArrayList<Point>();
        
        for(int i = 0; i < workers.size(); i++){
        	 List<Point> partialNodes = workers.get(i).partialNodes;
             
             for(int j = 0; j < partialNodes.size(); j++)
            	 if(finalNodes.size()==0 || !finalNodes.get(finalNodes.size()-1).equals(partialNodes.get(j)))
            		 finalNodes.add(partialNodes.get(j));
        }
        
        return finalNodes;
	}
	
}
