package org.educationalProject.surfacePathfinder.visualization;

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
	   protected double maxX = Double.NEGATIVE_INFINITY;
	   protected double minX = Double.POSITIVE_INFINITY;
	   protected double maxY = Double.NEGATIVE_INFINITY;
	   protected double minY = Double.POSITIVE_INFINITY;
	   protected double maxAlt = Double.NEGATIVE_INFINITY;
	   protected double minAlt = Double.POSITIVE_INFINITY;
	   private GLU glu = new GLU();
	   private float rotation =0f;
	   List<Triangle2D> triangles;
	   List<Point> nodes = null;
	   private float seenWidth = 2;
	   private float staticRotation = 0;
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
	      rotation +=0.2f;
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
		  this.triangles = triangles;
	      findExtremes();
	      final GLProfile profile = GLProfile.get( GLProfile.GL2 );
	      GLCapabilities capabilities = new GLCapabilities( profile );
	      final GLCanvas glcanvas = new GLCanvas( capabilities );
	      glcanvas.addGLEventListener( this );
	      glcanvas.setSize( 700, 700 );
	      final JFrame frame = new JFrame ( "3D" );
	      frame.getContentPane().add(glcanvas);
	      frame.setSize( frame.getContentPane().getPreferredSize() );
	      frame.setVisible( true );
	      final FPSAnimator animator = new FPSAnimator( glcanvas, 60,true );
	      animator.start();
	   }
	   
	   public void show3DMap(List<Triangle2D> triangles, List<Point> nodes) {
			  this.triangles = triangles;
			  this.nodes = nodes;
		      findExtremes();
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
		      glcanvas.setSize( 700, 700 );
		      final JFrame frame = new JFrame ( "3D" );
		      frame.getContentPane().add(glcanvas);
		      frame.setSize( frame.getContentPane().getPreferredSize() );
		      frame.setVisible( true );
		      final FPSAnimator animator = new FPSAnimator( glcanvas, 60,true );
		      animator.start();
		   }
	   
		
		/**
		* translates map width into screen width
		*/
		protected float normalizeX(double data){
			return (float) (seenWidth * (data - minX)/(maxX - minX));
		}
		/**
		* translates map height into screen height
		*/
		protected float normalizeY(double data){
			return (float) (seenWidth * (data - minY)/(maxY - minY));
		}
		/**
		* translates map height into screen height
		*/
		protected float normalizeAlt(double data){
			return (float) ((data - minAlt)/(maxAlt - minAlt));
		}
		protected void drawPoint(GL2 gl2, Point a){
			gl2.glVertex3f(
				normalizeX(a.x)-seenWidth/2 , 
				normalizeAlt(a.alt),
				normalizeY(a.y)-seenWidth/2 
				
			); 
		}
		protected void drawPoint(GL2 gl2, Vector2D p){
			Point a = (Point)p;
			gl2.glVertex3f(
				normalizeX(a.x)-seenWidth/2 , 
				normalizeAlt(a.alt),
				normalizeY(a.y)-seenWidth/2 
				
			); 
		}
		protected void drawColoredPoint(GL2 gl2, Point a){
			gl2.glColor3f(1, normalizeColor(a.alt), 0);	
			drawPoint(gl2, a);
		}
		
		protected float normalizeColor(double data){
			return (float) ((data - minAlt)/(maxAlt - minAlt));
		}

		protected void findExtremes(){	
			for(Triangle2D t : triangles){
				Point a = (Point)t.a;			
				minX = Math.min(minX, a.x);			
				minY = Math.min(minY, a.y);
				minAlt = Math.min(minAlt, a.alt);			
				maxX = Math.max(maxX, a.x);			
				maxY = Math.max(maxY, a.y);			
				maxAlt = Math.max(maxAlt, a.alt);
				
				a = (Point)t.b;			
				minX = Math.min(minX, a.x);			
				minY = Math.min(minY, a.y);
				minAlt = Math.min(minAlt, a.alt);			
				maxX = Math.max(maxX, a.x);			
				maxY = Math.max(maxY, a.y);			
				maxAlt = Math.max(maxAlt, a.alt);

				a = (Point)t.c;			
				minX = Math.min(minX, a.x);			
				minY = Math.min(minY, a.y);
				minAlt = Math.min(minAlt, a.alt);			
				maxX = Math.max(maxX, a.x);			
				maxY = Math.max(maxY, a.y);			
				maxAlt = Math.max(maxAlt, a.alt);
			}		
		}
		
	   
	}