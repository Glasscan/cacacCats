package shader;

import windowing.graphics.Color;
import geometry.Vertex3D;
import geometry.Point3DH;
import polygon.Polygon;

import shader.Light;
import shader.Lighting;
import shader.FaceShader;
import shader.VertexShader;
import shader.PixelShader;

//store all the possible shader methods

public class Shaders{
  public Shaders(){};

  public static Polygon NullFaceShader(Polygon polygon){
    return polygon;
  }

  public static Polygon FlatFaceShader(Polygon polygon){
    Point3DH normal;

    Vertex3D p1 = polygon.get(0).useCameraSpace(); //magic
    Vertex3D p2 = polygon.get(1).useCameraSpace();
    Vertex3D p3 = polygon.get(2).useCameraSpace();

    //System.out.println(p1 + " \n " + p2 + " \n " + p3);
    //get the center point
    double centerX = (p1.getX() + p2.getX() + p3.getX())/3.0;
    double centerY = (p1.getY() + p2.getY() + p3.getY())/3.0;
    double centerZ = (p1.getZ() + p2.getZ() + p3.getZ())/3.0;
    Point3DH center = new Point3DH(centerX, centerY, centerZ);

    if(p1.hasNormal()){
      return polygon; //finish last
    }
    else{
      Point3DH vLeft = calculateVectorDifference(p1, p2);
      Point3DH vRight = calculateVectorDifference(p1, p3);

      normal = crossProduct(vLeft, vRight); //confirmed that normal is calculated correctly
      //System.out.println(p1 + " \n" + p2 + " \n" + p3);
      Color newColor = Lighting.light(new Vertex3D(center, p1.getColor()), p1.getColor(), normal);
      polygon.setShadeColor(newColor);
    }

    return polygon;
  }


  public static Vertex3D NullVertexShader(Polygon polygon, Vertex3D point){
    return point;
  }
  public static Vertex3D GouraudVertexShader(Polygon polygon, Vertex3D point){
    return point;
  }
  public static Vertex3D PhongVertexShader(Polygon polygon, Vertex3D point){
    return point;
  }


  public static Color NullPixelShader(Polygon polygon, Vertex3D point){
    return point.getColor();
  }
  public static Color FlatPixelShader(Polygon polygon, Vertex3D point){
    return polygon.getShadeColor();
  }
  public static Color GouraudPixelShader(Polygon polygon, Vertex3D point){
    return point.getColor();
  }
  public static Color PhongPixelShader(Polygon polygon, Vertex3D point){
    return point.getColor();
  }

  //built in subtract function exists
  private static Point3DH calculateVectorDifference(Vertex3D p1, Vertex3D p2){
    double x = p2.getX() - p1.getX();
    double y = p2.getY() - p1.getY();
    double z = p2.getZ() - p1.getZ();
    return new Point3DH(x, y, z);
  }

  private static Point3DH crossProduct(Point3DH p1, Point3DH p2){
    double pointX = p1.getY()*p2.getZ() - p1.getZ()*p2.getY();
    double pointY = p1.getZ()*p2.getX() - p1.getX()*p2.getZ();
    double pointZ = p1.getX()*p2.getY() - p1.getY()*p2.getX();
    double sum = Math.sqrt(Math.pow(pointX, 2) + Math.pow(pointY, 2) + Math.pow(pointZ, 2));

    //convert to unit vector
    double newA = pointX/sum;
    double newB = pointY/sum;
    double newC = pointZ/sum;

    return new Point3DH(newA, newB, newC);
  }
}
