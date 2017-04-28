import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.TextureData;
import sun.awt.image.ImageWatched;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Nikhil on 11/03/17.
 */

public class Tree implements GLEventListener {
    private GLU glu = new GLU();
    private GLUquadric qobj;

    private int growthLevel = 9;

    private float branchLength = 0.5f;
    private float branchRadius = 0.07f;

    private float leftBranchAngle = 10.0f;
    private float rightBranchAngle = -60.0f;

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
    private Texture treeTexture;


    private float divergenceAngle = 90;
    private int divergenceCounter;

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
        this.divergenceCounter = 1;
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
        System.out.println("hello");
        final GL2 gl = drawable.getGL().getGL2();
        float[] rgba = {1f, 1f, 1f};
        //gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
        //gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, rgba, 0);
       //gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

        // Apply texture.
        treeTexture.enable(gl);
        treeTexture.bind(gl);

        // Draw sphere.

        glu.gluQuadricTexture(qobj, true);
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
        treeTexture.disable(gl);

    }

    public float[] getDivergenceMatrix(Branch branch, GL2 gl) {
        float[] array = new float[16];
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(branch.getBaseX(), branch.getBaseY(), branch.getBaseZ());
        float alpha = (float) Math.acos((branch.getBaseZ() - branch.getMotherBranch().getBaseZ()) / Math.sqrt(((branch.getBaseZ() - branch.getMotherBranch().getBaseZ()) * (branch.getBaseZ() - branch.getMotherBranch().getBaseZ()) +
                (branch.getBaseX() - branch.getMotherBranch().getBaseX()) * (branch.getBaseX() - branch.getMotherBranch().getBaseX()))));
        float beta = (float) Math.atan((branch.getBaseX() - branch.getMotherBranch().getBaseX()) / Math.sqrt(((branch.getBaseZ() - branch.getMotherBranch().getBaseZ()) * (branch.getBaseZ() - branch.getMotherBranch().getBaseZ()) +
                (branch.getBaseX() - branch.getMotherBranch().getBaseX()) * (branch.getBaseX() - branch.getMotherBranch().getBaseX()))));
        gl.glRotatef(-alpha, 1, 0, 0);
        gl.glRotatef(beta, 0, 1, 0);
        gl.glRotatef(divergenceAngle, 0, 0, 1);
        gl.glRotatef(-beta, 0, 1, 0);
        gl.glRotatef(alpha, 1, 0, 0);
        gl.glTranslatef(-branch.getBaseX(), -branch.getBaseY(), -branch.getBaseZ());
        gl.glGetFloatv(gl.GL_MODELVIEW_MATRIX, array, 0);
        gl.glPopMatrix();
        return array;
    }





    private void drawBranch(GL2 gl, Branch branch, int p) {
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

//        //more failed code
//        if(p>2) {
//            float[] divergenceMatrix = getDivergenceMatrix(branch, gl);
//            gl.glMultMatrixf(divergenceMatrix, 0);
//        }

//        if(p > 2) {
//            //failed divergence angle code based on reflection to find angle around which rotation must occur
//            float motherdirX = branch.getMotherBranch().getTipX() - branch.getMotherBranch().getBaseX();
//            float motherdirY = branch.getMotherBranch().getTipY() - branch.getMotherBranch().getBaseY();
//            float motherdirZ = branch.getMotherBranch().getTipZ() - branch.getMotherBranch().getBaseZ();
//            float mothermod = (float) Math.sqrt(motherdirX*motherdirX + motherdirY*motherdirY + motherdirZ*motherdirZ);
//
//            float dot = motherdirX*z_final_x + motherdirY*z_final_y + motherdirZ*z_final_z;
//            float reflX = ((2*(motherdirX) * (dot)) / ((mothermod) * (mothermod) )) - z_final_x;
//            float reflY = ((2*(motherdirY) * (dot)) / ((mothermod) * (mothermod) )) - z_final_y;
//            float reflZ = ((2*(motherdirZ) * (dot)) / ((mothermod) * (mothermod) )) - z_final_z;
//
//            float reflmag = (float) Math.sqrt(reflX*reflX + reflY*reflY + reflZ*reflZ);
//            reflX = (reflX/reflmag) * mothermod;
//            reflY = (reflY/reflmag) * mothermod;
//            reflZ = (reflZ/reflmag) * mothermod;
//            gl.glRotatef(divergenceAngle * divergenceCounter, reflX, reflY, reflZ);
//            divergenceCounter++;
//        }

        glu.gluSphere(qobj, branch.getStartradius()*1.005,40,40);
        glu.gluCylinder(qobj, branch.getStartradius(), branch.getEndradius(), branch.getLength(), 40, 40);
//        if(p >2 ) {
//            gl.glRotatef(-divergenceAngle * (divergenceCounter, reflX, reflY, reflZ);
//        }
        //.glTranslatef(-branch.getBaseX(), -branch.getBaseY(), - branch.getBaseZ());

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
                b1.setMotherBranch(b);

                Branch b2 = new Branch(b.getTipX(),b.getTipY(),b.getTipZ(), sumRX, sumRY, sumRZ, b.getEndradius(), b.getEndradius() * radiusContraction);
                b2.setMotherBranch(b);

                if(treeType ==3) {
                    Branch b3 =new Branch(b.getTipX(), b.getTipY(), b.getTipZ(), sumMX, sumMY, sumMZ, b.getEndradius(), b.getEndradius() * radiusContraction);
                    b3.setMotherBranch(b);
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
                    b1.setMotherBranch(temp);
                    Branch b2 = new Branch(temp.getTipX(), temp.getTipY(), temp.getTipZ(), sumRX, sumRY, sumRZ, temp.getEndradius(), temp.getEndradius() * radiusContraction);
                    b2.setMotherBranch(temp);
                    if(treeType ==3) {
                        Branch b3 = new Branch(temp.getTipX(), temp.getTipY(), temp.getTipZ(), sumMX, sumMY, sumMZ, temp.getEndradius(), temp.getEndradius() * radiusContraction);
                        b3.setMotherBranch(temp);
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
                drawBranch(gl,branch,p);
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

        try{

            File im = new File("tree.jpg");
            treeTexture = TextureIO.newTexture(im, false);

        }catch(IOException e){
            e.printStackTrace();
        }
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
