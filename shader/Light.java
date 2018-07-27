package shader;

import windowing.graphics.Color;
import geometry.Point3DH;
import java.util.ArrayList;

public class Light{
  public static ArrayList<Light> lightList = new ArrayList<>();
  Color intensity;
  Point3DH cameraSpaceLocation;
  private double fattA;
  private double fattB;

  public Light(Color intensity, Point3DH CSL, double fattA, double fattB){
    this.intensity = intensity;
    this.cameraSpaceLocation = CSL;
    this.fattA = fattA;
    this.fattB = fattB;

    lightList.add(this); //any any light into the list of lights
  }


  public Color getIntensity(){
      return intensity;
  }

  public Point3DH getCSL(){
    return cameraSpaceLocation;
  }

  public double getfattA(){
    return fattA;
  }

  public double getfattB(){
    return fattB;
  }

  public static ArrayList<Light> getLightList(){
    return lightList;
  }

}
