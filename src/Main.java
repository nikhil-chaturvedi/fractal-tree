import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;

/**
 * Created by Nikhil on 01/03/17.
 */
public class Main {
    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(capabilities);

        Tree tree = new Tree();
        glcanvas.addGLEventListener(tree);

        glcanvas.setSize(800, 800);

        final JFrame frame = new JFrame ("Fractal-Tree");

        frame.getContentPane().add(glcanvas);

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);
    }
}