import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Created by Nikhil on 11/03/17.
 */
public class Tree implements GLEventListener {
    private GLU glu = new GLU();
    private GLUquadric qobj;

    private int growthLevel = 9;

    private float branchLength = 1.0f;
    private float branchRadius = 0.07f;

    private float leftBranchAngle = 35.0f;
    private float rightBranchAngle = -35.0f;

    private float leftBranchContraction = 0.7f;
    private float rightBranchContraction = 0.7f;
    private float radiusContraction = 0.7f;

    private float divergenceAngle = 140.0f;

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        //glu.gluLookAt(0.0f, 2.0f, 6.0f, 0.0f, 2.0f, 0.0f, 0.0f, 1.0f, 0.0f);      //FRONT
        glu.gluLookAt(0.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);        //TOP

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(-this.divergenceAngle, 0.0f, 0.0f, 1.0f);

        tree(gl, this.branchLength, this.branchRadius, this.growthLevel);
    }

    private void tree(GL2 gl, float length, float radius, int depth) {
        if (depth == 0)
            return;

        gl.glPushMatrix();
            glu.gluCylinder(qobj, radius, this.radiusContraction * radius, length, 40, 40);
            gl.glPushMatrix();
                gl.glTranslatef(0.0f, 0.0f, length);
                gl.glRotatef(this.divergenceAngle, 0.0f, 0.0f, 1.0f);
                gl.glPushMatrix();
                    gl.glRotatef(this.leftBranchAngle, 0.0f, -1.0f, 0.0f);
                    tree(gl, this.leftBranchContraction * length, this.radiusContraction * radius, depth - 1);
                gl.glPopMatrix();
                gl.glPushMatrix();
                    gl.glRotatef(this.rightBranchAngle, 0.0f, -1.0f, 0.0f);
                    tree(gl, this.rightBranchContraction * length, this.radiusContraction * radius, depth - 1);
                gl.glPopMatrix();
            gl.glPopMatrix();
        gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        glu.gluDeleteQuadric(qobj);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        if( height <= 0 )
            height = 1;

        final float h = ( float ) width / ( float ) height;
        gl.glViewport( 0, 0, width, height );
        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();

        glu.gluPerspective( 45.0f, h, 0.1, 100.0 );
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel( GL2.GL_SMOOTH );
        gl.glClearColor( 0f, 0f, 0f, 0f );
        gl.glClearDepth( 1.0f );
        gl.glEnable( GL2.GL_DEPTH_TEST );
        gl.glDepthFunc( GL2.GL_LEQUAL );
        gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );

        qobj = glu.gluNewQuadric();
        glu.gluQuadricNormals(qobj, GLU.GLU_SMOOTH);
    }
}
