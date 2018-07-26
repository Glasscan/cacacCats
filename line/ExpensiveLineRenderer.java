package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class ExpensiveLineRenderer implements LineRenderer {
	// use the static factory make() instead of constructor.
	private ExpensiveLineRenderer() {}


	/*
	 * (non-Javadoc)
	 * @see client.LineRenderer#drawLine(client.Vertex2D, client.Vertex2D, windowing.Drawable)
	 *
	 * @pre: p2.x >= p1.x && p2.y >= p1.y
	 */
	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();

		double slope = deltaY / deltaX;
		double intercept = p2.getIntY() - slope * p2.getIntX();
		int argbColor = p1.getColor().asARGB();

		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			int y = (int)(slope * x + intercept);
			drawable.setPixel(x, y, 0.0, argbColor);
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new ExpensiveLineRenderer());
	}
}
