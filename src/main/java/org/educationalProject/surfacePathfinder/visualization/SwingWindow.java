package org.educationalProject.surfacePathfinder.visualization;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
* Swing window class. Can be used in a static way.
*/
public class SwingWindow {

    public static void start(Visualizer visualizer, int width, int height, String title){
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
        
        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( width, height );
        jframe.setVisible( true );
    }
}