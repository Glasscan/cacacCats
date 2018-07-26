//mine that was a big copy/paste of the BresenhamLineRenderer
package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import client.Clipper;

//modified for color interpolation
public class DDALineRenderer implements LineRenderer {
  private DDALineRenderer() { }

  @Override
  public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) { //this is the algorithm itself
    double cszP1 = 1.0/p1.getCameraSpaceZ(); //for assn3 (ONLY for color)
    double cszP2 = 1.0/p2.getCameraSpaceZ();
    //similar format to the slope intercept lines
    double dx = p2.getIntX() - p1.getIntX();
    double dy = p2.getIntY() - p1.getIntY();
    double dz = cszP2 - cszP1; //for assn2
    double slope = dy/dx;
    double zValue = cszP1; //starting z value
    double zSlope = dz/dx;

    if(dx == 0.0){
      zSlope = dz/dy;
    }

    double redValue = p1.getColor().getR()*cszP1; //initial 1/red_P1 to 1/red_P2
    double redSlope =  (p2.getColor().getR()*cszP2 - p1.getColor().getR()*cszP1)/dx;
    double greenValue = p1.getColor().getG()*cszP1;
    double greenSlope =  (p2.getColor().getG()*cszP2 - p1.getColor().getG()*cszP1)/dx;
    double blueValue = p1.getColor().getB()*cszP1;
    double blueSlope =  (p2.getColor().getB()*cszP2 - p1.getColor().getB()*cszP1)/dx;

    Color color = new Color(redValue, greenValue, blueValue);

    double y = p1.getIntY();
    double csz = cszP1;

    double cszInverse = 1.0/csz; //just some arbitrary initial valu;
    for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
      cszInverse = 1.0/csz;
      color = new Color(redValue*cszInverse, greenValue*cszInverse, blueValue*cszInverse);
      drawable.setPixel(x, (int)Math.round(y), cszInverse, color.asARGB()); //y converted from double to long to int

      y = y + slope;
      redValue = redValue + redSlope;
      greenValue = greenValue + greenSlope;
      blueValue = blueValue + blueSlope;
      csz = csz + zSlope; //i think it's supposed to be negative
    }

  }

  public static LineRenderer make() { //for the make() call in Client.java
    return new AnyOctantLineRenderer(new DDALineRenderer()); //use given
  }
}
