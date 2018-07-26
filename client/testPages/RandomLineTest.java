package client.testPages;

import java.util.Random;
import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class RandomLineTest {
	private final LineRenderer renderer;
	private final Drawable panel;
  private static final Random RNG = new Random(12345);
  private static final Random ColorRNG = new Random(12341754L);

	Vertex3D center;

  Vertex3D p1;
  Vertex3D p2;

	public RandomLineTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}

	private void render() {
    int p;
    RNG.setSeed(12345); //reset the seed to get same values
    ColorRNG.setSeed(12341754L);
    Color lineColor = Color.random(ColorRNG);

    for(p = 0; p < 30; p++){ //first shape
      lineColor = Color.random(ColorRNG);
      p1 = new Vertex3D(RNG.nextInt(299), RNG.nextInt(299), 0, lineColor);
      p2 = new Vertex3D(RNG.nextInt(299), RNG.nextInt(299), 0, lineColor);
			renderer.drawLine(p1, p2, panel);
    }
	}

  private void makeCenter() {
    int centerX = panel.getWidth() / 2;
    int centerY = panel.getHeight() / 2;
    center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
  }
}
