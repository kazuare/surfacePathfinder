package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;


public class DistancePoint {
    public Point point;
    public double distance;

    DistancePoint(Point a, double d){
        this.point = a;
        this.distance = d;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DistancePoint b = (DistancePoint) obj;
        return (this.point.equals(b.point));
    }
}
