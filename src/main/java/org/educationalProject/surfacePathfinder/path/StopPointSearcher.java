package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPointSearcher implements Runnable {
    protected AtomicInteger stopPointSource;
    protected AtomicInteger stopPointDestination;
    protected AtomicBoolean stop;
    protected ConcurrentHashMap<Point, Double> distancesSource;
    protected ConcurrentHashMap<Point, Double> distancesDestination;
    protected CopyOnWriteArrayList<Point> settledNodesSource;
    protected CopyOnWriteArrayList<Point> settledNodesDestination;
    protected CopyOnWriteArrayList<Point> intersection;

    public StopPointSearcher(AtomicInteger stopPointSource, AtomicInteger stopPointDestination,
                             AtomicBoolean stop,
                             CopyOnWriteArrayList<Point> settledNodesSource,
                             CopyOnWriteArrayList<Point> settledNodesDestination,
                             CopyOnWriteArrayList<Point> intersection,
                             ConcurrentHashMap<Point, Double> distancesSource,
                             ConcurrentHashMap<Point, Double> distancesDestination
                             ) {
        this.stopPointSource = stopPointSource;
        this.stopPointDestination = stopPointDestination;
        this.stop = stop;
        this.settledNodesSource = settledNodesSource;
        this.settledNodesDestination = settledNodesDestination;
        this.intersection = intersection;
        this.distancesSource = distancesSource;
        this.distancesDestination = distancesDestination;
    }


    protected void findStopPoint(int oldSizeNodesSource, int oldSizeNodesDestination) {
        int currentSizeNodesSource = 0;
        int currentSizeNodesDestination = 0;

        if (settledNodesSource.size() > oldSizeNodesSource) {
            currentSizeNodesSource = settledNodesSource.size();
            for (int i = 0; i < currentSizeNodesSource; i++) {
                Point tmp = settledNodesSource.get(i);
                if (settledNodesDestination.contains(tmp)) {
                    stop.set(true);
                    //stopPointSource.set(i);
                    //stopPointDestination.set(settledNodesDestination.indexOf(tmp));
                    intersection.add(tmp);
                }
            }
            oldSizeNodesSource = currentSizeNodesSource;
        }

        if (settledNodesDestination.size() > oldSizeNodesDestination) {
            currentSizeNodesDestination = settledNodesDestination.size();
            for (int i = 0; i < currentSizeNodesDestination; i++) {
                Point tmp = settledNodesDestination.get(i);
                if (settledNodesSource.contains(tmp)) {
                    stop.set(true);
                    //stopPointSource.set(i);
                    //stopPointDestination.set(settledNodesDestination.indexOf(tmp));
                    intersection.add(tmp);
                }
            }
            oldSizeNodesDestination = currentSizeNodesDestination;
        }
        if (stop.get()) {
            System.out.println("intersection size " + intersection.size());
            for (int i = 0; i < intersection.size(); i++)
                System.out.println("int " + intersection.get(i).toString());
            Double dist = Double.MAX_VALUE;
            Point res = new Point(0.0, 0.0, 0.0);
            for (Point p : intersection) {
                Double sum = distancesSource.get(p) + distancesDestination.get(p);
                if (sum < dist) {
                    dist = sum;
                    res = p;
                }
            }
            stopPointSource.set(settledNodesSource.indexOf(res));
            stopPointDestination.set(settledNodesDestination.indexOf(res));
        }
    }

    @Override
    public void run() {
        int oldSizeNodesSource = 0;
        int oldSizeNodesDestination = 0;
        while(!stop.get()) {
            findStopPoint(oldSizeNodesSource, oldSizeNodesDestination);
        }
    }
}