package org.educationalProject.surfacePathfinder.visualization;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.educationalProject.surfacePathfinder.Point;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
/**
* Swing window class. Can be used in a static way.
*/
public class PointSelectionWindow {
	public static Point a;
	public static Point b;
	public static List<Point> points;
	private static double maxX = 0;
	private static double maxY = 0;
	
    public static void start(Visualizer visualizer, List<Point> pointsList, int width, int height, String title){
    	points = pointsList;
    	
    	for(Point p : points){
    		maxX = Math.max(p.x, maxX);
    		maxY = Math.max(p.y, maxY);
    	}
    	
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( new VisualizerEventListener(visualizer)  );

        final JFrame jframe = new JFrame( title ); 
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });
        glcanvas.addMouseListener(new MouseListener() {// provides empty implementation of all
          	@Override
			public void mouseClicked(MouseEvent e) {
          		if(a==null || b==null){
          			System.out.println("Point selected");
          	  		double x = ((double)e.getX())/glcanvas.getWidth()*maxX;
          	  		double y = (glcanvas.getHeight() - (double)e.getY())/glcanvas.getHeight()*maxY;
          	  		System.out.println(x);
          	  		double minDist = Double.POSITIVE_INFINITY;
          	  		Point minPoint = null;
          	  		
	          	  	for(Point p : points){
	          	  		double dist = Math.sqrt( (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y) );
	      	  			if(minDist > dist){
	      	  				minDist = dist;
		      	  			minPoint = p;
	      	  			}
		      	  	}
          	  		
          	  		if(a == null)
          	  			a = minPoint;
          	  		else 
          	  			b = minPoint;
          	  		
          	  		System.out.println("x: " + minPoint.x + " y: " + minPoint.y + " alt: " + minPoint.alt );
          		}
          		
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( width, height );
        jframe.setVisible( true );
        
        
    }
}