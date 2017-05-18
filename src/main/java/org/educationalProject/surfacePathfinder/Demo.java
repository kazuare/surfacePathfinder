package org.educationalProject.surfacePathfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.educationalProject.surfacePathfinder.onlineTriangulation.GraphProxy;
import org.educationalProject.surfacePathfinder.test.SerejaTest;
import org.educationalProject.surfacePathfinder.test.YegorTest;
import org.educationalProject.surfacePathfinder.timing.NanoClock;
import org.educationalProject.surfacePathfinder.timing.TicTocException;
import org.educationalProject.surfacePathfinder.twoTierAStar.TwoTierAStar;
import org.educationalProject.surfacePathfinder.visualization.DecolorizedMapVisualizer;
import org.educationalProject.surfacePathfinder.visualization.MainDemoWindow;
import org.educationalProject.surfacePathfinder.visualization.SwingWindow;
import org.educationalProject.surfacePathfinder.visualization.ThreeDimensionalVisualizer;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

/*
 * Main class that uses other classes to get job done. 
 * */
public class Demo {
	//addition punishment in our distances
    static final double ALTITUDE_MULTIPLIER = 0;
    //is used to determine whether the edge is "bad" and should not be included
    // 0 == no exclusion at all
    static final double COS_THRESHOLD = 0;
    static final double TRIANGULATION_RADIUS = 0.75; //this value seems to work good.
    
    public static void setup(){
    	EdgeWeighter.setParams(ALTITUDE_MULTIPLIER);
    }
    
	/**
	 * @param addr
	 */
	public static void start(String addr) {
		
		setup();
		
		try{
            double resultingTime;
            // We will used this clock to measure time through all the phases
            NanoClock clock = new NanoClock();
            
            // Obj file reading
            clock.tic();
            ArrayList<Vector2D> points = ObjFileParser.getPoints(addr);
            resultingTime = clock.tocd();
            System.out.println("Reading is finished, phase duration is: " + resultingTime);

            //DemonstrationVisualizer demoVis = new DemonstrationVisualizer();
            //demoVis.setData((List<Point>)(List<? extends Vector2D>)points);
            //PointSelectionWindow.start(demoVis, (List<Point>)(List<? extends Vector2D>)points, 700, 700, "DEMO");
           
    		MainDemoWindow demo = new MainDemoWindow();
    		demo.start((List<Point>)(List<? extends Vector2D>)points, 680, 680, "MAIN DEMO");
            
            while(demo.a == null || demo.b == null){
            	try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            Point a = null;
            Point b = null;
            
            for(Point p : (List<Point>)(List<? extends Vector2D>)points){
            	if(p.equals(demo.a))a=p;
            	if(p.equals(demo.b))b=p;
            }
            
            clock.tic();
            
            // Points triangulating
            List<Triangle2D> triangles = Triangulator.triangulate(points);       
            // Triangles -> graph convertion. Some edges are deleted if they have high altitude delta
            SimpleWeightedGraph<Point,DefaultWeightedEdge> graph = TrianglesToGraphConverter.convert(triangles, COS_THRESHOLD, ALTITUDE_MULTIPLIER);
       
            resultingTime = clock.tocd();
            System.out.println("Graph building is finished, phase duration is: " + resultingTime);
           
            clock.tic();
            //Finding the shortest path
            AStarShortestPath<Point,DefaultWeightedEdge> astar =
                    new AStarShortestPath<Point,DefaultWeightedEdge>(
                            graph,
                            new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
                    );  
                    
            List<Point> nodes = astar.getPath(a, b).getVertexList();
           
            ThreeDimensionalVisualizer vis3d = new ThreeDimensionalVisualizer();
            vis3d.show3DMap(triangles, nodes);
            
            resultingTime = clock.tocd();
            System.out.println("Pathfinding is finished, phase duration is: " + resultingTime);
            System.out.println("Path length is: " + astar.getPath(a, b).getWeight());
            //Visualizing
            DecolorizedMapVisualizer vis1 = new DecolorizedMapVisualizer();
            vis1.setData(graph, nodes);
            SwingWindow.start(vis1, 700, 700, "full triangulation map");

            clock.tic();
            
            GraphProxy graph2 = new GraphProxy(
            	1.5*TRIANGULATION_RADIUS, 
            	(ArrayList<Point>)(ArrayList<? extends Vector2D>)points, 
            	"ModifiedJdiemkeWithVisualization"
            );
            AStarShortestPath<Point,DefaultWeightedEdge> astar2 =
            		new AStarShortestPath<Point,DefaultWeightedEdge>(
                            graph2,
                            new EuclidianEuristicWithAltitude<Point>(ALTITUDE_MULTIPLIER)
                    );
            List<Point> nodes2 = astar2.getPath(a, b).getVertexList();
            
            resultingTime = clock.tocd();
            System.out.println("partial triangulation A* is finished, phase duration is: " + resultingTime);
            System.out.println("Path length is: " + astar2.getPath(a, b).getWeight());
            DecolorizedMapVisualizer vis = new DecolorizedMapVisualizer();
            vis.setData(graph2, nodes2);
            SwingWindow.start(vis, 700, 700, "modified jdiemke map");
            
            clock.tic();
            TwoTierAStar twotier = new TwoTierAStar(points, (int)(points.size()*0.2), 30, COS_THRESHOLD, ALTITUDE_MULTIPLIER, TRIANGULATION_RADIUS);
            List<Point> nodes3 = twotier.findPath(a,b);
            resultingTime = clock.tocd();
            System.out.println("two tier A* is finished, phase duration is: " + resultingTime);
            //TwoTierAStar.analyzeResult(nodes3, graph, a, b);
            TwoTierAStar.printPathWeight(nodes3);
            
            System.out.println("end");
        }catch(IOException e){
            System.out.println("Problem with file reading accured!");
        }catch(TicTocException e){
            System.out.println("There is a problem with time measuring code. toc is executed before tic.");
        } catch (NotEnoughPointsException e) {
            System.out.println("Not enough points to triangulate");
        }
	}

}
