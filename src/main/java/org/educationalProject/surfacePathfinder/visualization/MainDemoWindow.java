package org.educationalProject.surfacePathfinder.visualization;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;

import java.util.List;

import org.educationalProject.surfacePathfinder.Point;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

import io.github.jdiemke.triangulation.Vector2D;

public class MainDemoWindow {
	public Point a;
	public Point b;
	public List<Point> points;
	private double maxX = 0;
	private double maxY = 0;
	private double minX = 0;
	private double minY = 0;
	private double maxAlt = 0;
	private double minAlt = 0;
	private double globalHeight = 0;
	private double globalWidth = 0;
	private double fixOffset = 5;
	
	
	Point currentClick = null;
	
    public void start(List<Point> pointsList, int width, int height, String title){
    	points = pointsList;
    	globalHeight = height;
    	globalWidth = width;
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
				System.out.println("=> x: " + e.getX() + " y: " + e.getY());
				currentClick = new Point(((double)e.getX())/globalWidth*maxX, (globalHeight - (double)e.getY()-fixOffset)/globalHeight*maxY, 0);
				if(a==null || b==null){
          			System.out.println("Point selected");
          	  		double x = ((double)e.getX())/globalWidth*maxX;
          	  		double y = (globalHeight - (double)e.getY()-fixOffset)/globalHeight*maxY;
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
        	
        	/**
        	* translates map width into screen width
        	*/
        	protected float normalizeWidth(double data){
        		return (float) (2*(data - minX)/(maxX - minX)-1);
        	}
        	/**
        	* translates map height into screen height
        	*/
        	protected float normalizeHeight(double data){
        		return (float) (2*(data - minY)/(maxY - minY)-1);
        	}
        	
        	protected void drawPoint(GL2 gl2, Vector2D a){
        		gl2.glVertex2f(
        			normalizeWidth(a.x), 
        			normalizeHeight(a.y)
        		); 
        	}
        	
        	protected float normalizeColor(double data){
        		return (float) ((data - minAlt)/(maxAlt - minAlt));
        	}
        	
        	protected void findExtremes(){		
        		for(Point a : points){			
        			minX = Math.min(minX, a.x);			
        			minY = Math.min(minY, a.y);
        			minAlt = Math.min(minAlt, a.alt);			
        			maxX = Math.max(maxX, a.x);			
        			maxY = Math.max(maxY, a.y);			
        			maxAlt = Math.max(maxAlt, a.alt);
        		}		
        	}
        	
        	protected void drawContent( GL2 gl2 ){
        		gl2.glPointSize(1.5f);
        		gl2.glBegin(GL.GL_POINTS);
                
        		for(Point p: points){
        			gl2.glColor3f(1, normalizeColor(p.alt), 0);	
        			gl2.glVertex2f(
        					normalizeWidth(p.x),
        					normalizeWidth(p.y)
        				);     
        		}	
        	
        		gl2.glEnd();
        		
        		if(a!=null || b!=null){
            		gl2.glPointSize(4f);
            		gl2.glBegin(GL.GL_POINTS);
            		
            		gl2.glColor3f(1, 0.5f, 1);	
            		if(a!=null)
	        			gl2.glVertex2f(
	        					normalizeWidth(a.x),
	        					normalizeWidth(a.y)
	        				); 
            		if(b!=null)
	        			gl2.glVertex2f(
	        					normalizeWidth(b.x),
	        					normalizeWidth(b.y)
	        				); 
            		
            		gl2.glEnd();
            		
            		gl2.glPointSize(1.5f);
        		}
                	
        		/*
        		if(currentClick!=null){
        			gl2.glPointSize(1f);
            		gl2.glBegin(GL.GL_POINTS);
            		
            		gl2.glColor3f(1, 0.5f, 1);	
            		gl2.glVertex2f(
        					normalizeWidth(currentClick.x),
        					normalizeWidth(currentClick.y)
        				); 
            		gl2.glEnd();
        		}
        		*/
        	}
        	
			@Override
			public void init(GLAutoDrawable drawable) {
				findExtremes();
			}

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
        window.setSize((int)globalWidth, (int)globalHeight);
        window.setTitle(title);
        window.setVisible(true);
        animator.start(); 
    }
}