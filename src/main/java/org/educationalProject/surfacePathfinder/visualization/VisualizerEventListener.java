package org.educationalProject.surfacePathfinder.visualization;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
/**
* Some utility class that does reshaping and displaying handling
*/
public class VisualizerEventListener implements GLEventListener{
	private Visualizer visualizer;
	public VisualizerEventListener(Visualizer visualizer){
		this.visualizer = visualizer;
	}
    @Override
    public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ){
    	visualizer.setup( glautodrawable.getGL().getGL2(), width, height );
    }
    
    @Override
    public void init( GLAutoDrawable glautodrawable ){}
    
    @Override
    public void dispose( GLAutoDrawable glautodrawable ){}
    
    @Override
    public void display( GLAutoDrawable glautodrawable ){
    	visualizer.checkAndDisplay( glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
    }
}
