import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.io.FileNotFoundException;

/**
 * Created by Nikhil on 01/03/17.
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(capabilities);

        Tree tree = new Tree(args[0], Integer.parseInt(args[1]));
        glcanvas.addGLEventListener(tree);
        //glcanvas.addKeyListener(tree);


        glcanvas.setSize(800, 800);

        final JFrame frame = new JFrame ("Fractal-Tree");

        frame.getContentPane().add(glcanvas);

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);
        //final FPSAnimator animator = new FPSAnimator(glcanvas, 300,true);
        //animator.start();
    }
}