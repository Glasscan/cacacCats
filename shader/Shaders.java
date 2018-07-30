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

    if(p1.hasNormal() && p2.hasNormal() && p3.hasNormal()){
       normal = averageNormal(p1.getNormal(), p2.getNormal(), p3.getNormal());
    }
    else{
      Point3DH vLeft = calculateVectorDifference(p1, p2);
      Point3DH vRight = calculateVectorDifference(p1, p3);

      normal = crossProduct(vLeft, vRight);
      //System.out.println(p1 + " \n" + p2 + " \n" + p3);

    }
    Color newColor = Lighting.light(new Vertex3D(center, p1.getColor()), p1.getColor(), normal);
    polygon.setShadeColor(newColor);
    return polygon;
  }


  public static Vertex3D NullVertexShader(Polygon polygon, Vertex3D point){
    Point3DH normal;

    Vertex3D p1 = polygon.get(0).useCameraSpace(); //magic
    Vertex3D p2 = polygon.get(1).useCameraSpace();
    Vertex3D p3 = polygon.get(2).useCameraSpace();

    if(p1.hasNormal() && p2.hasNormal() && p3.hasNormal()){ //if p1 has a normal then they should all have normals
       normal = averageNormal(p1.getNormal(), p2.getNormal(), p3.getNormal());
    }
    else{
      Point3DH vLeft = calculateVectorDifference(p1, p2);
      Point3DH vRight = calculateVectorDifference(p1, p3);

      normal = crossProduct(vLeft, vRight);
      //System.out.println(p1 + " \n" + p2 + " \n" + p3);

    }
    point.setNormal(normal);
    return point;
  }

  public static Vertex3D GouraudVertexShader(Polygon polygon, Vertex3D point){
    Point3DH normal;

    Vertex3D p1 = polygon.get(0).useCameraSpace(); //magic
    Vertex3D p2 = polygon.get(1).useCameraSpace();
    Vertex3D p3 = polygon.get(2).useCameraSpace();

    if(point.hasNormal()){
       normal = point.getNormal();
    }
    else{
      Point3DH vLeft = calculateVectorDifference(p1, p2);
      Point3DH vRight = calculateVectorDifference(p1, p3);

      normal = crossProduct(vLeft, vRight);
    }
    Vertex3D tempVertex = point.useCameraSpace();

    Color newColor = Lighting.light(tempVertex, tempVertex.getColor(), normal);
    point = point.replaceColor(newColor);
    point.setNormal(normal);
    point.setCameraPoint(tempVertex);
    return point;
  }

  public static Vertex3D PhongVertexShader(Polygon polygon, Vertex3D point){
    Point3DH normal;

    Vertex3D p1 = polygon.get(0).useCameraSpace(); //magic
    Vertex3D p2 = polygon.get(1).useCameraSpace();
    Vertex3D p3 = polygon.get(2).useCameraSpace();

    if(point.hasNormal()){
       normal = point.getNormal();
    }
    else{

      Point3DH vLeft = calculateVectorDifference(p1, p2);
      Point3DH vRight = calculateVectorDifference(p1, p3);

      normal = crossProduct(vLeft, vRight);
    }
    point.setNormal(normal);
    return point;
  }


  public static Color NullPixelShader(Polygon polygon, Vertex3D point){
    return point.getColor();
  }
  public static Color FlatPixelShader(Polygon polygon, Vertex3D point){
    return polygon.getShadeColor();
  }
  public static Color GouraudPixelShader(Polygon polygon, Vertex3D point){ //don't need to interpolate twice
    return point.getColor();
  }
  public static Color PhongPixelShader(Polygon polygon, Vertex3D point){
    Vertex3D tempVertex = point.useCameraSpace();
    Color newColor = Lighting.light(tempVertex, point.getColor(), point.getNormal());
    return newColor;
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

  private static Point3DH averageNormal(Point3DH p1, Point3DH p2, Point3DH p3){
    double x = (p1.getX() + p2.getX() + p3.getX())/3.0;
    double y = (p1.getY() + p2.getY() + p3.getY())/3.0;
    double z = (p1.getZ() + p2.getZ() + p3.getZ())/3.0;
    return new Point3DH(x, y, z);
  }
}
