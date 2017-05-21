package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPointSearcher implements Runnable {
    protected AtomicInteger stopPointSource;
    protected AtomicInteger stopPointDestination;
    protected AtomicBoolean stop;
    protected CopyOnWriteArrayList<Point> settledNodesSource;
    protected CopyOnWriteArrayList<Point> settledNodesDestination;

    public StopPointSearcher(AtomicInteger stopPointSource, AtomicInteger stopPointDestination,
                             AtomicBoolean stop,
                             CopyOnWriteArrayList<Point> settledNodesSource,
                             CopyOnWriteArrayList<Point> settledNodesDestination) {
        this.stopPointSource = stopPointSource;
        this.stopPointDestination = stopPointDestination;
        this.stop = stop;
        this.settledNodesSource = settledNodesSource;
        this.settledNodesDestination = settledNodesDestination;
    }


    protected void findStopPoint(int oldSizeNodesSource, int oldSizeNodesDestination) {
        int currentSizeNodesSource = 0;
        int currentSizeNodesDestination = 0;

        if (settledNodesSource.size() > oldSizeNodesSource) {
            currentSizeNodesSource = settledNodesSource.size();
            for (int i = oldSizeNodesSource; i < currentSizeNodesSource; i++) {
                Point tmp = settledNodesSource.get(i);
                if (settledNodesDestination.contains(tmp)) {
                    stop.set(true);
                    stopPointSource.set(i);
                    stopPointDestination.set(settledNodesDestination.indexOf(tmp));
                    return;
                }
            }
            oldSizeNodesSource = currentSizeNodesSource;
        }

        if (settledNodesDestination.size() > oldSizeNodesDestination) {
            currentSizeNodesDestination = settledNodesDestination.size();
            for (int i = oldSizeNodesDestination; i < currentSizeNodesDestination; i++) {
                Point tmp = settledNodesDestination.get(i);
                if (settledNodesSource.contains(tmp)) {
                    stop.set(true);
                    stopPointDestination.set(i);
                    stopPointSource.set(settledNodesSource.indexOf(tmp));
                    return;
                }
            }
            oldSizeNodesDestination = currentSizeNodesDestination;
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