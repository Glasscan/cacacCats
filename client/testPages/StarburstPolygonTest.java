package client.testPages;

import geometry.Vertex3D;
import java.util.Random;
import polygon.PolygonRenderer;
import polygon.Polygon;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class StarburstPolygonTest {
	private final PolygonRenderer renderer;
	private final Drawable panel;

  private static final int NUM_RAYS = 90;
  private static final double FRACTION_OF_PANEL_FOR_DRAWING = 0.9;
	private static final Random ColorRNG = new Random(12341754L);

	Vertex3D center;

	public StarburstPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}
/*
Vertex3D p1 = new Vertex3D(150, 150, 0, Color.random(ColorRNG)); //p1 will remain static
Vertex3D p2 = new Vertex3D(284, 160, 0, Color.random(ColorRNG));
Vertex3D p3 = new Vertex3D(280, 140, 0, Color.random(ColorRNG));
*/
//draw the thing
	private void render() {
		double radius = computeRadius();
		double angleDifference = (2.0 * Math.PI) / NUM_RAYS; //shift
		double angle = 0.0; //starting angle

		Vertex3D p1 = new Vertex3D(panel.getWidth()/2, panel.getHeight()/2, 0, Color.WHITE); //p1 will remain static
		Vertex3D p2;
		Vertex3D p3;
		Polygon bigP;

		ColorRNG.setSeed(12341754L);
		for(int ray = 0; ray < NUM_RAYS; ray++) {
			p1 = p1.replaceColor(Color.random(ColorRNG));
			p2 = radialPoint(radius, angle); //radial point
			p3 = radialPoint(radius, angle + angleDifference); // 2nd radial point
			bigP = Polygon.make(p1, p2, p3);

			renderer.drawPolygon(bigP, panel, c -> c);
			angle = angle + angleDifference;
		//	if(ray == 22) break;
		}
  }

    private void makeCenter() {
      int centerX = panel.getWidth() / 2;
      int centerY = panel.getHeight() / 2;
      center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }

    private Vertex3D radialPoint(double radius, double angle) {
      double x = center.getX() + radius * Math.cos(angle);
      double y = center.getY() + radius * Math.sin(angle);
      return new Vertex3D(x, y, 0, Color.random(ColorRNG));
    }

    private double computeRadius() {
      int width = panel.getWidth();
      int height = panel.getHeight();

      int minDimension = width < height ? width : height;

      return (minDimension / 2.0) * FRACTION_OF_PANEL_FOR_DRAWING;
    }
	}
