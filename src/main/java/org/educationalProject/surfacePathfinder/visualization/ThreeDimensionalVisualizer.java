package org.educationalProject.surfacePathfinder.visualization;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.JFrame;

import org.educationalProject.surfacePathfinder.Point;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import io.github.jdiemke.triangulation.Triangle2D;

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

	      DrawingUtils.drawSurface(gl2, sceneParams, triangles);
	      
	      DrawingUtils.drawSurfaceEdges(gl2, sceneParams, triangles, 1);
	      
	      if(nodes!=null)
	    	  DrawingUtils.drawPath(gl2, sceneParams, nodes, 10);
	     
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
			  sceneParams.setCenterOffset(true);
			  sceneParams.setDimensions(3);
			  
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
	   
	}