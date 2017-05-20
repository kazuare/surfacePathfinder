package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPointSearcher implements Runnable {
    private AtomicInteger stopPoint1;
    private AtomicInteger stopPoint2;
    private AtomicBoolean stop;
    private CopyOnWriteArrayList<Point> settledNodes1;
    private CopyOnWriteArrayList<Point> settledNodes2;

    public StopPointSearcher(AtomicInteger stopPoint1, AtomicInteger stopPoint2,
                             AtomicBoolean stop,
                             CopyOnWriteArrayList<Point> settledNodes1,
                             CopyOnWriteArrayList<Point> settledNodes2) {
        this.stopPoint1 = stopPoint1;
        this.stopPoint2 = stopPoint2;
        this.stop = stop;
        this.settledNodes1 = settledNodes1;
        this.settledNodes2 = settledNodes2;
    }
    private void findStopPoint(int oldSizeNodes1, int oldSizeNodes2) {
        int currentSizeNodes1 = 0;
        int currentSizeNodes2 = 0;
        if (settledNodes1.size() > oldSizeNodes1) {
            currentSizeNodes1 = settledNodes1.size();
            for (int i = oldSizeNodes1; i < currentSizeNodes1; i++) {
                Point tmp = settledNodes1.get(i);
                if (settledNodes2.contains(tmp)) {
                    stop.set(true);
                    stopPoint1.set(i);
                    stopPoint2.set(settledNodes2.indexOf(tmp));
                    return;
                }
            }
            oldSizeNodes1 = currentSizeNodes1;
        }
        if (settledNodes2.size() > oldSizeNodes2) {
            currentSizeNodes2 = settledNodes2.size();
            for (int i = oldSizeNodes2; i < currentSizeNodes2; i++) {
                Point tmp = settledNodes2.get(i);
                if (settledNodes1.contains(tmp)) {
                    stop.set(true);
                    stopPoint2.set(i);
                    stopPoint1.set(settledNodes1.indexOf(tmp));
                    return;
                }
            }
            oldSizeNodes2 = currentSizeNodes2;
        }
    }

    @Override
    public void run() {
        int oldSizeNodes1 = 0;
        int oldSizeNodes2 = 0;
        int currentSizeNodes1 = 0;
        int currentSizeNodes2 = 0;
        while(!stop.get()) {
            findStopPoint(oldSizeNodes1, oldSizeNodes2);
        }
    }
}
