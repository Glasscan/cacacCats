package client.testPages;

import geometry.Vertex3D;
import polygon.PolygonRenderer;
import polygon.Polygon;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class myTest {
	private final PolygonRenderer renderer;
	private final Drawable panel;
  private static final int MARGIN = 20; //20 pixels for the margins

  Vertex3D center;

	public myTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}

	private void render() {

    Vertex3D p1, p2, p3, p4, p5, p6; //p1 will remain static
    Color colorA = new Color(1, 0, 0);
    Color colorB = new Color(0, 1, 0);
    Color colorC = new Color(0, 0, 1);


    p1 = new Vertex3D(100, 100, 0, colorB);
    p2 = new Vertex3D(200, 100, 0, colorC);
    p3 = new Vertex3D(175, 150, 0, colorC);
    p4 = new Vertex3D(50, 200, 0, colorA);
    p5 = new Vertex3D(50, 60, 0, colorB);
    p6 = new Vertex3D(50, 110, 0, colorB);

    renderer.drawPolygon(Polygon.make(p1, p2, p4), panel, c-> Color.WHITE);
    renderer.drawPolygon(Polygon.make(p2, p4, p3), panel, c-> Color.WHITE);
}
    private void makeCenter() {
      int centerX = panel.getWidth() / 2;
      int centerY = panel.getHeight() / 2;
      center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }
	}
