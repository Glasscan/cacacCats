package shader;
import client.interpreter.SimpInterpreter;

import windowing.graphics.Color;
import geometry.Point3DH;
import geometry.Vertex3D;
import shader.Light;

import java.util.ArrayList;

public class Lighting{
  private Lighting(){}

  //CSP is cameraSpacePoint of object
  public static Color light(Vertex3D CSP, Color kDiffuse, Point3DH normal){
      Color newRed, newGreen, newBlue;
      double redSum, greenSum, blueSum;
      Light light;
      Color lightIntensity;
      Point3DH lightPoint;
      Point3DH vectorL, vectorR, vectorV; //light the normal they are all UNIT vectors
      double naturalRed, naturalGreen, naturalBlue; //Kd*Ia
      double atten; //attenuation
      double NdotL, VdotR;

      double kSpec = SimpInterpreter.getKSpec();
      double specExp = SimpInterpreter.getSpecExp();
      ArrayList<Light> lightList = Light.getLightList();
      Color ambient = SimpInterpreter.getAmbient(); //Ia ; reuse for each RGB

      //red
      naturalRed = kDiffuse.getR()*ambient.getR();
      naturalGreen = kDiffuse.getG()*ambient.getG();
      naturalBlue = kDiffuse.getB()*ambient.getB();

      redSum = greenSum = blueSum = 0.0;
      for(int i = 0; i < lightList.size(); i++){
        light = lightList.get(i);
        lightIntensity = light.getIntensity();
        lightPoint = light.getCSL();
        atten = 1.0/(light.getfattA() + light.getfattB()*calcDistance(lightPoint, CSP));
        vectorL = findVectorL(lightPoint, CSP);
        vectorR = calculateVectorR(vectorL, normal);
        vectorV = findVectorV(CSP);
        NdotL = dotProduct(normal, vectorL);
        VdotR = dotProduct(vectorV, vectorR);

        redSum = redSum + (lightIntensity.getR()*atten)*(kDiffuse.getR()*NdotL + kSpec*Math.pow(VdotR, specExp));
        greenSum = greenSum + (lightIntensity.getG()*atten)*(kDiffuse.getG()*NdotL + kSpec*Math.pow(VdotR, specExp));
        blueSum = blueSum + (lightIntensity.getB()*atten)*(kDiffuse.getB()*NdotL + kSpec*Math.pow(VdotR, specExp));

        System.out.println(" Normal: " + normal +  "\n VectorV: " + vectorV + "\n vectorR: " + vectorR + "\n VectorL: " + vectorL);
        System.out.println("-------------------");
      }
      redSum = naturalRed + redSum;
      greenSum = naturalGreen + greenSum;
      blueSum = naturalBlue + blueSum;

      return new Color(redSum, greenSum, blueSum);
  }

  //UNIT VECTOR from the object TO THE light
  private static Point3DH findVectorL(Point3DH lightPoint, Vertex3D object){
    double x = lightPoint.getX() - object.getX();
    double y = lightPoint.getY() - object.getY();
    double z = lightPoint.getZ() - object.getZ();
    double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

    //convert to unit vector
    double newX = x/magnitude;
    double newY = y/magnitude;
    double newZ = z/magnitude;

    return new Point3DH(newX, newY, newZ);
  }

  //learned reflection method from the internet
  private static Point3DH calculateVectorR(Point3DH vectorL, Point3DH normal){
    double LdotN = dotProduct(normal, vectorL);
    double x = 2.0*(LdotN)*normal.getX() - vectorL.getX();
    double y = 2.0*(LdotN)*normal.getY() - vectorL.getY();
    double z = 2.0*(LdotN)*normal.getZ() - vectorL.getZ();

    return new Point3DH(x, y, z);
  }

  //unit vector from object TO THE camera (contradicts assn4 instructions)
  //makes a very bold assumption that we are viewing from origin (325, 325, -0.1)
  private static Point3DH findVectorV(Vertex3D object){
    double x = 325.0 - object.getX();
    double y = 325.0 - object.getY();
    double z = -0.1 - object.getZ();
    double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

    //convert to unit vector
    double newX = x/magnitude;
    double newY = y/magnitude;
    double newZ = z/magnitude;

    return new Point3DH(newX, newY, newZ);
  }
  private static double dotProduct(Point3DH v1, Point3DH v2){
    double product = 0.0;
    product = product + v1.getX()*v2.getX();
    product = product + v1.getY()*v2.getY();
    product = product + v1.getZ()*v2.getZ();
    if(product < 0.0) product = 0.0;//negative dot-product treated as 0 (lecture 16)
    return product;
  }
  private static double calcDistance(Point3DH v1, Vertex3D v2){ //because I know the input
    double distance = 0.0;
    double dx = Math.pow(v2.getX() - v1.getX(), 2);
    double dy = Math.pow(v2.getY() - v1.getY(), 2);
    double dz = Math.pow(v2.getZ() - v1.getZ(), 2);
    distance = Math.sqrt(dx + dy + dz);
    return distance;
  }
}
