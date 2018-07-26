//mine
package line;

//don't know what i'll actually need
import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class BresenhamLineRenderer implements LineRenderer {
  private BresenhamLineRenderer() { }

  @Override
  public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) { //this is the algorithm itself

    //use a similar format for the slope intercept lines
    double dx = p2.getIntX() - p1.getIntX();
    double dy = p2.getIntY() - p1.getIntY();
    double slope = 2*dy;
    double q = slope - 2*dx;
    int argbColor = p1.getColor().asARGB();

    drawable.setPixel(p1.getIntX(), p1.getIntY(), 0.0, argbColor);

    int y = p1.getIntY();
    double err = slope - dx;
    for(int x = p1.getIntX() + 1; x <= p2.getIntX(); x++) {
      if(err >= 0){
        err += q;
        y++;
      }
      else{
        err += slope;
      }
      drawable.setPixel(x, y, 0.0, argbColor);
    }
  }

  public static LineRenderer make() { //for the make() call in Client.java
    return new AnyOctantLineRenderer(new BresenhamLineRenderer()); //use the given
  }
}
