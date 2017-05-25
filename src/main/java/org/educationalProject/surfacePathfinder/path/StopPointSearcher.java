package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPointSearcher implements Runnable {
    private AtomicBoolean stop;
    private Point stopPointForward;
    private Point stopPointReverse;
    private CopyOnWriteArrayList<VisitedVertex> visitedVerticesForward;
    private CopyOnWriteArrayList<VisitedVertex> visitedVerticesReverse;
    private Double minLength;
    private double topF = 0.0;
    private double topR = 0.0;
    private double topFHeuristic = Double.MAX_VALUE;
    private double topRHeuristic = Double.MAX_VALUE;
    private boolean flag = false;

    public StopPointSearcher(AtomicBoolean stop, Point stopPointForward,
                                Point stopPointReverse,
                                CopyOnWriteArrayList<VisitedVertex> visitedVerticesForward,
                                CopyOnWriteArrayList<VisitedVertex> visitedVerticesReverse) {
        this.stop = stop;
        this.stopPointForward = stopPointForward;
        this.stopPointReverse = stopPointReverse;
        this.visitedVerticesForward = visitedVerticesForward;
        this.visitedVerticesReverse = visitedVerticesReverse;
        minLength = Double.MAX_VALUE;
    }

    private void setPoint(Point a, Point b) {
        a.x = b.x;
        a.y = b.y;
        a.alt = b.alt;
    }
    private int containsVertex(CopyOnWriteArrayList<VisitedVertex> visitedVertices, Point p){
        for (int i = 0; i < visitedVertices.size(); i++) {
            if (visitedVertices.get(i).vertex.equals(p)) {
                return i;
            }
        }
        return -1;
    }

    private void findStopPoint(int oldSizeVisitedForward,
                               int oldSizeVisitedReverse){


        int curSizeVisitedForward = visitedVerticesForward.size();
        for (int i = oldSizeVisitedForward; i < curSizeVisitedForward; i++){
            VisitedVertex v = visitedVerticesForward.get(i);

            for (Point neighbour : v.neighbours.keySet()) {
                int index = containsVertex(visitedVerticesReverse, neighbour);
                if (index != -1) {
                    double tmpLength = v.distance + v.neighbours.get(neighbour) + visitedVerticesReverse.get(index).distance;
                    if (tmpLength < minLength) {
                        // System.out.println("minLength " + minLength);
                        minLength = tmpLength;
                        flag = true;
                        setPoint(stopPointForward, v.vertex);
                        setPoint(stopPointReverse, neighbour);
                        topFHeuristic = v.heuristic;
                        topRHeuristic = visitedVerticesReverse.get(index).heuristic;
                    }
                }
            }
            if ((v.distance > topF) && (v.heuristic > topFHeuristic) && flag) {
                topF = v.distance;
                //topFHeuristic = v.heuristic;
            }

        }


        int curSizeVisitedReverse = visitedVerticesReverse.size();
        for (int i = oldSizeVisitedReverse; i < curSizeVisitedReverse; i++){
            VisitedVertex v = visitedVerticesReverse.get(i);
            for (Point neighbour : v.neighbours.keySet()) {
                int index = containsVertex(visitedVerticesForward, neighbour);
                if (index != -1) {
                    double tmpLength = v.distance + v.neighbours.get(neighbour) + visitedVerticesForward.get(index).distance;
                    if (tmpLength < minLength) {
                        // System.out.println("minLength " + minLength);
                        minLength = tmpLength;
                        flag = true;
                        setPoint(stopPointReverse, v.vertex);
                        setPoint(stopPointForward, neighbour);
                        topRHeuristic = v.heuristic;
                        topFHeuristic = visitedVerticesForward.get(index).heuristic;
                    }
                }
            }
            if((v.distance > topR) && (v.heuristic > topRHeuristic) && flag) {
                topR = v.distance;
                //topRHeuristic = v.heuristic;
            }
        }

        oldSizeVisitedForward = curSizeVisitedForward;
        oldSizeVisitedReverse = curSizeVisitedReverse;

        if (topF + topR > minLength) {
            // System.out.println("top " + (topF + topR));
            System.out.println("minLength " + minLength);
            /*System.out.println("forw " + containsVertex(visitedVerticesForward, stopPointForward) +
                   "/" + stopIndexForward.get() + "/" + visitedVerticesForward.size());
            System.out.println("rever " + containsVertex(visitedVerticesReverse, stopPointReverse)
                    + "/" + stopIndexReverse.get() + "/" + visitedVerticesReverse.size());
            System.out.println("forw nsp" + stopPointForward.toString());
            System.out.println("rever nsp" + stopPointReverse.toString());
            System.out.println("forw nsp stindex " + visitedVerticesForward.get(stopIndexForward.get()).vertex.toString());
            System.out.println("rever nsp stindex " + visitedVerticesReverse.get(stopIndexReverse.get()).vertex.toString());*/
            stop.set(true);
        }
    }
    @Override
    public void run() {
        int oldSizeVisitedForward = 0;
        int oldSizeVisitedReverse = 0;
        while(!stop.get()) {
            findStopPoint(oldSizeVisitedForward, oldSizeVisitedReverse);
        }
    }
}