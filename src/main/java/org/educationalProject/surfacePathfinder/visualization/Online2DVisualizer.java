package org.educationalProject.surfacePathfinder.visualization;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Online2DVisualizer extends MapVisualizer {

    private ArrayList<Point> list;
    public void setData(WeightedGraph<Point, DefaultWeightedEdge> graph, ArrayList<Point> list, List<Point> pathCoords){
        this.graph = graph;
        this.list = list;
        this.path = pathCoords;
        dataSet = true;
    }

    protected void drawColoredPoint(GL2 gl2, Point a){
        Point color = ColorLevels.getColor(normalizeColor(a.alt));
        gl2.glColor3f((float)color.x, (float)color.y, (float)color.alt);
        drawPoint(gl2, a);
    }
    protected void drawContent( GL2 gl2 ){
        gl2.glLineWidth(2f);
        gl2.glBegin(GL.GL_LINES);


        for (int i = 0; i < list.size(); i += 2){
            drawColoredPoint(gl2, list.get(i));
            drawColoredPoint(gl2, list.get(i + 1));
        }

        gl2.glEnd();
    }
    protected void drawPath( GL2 gl2 ){
        if(path == null)
            return;


        gl2.glColor3f( 0, 0, 0 );

        gl2.glLineWidth(9);
        gl2.glBegin( GL.GL_LINES );

        int size = path.size();
        for(int i = 0; i < size - 1; i++){
            drawPoint(gl2, path.get(i));
            drawPoint(gl2, path.get(i+1));
        }

        gl2.glEnd();

        gl2.glColor3f( 1, 1, 1 );

        gl2.glLineWidth(3);
        gl2.glBegin( GL.GL_LINES );

        for(int i = 0; i < size - 1; i++){
            drawPoint(gl2, path.get(i));
            drawPoint(gl2, path.get(i+1));
        }

        gl2.glEnd();

        gl2.glColor3f(1, 0, 0);
        gl2.glLineWidth(15);
        gl2.glBegin( GL.GL_LINES );
        drawPoint(gl2, path.get(0));
        drawPoint(gl2, path.get(1));
        drawPoint(gl2, path.get(size - 2));
        drawPoint(gl2, path.get(size - 1));
        gl2.glEnd();
    }
}
