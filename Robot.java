/* I declare that this code is my own work */
/* Author <Ruiqing Xu> <rxu22@sheffield.ac.uk> */

import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

class Robot
{
    
    private SGNode robotRoot;
    private TransformNode robotMoveTranslate;
    private TransformNode pose1Transfrom;
    private TransformNode pose2Transfrom;
    private TransformNode pose3Transfrom;

    private Vec3 prePosition = new Vec3();
    private float prePose1Angle;
    private float prePose2Angle;
    private float prePose3Angle;

    private Vec3 position;
    private float pose1Angle;
    private float pose2Angle;
    private float pose3Angle;

    private void copyToPre()
    {
        prePosition.x = position.x;
        prePosition.y = position.y;
        prePosition.z = position.z;

        prePose1Angle = pose1Angle;
        prePose2Angle = pose2Angle;
        prePose3Angle = pose3Angle;
    }

    // Set the relevant value of pose 1
    public void SetPose1()
    {
        position = new Vec3(-3.0f, 0.4f, -8.0f);
        pose1Angle = 20.0f;
        pose2Angle = 30.0f;
        pose3Angle = -50.0f;
    }

    // Set the relevant value of pose 2
    public void SetPose2()
    {
        position = new Vec3(3.0f, 0.4f, -6.0f);
        pose1Angle = -20.0f;
        pose2Angle = -30.0f;
        pose3Angle = 50.0f;
    }
        
    // Set the relevant value of pose 3
    public void SetPose3()
    {
        position = new Vec3(5.0f, 0.4f, -2.0f);
        pose1Angle = 10.0f;
        pose2Angle = 20.0f;
        pose3Angle = -30.0f;
    }
        
    // Set the relevant value of pose 4
    public void SetPose4()
    {
        position = new Vec3(0.0f, 0.4f, 8.0f);
        pose1Angle = 20.0f;
        pose2Angle = -15.0f;
        pose3Angle = 50.0f;
    }
        
    // Set the relevant value of pose 5
    public void SetPose5()
    {
        position = new Vec3(-8.0f, 0.4f, 1.0f);
        pose1Angle = -20.0f;
        pose2Angle = 15.0f;
        pose3Angle = 10.0f;
    }
    public Robot()
    {

    }

    public void initialise(GL3 gl, Camera camera, Light light, Light sunLight, Light spotLight)
    {
        preSecond = getSeconds();

        SetPose4();
        copyToPre();

        // the robot textures
        int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2.jpg");
        int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2.jpg");

        // the nose and ears textures
        int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/mar0kuu2.jpg");
        int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/mar0kuu2_specular.jpg");

        // initialise the mesh
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        
        // initialise the shader 
        Shader shader = new Shader(gl, "vs_texture.txt", "fs_texture.txt");
        
        // initialise the material
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);

