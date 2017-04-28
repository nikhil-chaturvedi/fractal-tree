import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import sun.awt.image.ImageWatched;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Created by Nikhil on 11/03/17.
 */

public class Tree implements GLEventListener {
    private GLU glu = new GLU();
    private GLUquadric qobj;

    private int growthLevel = 2;

    private float branchLength = 0.5f;
    private float branchRadius = 0.07f;

    private float leftBranchAngle = 35.0f;
    private float rightBranchAngle = -35.0f;

    private float leftBranchContraction = 0.9f;
    private float rightBranchContraction = 0.7f;
    private float middleBranchContraction = 0.9f;
    private float radiusContraction = 0.7f;
    private ArrayList<Controller_nonunif> nonuniflist;
    private Controller_unif unif;

    private ArrayList<Branch> total;
    private ArrayList<Branch> current;
    private ArrayList<Branch> next;
    private int treeType = 1;
    private float angle;


    private float divergenceAngle = 90;

    public Tree() {
        this.total = new ArrayList<Branch>();
        this.current = new ArrayList<Branch>();
        this.next = new ArrayList<Branch>();
        this.nonuniflist= new ArrayList<Controller_nonunif>();
        this.unif = new Controller_unif();
        this.treeType = 1;
    }
    public Tree(int treeType) {
        this.total = new ArrayList<Branch>();
        this.current = new ArrayList<Branch>();
        this.next = new ArrayList<Branch>();
        this.nonuniflist= new ArrayList<Controller_nonunif>();
        this.treeType = treeType;
        this.angle = 0;
        if(this.treeType >1) {
            this.unif = new Controller_unif(0,0,-0.015f);
            //this.nonuniflist.add(new Controller_nonunif(-30, 0, -20, -0.05));
        }

    }

    public void setTreeType(int treeType) {
        treeType = treeType;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        this.angle += 45;
        glu.gluLookAt(0, 4.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);      //FRONT

        //glu.gluLookAt(0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);        //TOP
        //glu.gluLookAt(-4.0f, 0.0f, 4.0f, 0.0f, 0.0f, 4.0f, 0.0f, 0.0f, 1.0f);        //SIDE

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        // gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        //gl.glRotatef(-this.divergenceAngle, 0.0f, 0.0f, 1.0f);

        tree(gl, this.branchLength, this.branchRadius, this.growthLevel);

    }

    private void drawBranch(GL2 gl, Branch branch) {
        float z_init_x = 0;
        float z_init_y = 0;
        float z_init_z = 1;
        float z_init_mod = 1;

        float z_final_x = branch.getTipX() - branch.getBaseX();
        float z_final_y = branch.getTipY() - branch.getBaseY();
        float z_final_z = branch.getTipZ() - branch.getBaseZ();
        float z_final_mod = (float) Math.sqrt(z_final_x*z_final_x+ z_final_y*z_final_y + z_final_z*z_final_z);

        float x = (z_init_y * z_final_z) - (z_init_z * z_final_y);
        float y = (z_init_z * z_final_x) - (z_init_x * z_final_z);
        float z = (z_init_x * z_final_y) - (z_init_y * z_final_x);

        float angle = (float) Math.acos(((z_init_x*z_final_x + z_init_y*z_final_y + z_init_z*z_final_z)/(z_init_mod*z_final_mod)));

        //gl.glRotatef(-90 , 0 , 0, 1);
        gl.glTranslatef(branch.getBaseX(), branch.getBaseY(), branch.getBaseZ());
        gl.glRotatef((float) (angle * 180.0/Math.PI) ,x,y,z);
        gl.glRotatef(divergenceAngle ,0,(float) -Math.sin(angle),(float) Math.cos(angle));


        glu.gluSphere(qobj, branch.getStartradius()*1.005,40,40);
        glu.gluCylinder(qobj, branch.getStartradius(), branch.getEndradius(), branch.getLength(), 40, 40);
        gl.glTranslatef(-branch.getBaseX(), -branch.getBaseY(), - branch.getBaseZ());

    }

