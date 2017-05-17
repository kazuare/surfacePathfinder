package org.educationalProject.surfacePathfinder.visualization;

import javax.swing.Timer;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import org.educationalProject.surfacePathfinder.timing.NanoClock;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 * Swing window class. Can be used in a static way.
 */
public class SwingWithUpdate {
    final Timer ti = new Timer(0, null);
    private String title;
    private boolean started = false;
    private Visualizer visualizer;
    private int width;
    private int height;
    private JFrame jFrame;

    public SwingWithUpdate(Visualizer visualizer, int width, int height, String title)
    {
        this.visualizer = visualizer;
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void display() {

        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( new VisualizerEventListener(visualizer)  );

        if(started) {
            jFrame.getContentPane().add( glcanvas, BorderLayout.CENTER );
            jFrame.repaint();
            jFrame.validate();
            double start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 42);
            return;
        }
        jFrame = new JFrame(title);
        jFrame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jFrame.dispose();
                System.exit( 0 );
            }
        });

        jFrame.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jFrame.setSize( width, height );
        jFrame.setVisible( true );
        started = true;
    }
}