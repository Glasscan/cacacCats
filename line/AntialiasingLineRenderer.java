//mine that was a big copy/paste of the BresenhamLineRenderer
package line;

//don't know what i'll actually need
import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class AntialiasingLineRenderer implements LineRenderer {
  public static final double radius = Math.sqrt(2);
  public static final double circleArea = Math.PI * Math.pow(radius, 2);

  private AntialiasingLineRenderer() { }

  @Override
  public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) { //this is the algorithm itself
    //perform DDA to find pixels to use
    //the following will be reused extensively

    double p1_x = p1.getIntX();
    double p1_y = p1.getIntY();
    double p2_x = p2.getIntX();
    double p2_y = p2.getIntY();

    double dx = p2_x - p1_x;
    double dy = p2_y - p1_y;
    double slope = dy/dx;
    double b = p1_y - slope * p1_x; //y intercept
    int argbColor = p1.getColor().asARGB();

    double y = p1_y;

    // the equation for the line formed by p1 and p2 is:
    // y = slope * x + b;
    //using a pixel radius of 0.5
    for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
      double pixelY = y;
      double pixelX = x; //for consistency
      double pixelSlope = (-1)*(1/slope);

      //compare overlaps of pixels above and below the these "centers"
      double area;
      double areaAbove;
      double areaBelow;
      //draw the thing
      if(slope == 0) area = flatOverlap(false);
      else area = Overlap(pixelX, Math.round(pixelY), pixelSlope, b, slope);
      Color midColor = Color.fromARGB(drawable.getPixel(x, (int)Math.round(pixelY)));
      midColor = midColor.add(p1.getColor().scale(area));
      drawable.setPixel(x, (int)Math.round(pixelY), 0.0, Color.fromARGB(argbColor).scale(area).asARGB());

      if(slope == 0) areaAbove = flatOverlap(true);
      else areaAbove = Overlap(pixelX, Math.round(pixelY) + 1, pixelSlope, b, slope);
      Color aboveColor = Color.fromARGB(drawable.getPixel(x, (int)Math.round(pixelY) + 1)); //below
      aboveColor = aboveColor.add(p1.getColor().scale(areaAbove));
      drawable.setPixel(x, (int)Math.round(pixelY) + 1, 0.0, aboveColor.asARGB());

      if(slope == 0) areaBelow = flatOverlap(true);
      else areaBelow = Overlap(pixelX, Math.round(pixelY) - 1, pixelSlope, b, slope);
      Color belowColor = Color.fromARGB(drawable.getPixel(x, (int)Math.round(pixelY) - 1)); //below
      belowColor = belowColor.add(p1.getColor().scale(areaBelow));
      drawable.setPixel(x, (int)Math.round(pixelY) - 1, 0.0, belowColor.asARGB());

      y = y + slope; //will increment for the next iteration
    }
  }

private static double Overlap(double pixelX, double pixelY,
    double pixelSlope, double b, double slope){

      double pixelB = pixelY - pixelSlope*pixelX;
      double interceptX = (b - pixelB)/(pixelSlope - slope); //x value of where d intercepts p1-p2
      double interceptY = interceptX * slope + b; //respective y value
      double d = Math.sqrt(
      Math.pow((pixelX - interceptX), 2) + Math.pow((pixelY - interceptY), 2)) - 0.5;//Pythagorus and subtraction

      if(d > radius) return 0;
      else if(d > 0){
        double theta = Math.acos(d/radius); // value in radians
        double wedge = d * Math.sqrt(Math.pow(radius, 2) - Math.pow(d, 2)); //area of (both) triangles
        double pacman = (1 - theta/Math.PI) *circleArea; //area of the rest of the circle

        double area = 1 - (wedge + pacman)/circleArea; //area of overlap
        return area;
      }

      else{
        d = (-1)*d; // flip the sign
        double theta = Math.acos(d/radius);
        double wedge = d * Math.sqrt(Math.pow(radius, 2) - Math.pow(d, 2));
        double pacman = (1 - theta/Math.PI) *circleArea;

        double area = (wedge + pacman)/circleArea;
        return area;
      }
  }
  private static double flatOverlap(boolean AA){
    double d;
    if (AA) d = 0.5;
    else  d = -0.5;

    if(d > 0){
      double theta = Math.acos(d/radius); // value in radians
      double wedge = d * Math.sqrt(Math.pow(radius, 2) - Math.pow(d, 2)); //area of (both) triangles
      double pacman = (1 - theta/Math.PI) *circleArea; //area of the rest of the circle

      double area = 1 - (wedge + pacman)/circleArea; //area of overlap
      return area;
    }
    else{
      d = (-1)*d; // flip the sign
      double theta = Math.acos(d/radius);
      double wedge = d * Math.sqrt(Math.pow(radius, 2) - Math.pow(d, 2));
      double pacman = (1 - theta/Math.PI) *circleArea;

      double area = (wedge + pacman)/circleArea;
      return area;
    }
  }

  public static LineRenderer make() { //for the make() call in Client.java
    return new AnyOctantLineRenderer(new AntialiasingLineRenderer()); //use given
  }
}