    private void tree(GL2 gl, float length, float radius, int depth) {

        for(int i=0; i<depth; i++) {
            //generate list of branches, to be rendered later
            if(i == 0) {
                total.add(new Branch(0,0,0,0,0,branchLength,radius,radius*radiusContraction));
            }
            if(i== 1) {
                
                //b1 and b2 coordinates don
                double w = length;
                Branch b = total.get(total.size() -1);
                float xLInit = (float) (b.getTipX() + leftBranchContraction * ( 0 - w * Math.sin(leftBranchAngle * Math.PI / 180.0)));
                float yLInit = b.getTipY();
                float zLInit = (float) (b.getTipZ() + leftBranchContraction * ( 0 + w * Math.cos(leftBranchAngle * Math.PI/ 180.0)));
                float sumLX = xLInit;
                float sumLY = yLInit;
                float sumLZ = zLInit;

                float xRInit = (float) (b.getTipX() + rightBranchContraction * ( 0 - w * Math.sin(rightBranchAngle * Math.PI / 180.0)));
                float yRInit = b.getTipY();
                float zRInit = (float) (b.getTipZ() + rightBranchContraction * ( 0 + w * Math.cos(rightBranchAngle * Math.PI/ 180.0)));
                float sumRX = xRInit;
                float sumRY = yRInit;
                float sumRZ = zRInit;

                float xMInit = Branch.getMidTipX(b, middleBranchContraction);
                float yMInit = Branch.getMidTipY(b, middleBranchContraction);
                float zMInit = Branch.getMidTipZ(b, middleBranchContraction);
                float sumMX = xMInit;
                float sumMY = yMInit;
                float sumMZ = zMInit;


                for(int k = 0; k<nonuniflist.size(); k++) {
                    sumLX += nonuniflist.get(k).dx(xLInit, yLInit, zLInit);
                    sumRX += nonuniflist.get(k).dx(xRInit, yRInit, zRInit);
                    sumMX += nonuniflist.get(k).dx(xRInit, yRInit, zRInit);

                    sumLY += nonuniflist.get(k).dy(xLInit, yLInit, zLInit);
                    sumRY += nonuniflist.get(k).dy(xRInit, yRInit, zRInit);
                    sumMY += nonuniflist.get(k).dy(xRInit, yRInit, zRInit);

                    sumLZ += nonuniflist.get(k).dz(xLInit, yLInit, zLInit);
                    sumRZ += nonuniflist.get(k).dz(xRInit, yRInit, zRInit);
                    sumMZ += nonuniflist.get(k).dz(xRInit, yRInit, zRInit);
                }

                if(treeType >1 ) {
                    sumLX += unif.getDx();
                    sumLY += unif.getDy();
                    sumLZ += unif.getDz();

                    sumRX += unif.getDx();
                    sumRY += unif.getDy();
                    sumRZ += unif.getDz();

                    sumMX += unif.getDx();
                    sumMY += unif.getDy();
                    sumMZ += unif.getDz();
                }
                Branch b1 = new Branch(b.getTipX(),b.getTipY(),b.getTipZ(), sumLX, sumLY, sumLZ, b.getEndradius(), b.getEndradius() * radiusContraction);

                Branch b2 = new Branch(b.getTipX(),b.getTipY(),b.getTipZ(), sumRX, sumRY, sumRZ, b.getEndradius(), b.getEndradius() * radiusContraction);

                if(treeType ==3) {
                    Branch b3 =new Branch(b.getTipX(), b.getTipY(), b.getTipZ(), sumMX, sumMY, sumMZ, b.getEndradius(), b.getEndradius() * radiusContraction);
                    total.add(b3);
                    current.add(b3);
                }
                total.add(b1);
                total.add(b2);
                current.add(b1);
                current.add(b2);
            }
            else {

                for(int p = 0; p< current.size(); p++) {

                    Branch temp = current.get(p);
                    float xLInit = 0;
                    float yLInit = 0;
                    float zLInit = 0;
                    float xRInit = 0;
                    float yRInit = 0;
                    float zRInit = 0;
                    float w = temp.getLength();

                    if(p==0 && treeType ==3) {
                        xLInit = (float) (temp.getTipX() + leftBranchContraction * ( 0 - w * Math.sin(leftBranchAngle * Math.PI / 180.0)));
                        yLInit = temp.getTipY();
                        zLInit = (float) (temp.getTipZ() + leftBranchContraction * ( 0 + w * Math.cos(leftBranchAngle * Math.PI/ 180.0)));
                        xRInit = (float) (temp.getTipX() + rightBranchContraction * ( 0 - w * Math.sin(rightBranchAngle * Math.PI / 180.0)));
                        yRInit = temp.getTipY();
                        zRInit = (float) (temp.getTipZ() + rightBranchContraction * ( 0 + w * Math.cos(rightBranchAngle * Math.PI/ 180.0)));
                    }
                    else {
                        xLInit = Branch.getTipX(leftBranchAngle, temp, leftBranchContraction);
                        yLInit = Branch.getTipY(leftBranchAngle, temp, leftBranchContraction);
                        zLInit = Branch.getTipZ(leftBranchAngle,temp,leftBranchContraction);
                        xRInit = Branch.getTipX(rightBranchAngle, temp, rightBranchContraction);
                        yRInit = Branch.getTipY(rightBranchAngle, temp, rightBranchContraction);
                        zRInit = Branch.getTipZ(rightBranchAngle,temp,rightBranchContraction);
                    }


                    float sumLX = xLInit;
                    float sumLY = yLInit;
                    float sumLZ = zLInit;

                    float sumRX = xRInit;
                    float sumRY = yRInit;
                    float sumRZ = zRInit;

                    float xMInit = Branch.getMidTipX(temp, middleBranchContraction);
                    float yMInit = Branch.getMidTipY(temp, middleBranchContraction);
                    float zMInit = Branch.getMidTipZ(temp, middleBranchContraction);
                    float sumMX = xMInit;
                    float sumMY = yMInit;
                    float sumMZ = zMInit;


                    for(int k = 0; k<nonuniflist.size(); k++) {
                        sumLX += nonuniflist.get(k).dx(xLInit, yLInit, zLInit);
                        sumRX += nonuniflist.get(k).dx(xRInit, yRInit, zRInit);
                        sumMX += nonuniflist.get(k).dx(xRInit, yRInit, zRInit);

                        sumLY += nonuniflist.get(k).dy(xLInit, yLInit, zLInit);
                        sumRY += nonuniflist.get(k).dy(xRInit, yRInit, zRInit);
                        sumMY += nonuniflist.get(k).dy(xRInit, yRInit, zRInit);

                        sumLZ += nonuniflist.get(k).dz(xLInit, yLInit, zLInit);
                        sumRZ += nonuniflist.get(k).dz(xRInit, yRInit, zRInit);
                        sumMZ += nonuniflist.get(k).dz(xRInit, yRInit, zRInit);
                    }

                    if(treeType >1) {
                        sumLX += unif.getDx();
                        sumLY += unif.getDy();
                        sumLZ += unif.getDz();

                        sumRX += unif.getDx();
                        sumRY += unif.getDy();
                        sumRZ += unif.getDz();

                        sumMX += unif.getDx();
                        sumMY += unif.getDy();
                        sumMZ += unif.getDz();
                    }

                    Branch b1 = new Branch(temp.getTipX(), temp.getTipY(), temp.getTipZ(), sumLX, sumLY, sumLZ, temp.getEndradius(), temp.getEndradius() * radiusContraction);

                    Branch b2 = new Branch(temp.getTipX(), temp.getTipY(), temp.getTipZ(), sumRX, sumRY, sumRZ, temp.getEndradius(), temp.getEndradius() * radiusContraction);

                    if(treeType ==3) {
                        Branch b3 = new Branch(temp.getTipX(), temp.getTipY(), temp.getTipZ(), sumMX, sumMY, sumMZ, temp.getEndradius(), temp.getEndradius() * radiusContraction);
                        next.add(b3);
                        total.add(b3);
                    }

                    next.add(b1);
                    next.add(b2);
                    total.add(b1);
                    total.add(b2);
                }
                current.clear();
                current.addAll(next);
                next.clear();

            }
        }

        //now render the tree - done incorrectly
        for(int p =0; p < total.size(); p++) {

            gl.glPushMatrix();
                Branch branch = total.get(p);
            //gl.glRotatef(divergenceAngle, 0, 0, 1);
                drawBranch(gl,branch);
            gl.glPopMatrix();


        }


//        if (depth == 0)
//            return;
//        if (depth <= 2)
//            gl.glColor3f(0, 1.0f, (float) 64.0/255);
//        else
//            gl.glColor3f( (float) 128.0/255 , (float) 64.0/255, 0);
//
//        gl.glPushMatrix();
//            glu.gluCylinder(qobj, radius, this.radiusContraction * radius, length, 40, 40);
//            gl.glPushMatrix();
//                gl.glTranslatef(0.0f, 0.0f, length);
//               // gl.glRotatef(this.divergenceAngle, 0.0f, 0.0f, 1.0f);
//                gl.glPushMatrix();
//                    gl.glRotatef(this.leftBranchAngle, 0.0f, -1.0f, 0.0f);
//                    tree(gl, this.leftBranchContraction * length, this.radiusContraction * radius, depth - 1);
//                gl.glPopMatrix();
//                gl.glPushMatrix();
//                    gl.glRotatef(this.rightBranchAngle, 0.0f, -1.0f, 0.0f);
//                    tree(gl, this.rightBranchContraction * length, this.radiusContraction * radius, depth - 1);
//                gl.glPopMatrix();
//            gl.glPopMatrix();
//        gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        glu.gluDeleteQuadric(qobj);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //System.out.println("Khrea");
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

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);

        //float[] ambientLight = { 0.1f, 0.f, 0.f,0f };  // weak RED ambient
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);

        float[] diffuseLight = { 0.5f,0.5f,0.5f,0f };  // multicolor diffuse
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);

        gl.glEnable( GL2.GL_DEPTH_TEST );
        gl.glDepthFunc( GL2.GL_LEQUAL );
        gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );

        qobj = glu.gluNewQuadric();
        glu.gluQuadricNormals(qobj, GLU.GLU_SMOOTH);
    }

//    @Override
//    public void keyTyped(KeyEvent e) {
//
//    }
//
//    @Override
//    public void keyPressed(KeyEvent e) {
//
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//
//    }
}
