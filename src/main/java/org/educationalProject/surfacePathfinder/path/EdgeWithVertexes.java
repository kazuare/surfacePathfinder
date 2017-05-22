package org.educationalProject.surfacePathfinder.path;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashSet;
import java.util.Hashtable;


public class EdgeWithVertexes {
    public Point source;
    public Point target;
    public DefaultWeightedEdge edge;

    public EdgeWithVertexes(Point source, Point target, DefaultWeightedEdge edge) {
        this.source = source;
        this.target = target;
        this.edge = edge;
    }

    public boolean equals(EdgeWithVertexes b){
        if(b == null)
            return false;
        if (this == b)
            return true;
        return source.equals(b.source) && target.equals(b.target);
    }
    public boolean equals(DefaultWeightedEdge e){
        if (this.edge == e)
            return true;
        return false;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EdgeWithVertexes b = (EdgeWithVertexes) obj;
        return source.equals(b.source) && target.equals(b.target);
    }
}