        // initialise the material models
        Mat4 modelMatrix = Mat4Transform.scale(1,1,1);
        Model sphere = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, mesh, textureId1, textureId2);
        Model sphereNose = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, mesh, textureId3, textureId4);

        // create the robot root node
        robotRoot = new NameNode("root");
        robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,0.4f,0));
     
        // create the robot foot node
        pose1Transfrom = new TransformNode("foot transform", modelMatrix);
        modelMatrix = Mat4Transform.scale(0.8f, 0.8f, 0.8f);
        TransformNode footTransform = new TransformNode("foot transform", modelMatrix);
            ModelNode footShape = new ModelNode("Cube(foot)", sphere);

        // create the robot body node
        modelMatrix = Mat4Transform.translate(0.0f, 1.6f, 0.0f);
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.8f, 2.4f, 0.8f));
        TransformNode bodyTransform = new TransformNode("body transform", modelMatrix);
            ModelNode bodyShape = new ModelNode("Cube(body)", sphere);

        // create the robot neck node
        modelMatrix = Mat4Transform.translate(0.0f, 2.8f, 0.0f);
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.2f, 0.2f, 0.2f));
        TransformNode neckTransform = new TransformNode("neck transform", modelMatrix);
            ModelNode neckShape = new ModelNode("Cube(neck)", sphere);

        // create the robot head node
        modelMatrix = Mat4Transform.rotateAroundX(0.0f);
        pose2Transfrom = new TransformNode("pose2 transform", modelMatrix);

        modelMatrix = Mat4Transform.translate(0.0f, 2.5f, 0.0f);
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(4.0f, 4.0f, 4.0f));
        TransformNode headTransform = new TransformNode("head transform", modelMatrix);
            ModelNode headShape = new ModelNode("Cube(head)", sphere);

        // create the robot nose node
        modelMatrix = Mat4Transform.translate(0.0f, 0.0f, 0.6f);
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.2f, 0.2f, 0.2f));
        TransformNode noseTransform = new TransformNode("nose transform", modelMatrix);
            ModelNode noseShape = new ModelNode("Cube(nose)", sphereNose);

        modelMatrix = Mat4Transform.rotateAroundX(0.0f);
        pose3Transfrom = new TransformNode("pose3 transform", modelMatrix);

        // create the robot ears node
        modelMatrix = Mat4Transform.translate(-0.6f, 0.6f, 0.0f);
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(45.0f));
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.2f, 0.8f, 0.2f));
        TransformNode ear1Transform = new TransformNode("ear1 transform", modelMatrix);
            ModelNode ear1Shape = new ModelNode("Cube(ear1)", sphereNose);          
        
        modelMatrix = Mat4Transform.translate(0.6f, 0.6f, 0.0f);
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundZ(-45.0f));
        modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.2f, 0.8f, 0.2f));
        TransformNode ear2Transform = new TransformNode("ear2 transform", modelMatrix);
            ModelNode ear2Shape = new ModelNode("Cube(ear2)", sphereNose);

        // build the hierarchical relation.
        robotRoot.addChild(robotMoveTranslate);
            robotMoveTranslate.addChild(pose1Transfrom);
                pose1Transfrom.addChild(footTransform);
                    footTransform.addChild(footShape);
                pose1Transfrom.addChild(bodyTransform);
                    bodyTransform.addChild(bodyShape);
                pose1Transfrom.addChild(neckTransform);
                    neckTransform.addChild(neckShape);
                        neckTransform.addChild(pose2Transfrom);
                            pose2Transfrom.addChild(headTransform);
                                headTransform.addChild(headShape);
                                headTransform.addChild(noseTransform);
                                    noseTransform.addChild(noseShape);
                                headTransform.addChild(pose3Transfrom);
                                    pose3Transfrom.addChild(ear1Transform);
                                        ear1Transform.addChild(ear1Shape);
                                    pose3Transfrom.addChild(ear2Transform);
                                        ear2Transform.addChild(ear2Shape);

        robotRoot.update();
    }

    private double preSecond = 0.0f;

    private float getNextValue(float v1, float v2, float delta)
    {
        // Get the position of the next point of a float value animation
        float add = v2 - v1;
        float needAdd = (add == 0.0f ? 0.0f : (add / Math.abs(add))) * delta;
        if(delta > Math.abs(add))
        {
            return v1 + add;
        }
        else
        {
            return v1 + needAdd;
        }
    }

    public void render(GL3 gl)
    {
        // Calculate the position of the next point in the translate animation
        float deltaSecond = (float)(getSeconds() - preSecond);
        prePosition.x = getNextValue(prePosition.x, position.x, deltaSecond);
        prePosition.y = getNextValue(prePosition.y, position.y, deltaSecond);
        prePosition.z = getNextValue(prePosition.z, position.z, deltaSecond);
        robotMoveTranslate.setTransform(Mat4Transform.translate(prePosition.x, prePosition.y, prePosition.z));
        
        // Calculate the position of the next point in the rotation animation
        prePose1Angle = getNextValue(prePose1Angle, pose1Angle, deltaSecond * 5.0f);
        prePose2Angle = getNextValue(prePose2Angle, pose2Angle, deltaSecond * 5.0f);
        prePose3Angle = getNextValue(prePose3Angle, pose3Angle, deltaSecond * 5.0f);
        pose1Transfrom.setTransform(Mat4Transform.rotateAroundX(prePose1Angle));
        pose2Transfrom.setTransform(Mat4Transform.rotateAroundX(prePose2Angle));
        pose3Transfrom.setTransform(Mat4Transform.rotateAroundX(prePose3Angle));
        robotRoot.update();
        robotRoot.draw(gl);

        preSecond = getSeconds();
    }

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
    }

}