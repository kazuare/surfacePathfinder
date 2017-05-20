package org.educationalProject.surfacePathfinder.visualization;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

public class MainDemoWindow {
	public Point a;
	public Point b;
	public List<Point> points;
	private SceneParams sceneParams;
	private WeightedGraph<Point, DefaultWeightedEdge> graphToDraw = null;
	private List<Point> path = null;
	
	Point currentClick = null;
	
	public void setGraph(WeightedGraph<Point, DefaultWeightedEdge> graph){
		graphToDraw = graph;
	}
	
	public void setPath(List<Point> path){
		this.path = path;
	}
	
    public void start(List<Point> pointsList, int width, int height, String title){
    	points = pointsList;
    	
    	sceneParams = new SceneParams();
    	sceneParams.findExtremes(points);
    	sceneParams.setWidthAndHeight(2, 2);
    	sceneParams.setCenterOffset(true);

        GLProfile profile = GLProfile.getDefault();
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLWindow window = GLWindow.create(capabilities);
        final FPSAnimator animator = new FPSAnimator(window, 60, true);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent arg0) {
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted())
                            animator.stop();    
                        System.exit(0);
                    }
                }.start();
            }
        });
        window.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {	
				currentClick = new Point(((double)e.getX())/sceneParams.getWidth()*sceneParams.getMaxX(), (sceneParams.getHeight() - (double)e.getY())/sceneParams.getHeight()*sceneParams.getMaxY(), 0);
				if(a==null || b==null){
          			System.out.println("Point selected");
          	  		double x = ((double)e.getX())/width*sceneParams.getMaxX();
          	  		double y = (height - (double)e.getY())/height*sceneParams.getMaxY();
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
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseMoved(MouseEvent e) {}
			@Override
			public void mouseDragged(MouseEvent e) {}
			@Override
			public void mouseWheelMoved(MouseEvent e) {}
        	
        });
        window.addGLEventListener(new GLEventListener(){
        	
        	protected void drawContent( GL2 gl2 ){
        		
        		if(graphToDraw != null)
        			DrawingUtils.drawGraph(gl2,sceneParams,graphToDraw,1);
        		        		
        		gl2.glPointSize(1.5f);
        		gl2.glBegin(GL.GL_POINTS);
                
        		for(Point p: points){
        			gl2.glColor3f(1, DrawingUtils.normalizeAlt(sceneParams, p.alt), 0);	
        			gl2.glVertex2f(
        				DrawingUtils.normalizeX(sceneParams, p.x),
        				DrawingUtils.normalizeY(sceneParams, p.y)
        			);     
        		}	
        	
        		gl2.glEnd();
        		
        		if(a!=null || b!=null){
            		gl2.glPointSize(4f);
            		gl2.glBegin(GL.GL_POINTS);
            		
            		gl2.glColor3f(1, 0.5f, 1);	
            		if(a!=null)
	        			gl2.glVertex2f(
	        				DrawingUtils.normalizeX(sceneParams, a.x),
	        				DrawingUtils.normalizeY(sceneParams, a.y)
	        			); 
            		if(b!=null)
	        			gl2.glVertex2f(
	        				DrawingUtils.normalizeX(sceneParams, b.x),
	        				DrawingUtils.normalizeY(sceneParams, b.y)
	        			); 
            		
            		gl2.glEnd();
            		
            		gl2.glPointSize(1.5f);
        		}
        		
        		if(path!=null)
        			DrawingUtils.drawPath(gl2, sceneParams, path, 3);
                	
        	}
        	
			@Override
			public void init(GLAutoDrawable drawable) {}

			@Override
			public void dispose(GLAutoDrawable drawable) {}

			@Override
			public void display(GLAutoDrawable drawable) {
				GL2 gl = drawable.getGL().getGL2();   // get the OpenGL graphics context

		        gl.glClear(GL.GL_COLOR_BUFFER_BIT);    // clear background
		        gl.glLoadIdentity();                   // reset the model-view matrix    

		        drawContent(gl);
			}

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
        	
        });
        window.setSize((int)width, (int)height);
        window.setTitle(title);
        window.setVisible(true);
        animator.start(); 
    }
}