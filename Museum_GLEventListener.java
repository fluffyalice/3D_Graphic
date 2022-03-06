/* writen by ch7 */
/* I declare that line 90 to line 363 this code is my own work */
/* Author <Ruiqing Xu> <rxu22@sheffield.ac.uk> */
import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Museum_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Museum_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    sunLight.dispose(gl);
    spotLight.dispose(gl);
    floor.dispose(gl);

    spotlight1.dispose(gl);
    spotlight2.dispose(gl);
    spotlight3.dispose(gl);
    
    spotlightBulb1.dispose(gl);
    spotlightBulb2.dispose(gl);

    wall.dispose(gl);
    
    wall1.dispose(gl);
    wall2.dispose(gl);
    wall3.dispose(gl);
    wall4.dispose(gl);

    cloud.dispose(gl);
    door.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
  public void turnLight()
  {
    light.setOn(light.getIsOn() == 0 ? 1 : 0);
  }

  public void turnSun()
  {
    sunLight.setOn(sunLight.getIsOn() == 0 ? 1 : 0);
  }

  public void turnSpot()
  {
    spotLight.setOn(spotLight.getIsOn() == 0 ? 1 : 0);
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Model floor;
  private Light light;
  private Light sunLight;
  private Light spotLight;
  private Robot robot;

  private Model door;

  private Model wall;
  private Model wall1;
  private Model wall2;
  private Model wall3;
  private Model wall4;

  private Model cloud;

  private Model spotlight1;
  private Model spotlight2;
  private Model spotlight3;
  private Model spotlightBulb1;
  private Model spotlightBulb2;

  private Model egg1;
  private Model egg2;
  
  private Model mobilePhone1;
  private Model mobilePhone2;
  private Model mobilePhone3;

  private Mat4 spotlightMat;
  
  public Robot getRobot()
  {
      return robot;
  }

  private void initialise(GL3 gl) {
    // the floor textures
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    
    // the spot light textures
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    
    // the wall textures
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/ven0aaa2_specular.jpg");

    // this cloud texture
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");


    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");

    // the egg textures
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/ear0xuu2.jpg");
    int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/ear0xuu2_specular.jpg");

    // the mobile phone textures
    int[] textureId11 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId12 = TextureLibrary.loadTexture(gl, "textures/door.jpg");
    int[] textureId13 = TextureLibrary.loadTexture(gl, "textures/screen.jpg");

    // initialise the lights        
    light = new Light(gl);
    light.setCamera(camera);
    sunLight = new Light(gl);
    sunLight.setCamera(camera);
    spotLight = new Light(gl);
    spotLight.setCamera(camera);
    
    // initialise the robot
    robot = new Robot();
    robot.initialise(gl, camera, light, sunLight, spotLight);

    // initialise the meshes
    Mesh planeMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Mesh cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mesh sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());

    // initialise the shader 
    Shader shader = new Shader(gl, "vs_texture.txt", "fs_texture.txt");

    // initialise the material
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);

    // initialise this floor
    Mat4 modelMatrix = Mat4Transform.scale(20.0f,1.0f,20.0f);
    floor = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId0, textureId1);
    
    // initialise the spot light
    modelMatrix = Mat4Transform.translate(8.0f, 0.1f, 3.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.6f, 0.2f, 0.6f));
    spotlight1 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, cubeMesh, textureId2, textureId3);

    modelMatrix = Mat4Transform.translate(8.0f, 3.1f, 3.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.2f, 6.0f, 0.2f));
    spotlight2 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, cubeMesh, textureId2, textureId3);
  
    modelMatrix = Mat4Transform.translate(7.1f, 6.2f, 3.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(2.0f, 0.2f, 0.2f));
    spotlight3 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, cubeMesh, textureId2, textureId3);
  
    spotlightBulb1 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, cubeMesh, textureId2, textureId3);
    spotlightBulb2 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, sphereMesh, textureId2, textureId3);

    // initialise the walls
    modelMatrix = Mat4Transform.translate(0.0f, 5.0f, -10.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(20.0f,1.0f,10.0f));
    wall = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId4, textureId5);
    
    modelMatrix = Mat4Transform.rotateAroundZ(-90.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(-8.5f, -10.0f, 0.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(3.0f,1.0f,20.0f));
    wall1 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId4, textureId5);
    
    modelMatrix = Mat4Transform.rotateAroundZ(-90.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(-1.5f, -10.0f, 0.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(3.0f,1.0f,20.0f));
    wall2 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId4, textureId5);
    
    modelMatrix = Mat4Transform.rotateAroundZ(-90.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(-5.0f, -10.0f, 7.5f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(4.0f,1.0f,5.0f));
    wall3 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId4, textureId5);
    
    modelMatrix = Mat4Transform.rotateAroundZ(-90.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(-5.0f, -10.0f, -7.5f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(4.0f,1.0f,5.0f));
    wall4 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId4, textureId5);
    
    // initialise the cloud
    modelMatrix = Mat4Transform.rotateAroundZ(-90.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(-5.0f, -15.0f, 0.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(30.0f,1.0f,30.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundY(90.0f));
    cloud = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId6);
    
    // initialise the door
    modelMatrix = Mat4Transform.translate(-5.0f, 4.0f, -9.8f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(4.0f,1.0f,8.0f));
    door = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId12);

    // initialise the egg
    modelMatrix = Mat4Transform.translate(0.0f, 0.5f, 0.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(2.0f,1.0f,2.0f));    
    egg1 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, cubeMesh, textureId9, textureId10);
    
    modelMatrix = Mat4Transform.translate(0.0f, 2.0f, 0.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(1.5f,2.0f,1.5f));
    Material eggMaterial = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(1.0f, 1.0f, 1.0f), 128.0f);
    egg2 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, sphereMesh, textureId2, textureId3);

    
    // initialise the mobile phone
    modelMatrix = Mat4Transform.translate(6.0f, 0.5f, -4.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(2.0f,1.0f,2.0f));
    mobilePhone1 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, cubeMesh, textureId9, textureId10);
    
    modelMatrix = Mat4Transform.translate(6.0f, 2.5f, -4.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(2.0f,1.0f,3.0f));
    mobilePhone2 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId11);
    
    modelMatrix = Mat4Transform.translate(6.0f, 2.5f, -3.8f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(90.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(1.3f,1.0f,2.6f));
    mobilePhone3 = new Model(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, planeMesh, textureId13);
  }

  private void setSpotLightAngle(float angle)
  {
    // set the spot light transform
    Mat4 modelMatrix = Mat4Transform.translate(6.2f, 6.1f, 3.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(180.0f + angle));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(0.0f, 0.3f, 0.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.4f, 0.6f, 0.4f));
    spotlightBulb1.setModelMatrix(modelMatrix);
    
    modelMatrix = Mat4Transform.translate(6.2f, 6.1f, 3.0f);
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.rotateAroundX(180.0f + angle));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.translate(0.0f, 0.6f, 0.0f));
    modelMatrix = Mat4.multiply(modelMatrix, Mat4Transform.scale(0.4f, 0.4f, 0.4f));

    spotlightMat = modelMatrix;

    spotlightBulb2.setModelMatrix(modelMatrix);
  }
 
  private Vec3 multiplyMat4Vec4(Mat4 m, Vec4 v)
  {
    // calculate the matrix multiplication
    float[] arr = m.toFloatArrayForGLSL();
    Vec3 result = new Vec3();
    result.x = arr[0] * v.x + arr[4] * v.y + arr[8] * v.z + arr[12] * v.w;
    result.y = arr[1] * v.x + arr[5] * v.y + arr[9] * v.z + arr[13] * v.w;
    result.z = arr[2] * v.x + arr[6] * v.y + arr[10] * v.z + arr[14] * v.w;
    return result;
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    robot.render(gl);

    // Calculate the rotation angle of the spot light
    float rotateAngle = 30f*(float)Math.sin(getSeconds());
    setSpotLightAngle(rotateAngle);

    // set spot light source position and rotation
    spotLight.setPosition(multiplyMat4Vec4(spotlightMat, new Vec4(0.0f, 0.0f, 0.0f, 1.0f)));
    spotLight.setDirection(multiplyMat4Vec4(spotlightMat, new Vec4(0.0f, -1.0f, 0.0f, 0.0f)));

    // render the floor
    floor.render(gl);

    // render the spot light
    spotlight1.render(gl);
    spotlight2.render(gl);
    spotlight3.render(gl);

    spotlightBulb1.render(gl);
    spotlightBulb2.render(gl);

    // render the walls
    wall.render(gl);
    wall1.render(gl);
    wall2.render(gl);
    wall3.render(gl);
    wall4.render(gl);
    
    // render the cloud
    cloud.render(gl);
    
    // render the door
    door.render(gl);

    // render the egg    
    egg1.render(gl);
    egg2.render(gl);
    
    // render the mobile phone
    mobilePhone1.render(gl);
    mobilePhone2.render(gl);
    mobilePhone3.render(gl);
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  
}