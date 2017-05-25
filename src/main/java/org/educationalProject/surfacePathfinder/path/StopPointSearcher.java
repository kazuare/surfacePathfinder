package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPointSearcher implements Runnable {
    protected AtomicBoolean stop;
    protected Point stopPointForward;
    protected Point stopPointReverse;
    protected CopyOnWriteArrayList<VisitedVertex> visitedVerticesForward;
    protected CopyOnWriteArrayList<VisitedVertex> visitedVerticesReverse;
    protected Double minLength;
    protected double topF = 0.0;
    protected double topR = 0.0;
    protected double topFHeuristic = Double.MAX_VALUE;
    protected double topRHeuristic = Double.MAX_VALUE;
    protected boolean flag = false;

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


    protected int containsVertex(CopyOnWriteArrayList<VisitedVertex> visitedVertices, Point p){
        for (int i = 0; i < visitedVertices.size(); i++) {
            if (visitedVertices.get(i).vertex.equals(p)) {
                return i;
            }
        }
        return -1;
    }
    protected boolean checkStop() {
        if (stop.get()) {
            if (!stopPointForward.equals(new Point(0.0, 0.0, 0.0)))
                stopPointReverse.setPoint(visitedVerticesReverse.get(0).vertex);
            if (!stopPointReverse.equals(new Point(0.0, 0.0, 0.0)))
                stopPointForward.setPoint(visitedVerticesForward.get(0).vertex);
            return true;
        }
        return false;
    }

    protected void findStopPoint(int oldSizeVisitedForward,
                               int oldSizeVisitedReverse){

        if (checkStop())
            return;


        int curSizeVisitedForward = visitedVerticesForward.size();
        for (int i = oldSizeVisitedForward; i < curSizeVisitedForward; i++){
            VisitedVertex v = visitedVerticesForward.get(i);

            for (Point neighbour : v.neighbours.keySet()) {
                int index = containsVertex(visitedVerticesReverse, neighbour);
                if (index != -1) {
                    double tmpLength = v.distance + v.neighbours.get(neighbour) + visitedVerticesReverse.get(index).distance;
                    if (tmpLength < minLength) {
                        minLength = tmpLength;
                        flag = true;
                        stopPointForward.setPoint(v.vertex);
                        stopPointReverse.setPoint(neighbour);
                        topFHeuristic = v.heuristic;
                        topRHeuristic = visitedVerticesReverse.get(index).heuristic;
                    }
                }
            }
            if ((v.distance > topF) && (v.heuristic < topFHeuristic) && flag) {
                topF = v.distance;
                //topFHeuristic = v.heuristic;
            }
        }

        if (checkStop())
            return;

        int curSizeVisitedReverse = visitedVerticesReverse.size();
        for (int i = oldSizeVisitedReverse; i < curSizeVisitedReverse; i++){
            VisitedVertex v = visitedVerticesReverse.get(i);
            for (Point neighbour : v.neighbours.keySet()) {
                int index = containsVertex(visitedVerticesForward, neighbour);
                if (index != -1) {
                    double tmpLength = v.distance + v.neighbours.get(neighbour) + visitedVerticesForward.get(index).distance;
                    if (tmpLength < minLength) {
                        minLength = tmpLength;
                        flag = true;
                        stopPointReverse.setPoint(v.vertex);
                        stopPointForward.setPoint(neighbour);
                        topRHeuristic = v.heuristic;
                        topFHeuristic = visitedVerticesForward.get(index).heuristic;
                    }
                }
            }
            if((v.distance > topR) && (v.heuristic < topRHeuristic) && flag) {
                topR = v.distance;
                //topRHeuristic = v.heuristic;
            }
        }

        if (checkStop())
            return;

        oldSizeVisitedForward = curSizeVisitedForward;
        oldSizeVisitedReverse = curSizeVisitedReverse;
        /*if (flag)
            stop.set(true);*/
        if (topF + topR > minLength)
            stop.set(true);

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