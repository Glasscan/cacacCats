package client.testPages;

import geometry.Vertex3D;
import java.util.Random;
import polygon.PolygonRenderer;
import polygon.Polygon;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class RandomPolygonTest {
	private final PolygonRenderer renderer;
	private final Drawable panel;
	private static final Random ColorRNG = new Random(12341754L);

	Vertex3D center;

	public RandomPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}

	private void render() {

    Vertex3D p1; //p1 will remain static
    Vertex3D p2;
    Vertex3D p3;
    Color colorA;
    Random RNG = new Random(12345);
    for(int i = 0; i < 30; i++){
      p1 = new Vertex3D(RNG.nextInt(299), RNG.nextInt(299), 0, Color.random(ColorRNG));
      p2 = new Vertex3D(RNG.nextInt(299), RNG.nextInt(299), 0, Color.random(ColorRNG));
      p3 = new Vertex3D(RNG.nextInt(299), RNG.nextInt(299), 0, Color.random(ColorRNG));

      renderer.drawPolygon(Polygon.make(p1, p2, p3), panel, c-> c);

    }

  }
    private void makeCenter() {
      int centerX = panel.getWidth() / 2;
      int centerY = panel.getHeight() / 2;
      center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }

}
