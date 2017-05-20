package org.educationalProject.surfacePathfinder.visualization;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.JFrame;

import org.educationalProject.surfacePathfinder.Point;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

public class ThreeDimensionalVisualizer implements GLEventListener{	   
	   protected SceneParams sceneParams;
	   private GLU glu = new GLU();
	   private float rotation =0f;
	   List<Triangle2D> triangles;
	   List<Point> nodes = null;
	   private float staticRotation = 0;
	   private float rotationDeltaDesired = 0.25f;
	   private float rotationDelta = rotationDeltaDesired;
	   @Override
	   public void display( GLAutoDrawable drawable ) {
	      final GL2 gl2 = drawable.getGL().getGL2();
	      gl2.glShadeModel( GL2.GL_SMOOTH );
	      gl2.glClearColor( 0f, 0f, 0f, 0f );
	      gl2.glClearDepth( 1.0f );
	      gl2.glEnable( GL2.GL_DEPTH_TEST );
	      gl2.glDepthFunc( GL2.GL_LEQUAL );
	      gl2.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );
	      gl2.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );     
	      gl2.glLoadIdentity();  
	      gl2.glTranslatef( 0f, -0.5f,-3.0f ); 
	      gl2.glRotatef( rotation, 0.0f, 1.0f, 0.0f );
	      gl2.glRotatef( staticRotation, 0.0f, 0.0f, 1.0f );
	      gl2.glBegin( GL2.GL_TRIANGLES );   
	      				
		  
	      for(Triangle2D t : triangles){
	    	  drawColoredPoint(gl2, (Point) t.a);
	    	  drawColoredPoint(gl2, (Point) t.b);
	    	  drawColoredPoint(gl2, (Point) t.c);
		  }	      
	      
	      gl2.glEnd(); 
	      
	      gl2.glLineWidth(1);
	      gl2.glBegin( GL2.GL_LINES );   	      				
	      gl2.glColor3f(0, 0, 0);
	      for(Triangle2D t : triangles){
	    	  drawPoint(gl2, (Point) t.a);
	    	  drawPoint(gl2, (Point) t.b);
	    	  drawPoint(gl2, (Point) t.b);
	    	  drawPoint(gl2, (Point) t.c);
	    	  drawPoint(gl2, (Point) t.c);
	    	  drawPoint(gl2, (Point) t.a);
		  }	      
	      
	      gl2.glEnd(); 
	      
	      if(nodes!=null){
	    	  gl2.glLineWidth(10);
		      gl2.glBegin( GL2.GL_LINES ); 
		      
		      gl2.glColor3f(1, 1, 1);	
		      for(int i = 0; i < nodes.size()-1; i++){
		    	  drawPoint(gl2, nodes.get(i));
		    	  drawPoint(gl2, nodes.get(i+1));
		      }
		      gl2.glEnd(); 
	      }
	      
	      gl2.glFlush();
	      rotation += rotationDelta;
	   }
	   @Override
	   public void dispose( GLAutoDrawable drawable ) {}
	   @Override
	   public void init( GLAutoDrawable drawable  ) {
	      final GL2 gl2 = drawable.getGL().getGL2();
	      gl2.glShadeModel( GL2.GL_SMOOTH );
	      gl2.glClearColor( 0f, 0f, 0f, 0f );
	      gl2.glClearDepth( 1.0f );
	      gl2.glEnable( GL2.GL_DEPTH_TEST );
	      gl2.glDepthFunc( GL2.GL_LEQUAL );
	      gl2.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );
	   }
	   @Override
	   public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {
	      final GL2 gl2 = drawable.getGL().getGL2();
	      final float h = ( float ) width / ( float ) height;
	      gl2.glViewport( 0, 0, width, height );
	      gl2.glMatrixMode( GL2.GL_PROJECTION );
	      gl2.glLoadIdentity();
	      glu.gluPerspective( 45.0f, h, 1.0, 20.0 );
	      gl2.glMatrixMode( GL2.GL_MODELVIEW );
	      gl2.glLoadIdentity();
	   }
	   public void show3DMap(List<Triangle2D> triangles) {
		   	  show3DMap(triangles,null);
	   }
	   
	   public void show3DMap(List<Triangle2D> triangles, List<Point> nodes) {
			  this.triangles = triangles;
			  this.nodes = nodes;
			  
			  sceneParams = new SceneParams();
			  sceneParams.findExtremes(triangles);
			  sceneParams.setWidthAndHeight(2, 2);
			  
		      final GLProfile profile = GLProfile.get( GLProfile.GL2 );
		      GLCapabilities capabilities = new GLCapabilities( profile );
		      final GLCanvas glcanvas = new GLCanvas( capabilities );
		      glcanvas.addGLEventListener( this );
		      glcanvas.addMouseWheelListener(new MouseWheelListener(){
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					staticRotation += e.getPreciseWheelRotation();				
				}		    	  
		      });
		      glcanvas.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if(rotationDelta == rotationDeltaDesired)
						rotationDelta = 0;
					else
						rotationDelta = rotationDeltaDesired;
				}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseReleased(MouseEvent e) {}		    	  
		      });
		      glcanvas.setSize( 700, 700 );
		      final JFrame frame = new JFrame ( "3D" );
		      frame.getContentPane().add(glcanvas);
		      frame.setSize( frame.getContentPane().getPreferredSize() );
		      frame.setVisible( true );
		      final FPSAnimator animator = new FPSAnimator( glcanvas, 60,true );
		      animator.start();
		   }
	   
		
		/**
		* translates map width into 3d model width
		*/
		protected float normalizeX(double data){
			return (float) (sceneParams.getWidth() * (data - sceneParams.getMinX())/(sceneParams.getMaxX() - sceneParams.getMinX()));
		}
		/**
		* translates map height into 3d model height
		*/
		protected float normalizeY(double data){
			return (float) (sceneParams.getHeight() * (data - sceneParams.getMinY())/(sceneParams.getMaxY() - sceneParams.getMinY()));
		}

		protected float normalizeAlt(double data){
			return (float) ((data - sceneParams.getMinAlt())/(sceneParams.getMaxAlt() - sceneParams.getMinAlt()));
		}
		protected void drawPoint(GL2 gl2, Point a){
			gl2.glVertex3f(
				normalizeX(a.x)-sceneParams.getWidth()/2 , 
				normalizeAlt(a.alt),
				normalizeY(a.y)-sceneParams.getHeight()/2 
				
			); 
		}
		protected void drawColoredPoint(GL2 gl2, Point a){
    		Point color = ColorLevels.getColor1(normalizeColor(a.alt));
    		gl2.glColor3f((float)color.x, (float)color.y, (float)color.alt);
			drawPoint(gl2, a);
		}
		
		protected float normalizeColor(double data){
			return normalizeAlt(data);
		}

	   
	}