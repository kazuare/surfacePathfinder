package org.educationalProject.surfacePathfinder;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public abstract class Visualizer {
	protected boolean done = false;
	protected boolean dataSet = false;
	
    protected void setup( GL2 gl2, int width, int height ) {
        gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();

        GLU glu = new GLU();
        glu.gluOrtho2D( 0.0f, width, 0.0f, height );

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

        gl2.glViewport( 0, 0, width, height );
    }
    
    protected void checkAndDisplay( GL2 gl2, int width, int height ){
    	if(dataSet)
    		display(gl2, width, height);
    	else System.out.println("Warning: no data is set");
    }

    protected abstract void display( GL2 gl2, int width, int height );
}