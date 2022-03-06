/* writen by ch7 */
/* I declare that line 65 to line 90 this code is my own work */
/* Author <Ruiqing Xu> <rxu22@sheffield.ac.uk> */

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Model {
  
  private Mesh mesh;
  private int[] textureId1; 
  private int[] textureId2; 
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light light;
  private Light sunLight;
  private Light spotLight;
  
  public Model(GL3 gl, Camera camera, Light light, Light sunLight, Light spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light = light;
    this.sunLight = sunLight;
    this.spotLight = spotLight;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }
  
  public Model(GL3 gl, Camera camera, Light light, Light sunLight, Light spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, mesh, textureId1, null);
  }
  
  public Model(GL3 gl, Camera camera, Light light, Light sunLight, Light spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light, sunLight, spotLight, shader, material, modelMatrix, mesh, null, null);
  }
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void setLight(Light light) {
    this.light = light;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    // Set the parameter information of the point light source
    shader.setInt(gl, "light.isOn", light.getIsOn());
    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());

    // Set the parameter information of the sun light source
    shader.setInt(gl, "sunLight.isOn", sunLight.getIsOn());
    shader.setVec3(gl, "sunLight.direction", sunLight.getDirection());
    shader.setVec3(gl, "sunLight.ambient", sunLight.getMaterial().getAmbient());
    shader.setVec3(gl, "sunLight.diffuse", sunLight.getMaterial().getDiffuse());
    shader.setVec3(gl, "sunLight.specular", sunLight.getMaterial().getSpecular());

    // Set the parameter information of the spot light source
    shader.setInt(gl, "spotLight.isOn", spotLight.getIsOn());
    shader.setVec3(gl, "spotLight.position", spotLight.getPosition());
    shader.setVec3(gl, "spotLight.direction", spotLight.getDirection());
    shader.setVec3(gl, "spotLight.ambient", spotLight.getMaterial().getAmbient());
    shader.setVec3(gl, "spotLight.diffuse", spotLight.getMaterial().getDiffuse());
    shader.setVec3(gl, "spotLight.specular", spotLight.getMaterial().getSpecular());

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());  

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    mesh.render(gl);
  } 
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
  }
  
}