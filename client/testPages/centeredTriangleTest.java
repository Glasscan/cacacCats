package client.testPages;

import geometry.Vertex3D;
import java.util.Random;
import polygon.PolygonRenderer;
import polygon.Polygon;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class centeredTriangleTest {
	private final PolygonRenderer renderer;
	private final Drawable panel;
	private static final Random RNG = new Random(5234);
	private final double nextAngle = (1.0/3.0)*(2*Math.PI); //120 degrees

	Vertex3D center;

	public centeredTriangleTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}

	private void render() {
		int centerX = panel.getWidth() / 2;
	 	int centerY = panel.getHeight() / 2;

    Vertex3D p1; //p1 will remain static
    Vertex3D p2;
    Vertex3D p3;
    Color color;

		for(int i = 0; i < 6; i++){
			color = new Color(1.0 - i*0.15, 1.0 - i*0.15, 1.0 - i*0.15);
			double randomAngle = RNG.nextDouble()*(2*Math.PI); // between 0 and 360
			int z = RNG.nextInt(200);
		//	System.out.println(-z);
			p1 = new Vertex3D(centerX + 275*(Math.sin(randomAngle)), centerY + 275*(Math.cos(randomAngle)), -z, color); //start
			p2 = new Vertex3D(centerX + 275*(Math.sin(randomAngle + nextAngle)), centerY + 275*(Math.cos(randomAngle + nextAngle)), -z, color); //to the right of start...
			p3 = new Vertex3D(centerX + 275*(Math.sin(randomAngle + 2*nextAngle)), centerY + 275*(Math.cos(randomAngle + 2*nextAngle)), -z, color); //and below the start...

			renderer.drawPolygon(Polygon.make(p1, p2, p3), panel, c-> Color.WHITE);
	}
}

    private void makeCenter() {
      int centerX = panel.getWidth() / 2;
      int centerY = panel.getHeight() / 2;
      center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }

	}
