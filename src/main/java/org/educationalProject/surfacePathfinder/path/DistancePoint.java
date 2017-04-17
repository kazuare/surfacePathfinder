package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;


public class DistancePoint {
    public Point point;
    public double distance;

    DistancePoint(Point a, double d){
        this.point = a;
        this.distance = d;
    }
}
