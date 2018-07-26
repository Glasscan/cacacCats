package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class ParallelogramTest {
	private final LineRenderer renderer;
	private final Drawable panel;
	Vertex3D center;

  Vertex3D p1;
  Vertex3D p2;

	public ParallelogramTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}

	private void render() {
    int p;
    for(p = 0; p < 50; p++){ //first shape
      p1 = new Vertex3D(20, invert(80 + p), 0, Color.WHITE);
      p2 = new Vertex3D(150, invert(150 + p), 0, Color.WHITE);
			renderer.drawLine(p1, p2, panel);
    }
    for(p = 0; p < 50; p++){ //second shape
      p1 = new Vertex3D(160 + p, invert(270), 0, Color.WHITE);
      p2 = new Vertex3D(240 + p, invert(40), 0, Color.WHITE);
      renderer.drawLine(p1, p2, panel);
    }

	}

  private int invert(int y){
    return panel.getHeight()- 1 - y;
  }
  private void makeCenter() {
    int centerX = panel.getWidth() / 2;
    int centerY = panel.getHeight() / 2;
    center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
  }
}
