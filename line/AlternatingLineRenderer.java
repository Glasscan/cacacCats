package line;

import geometry.Vertex3D;
//import notProvided.line.BresenhamLineRenderer;
//import notProvided.line.DDALineRenderer;
import windowing.drawable.Drawable;

// Because we have the interface LineRenderer, this class is
// very simple.

public class AlternatingLineRenderer implements LineRenderer {
	// use the static factory make() instead of constructor.
	private AlternatingLineRenderer() { }

	private LineRenderer secondRenderer = DDALineRenderer.make();
	private LineRenderer firstRenderer = BresenhamLineRenderer.make();

	private int parity = 0;

	/*
	 * (non-Javadoc)
	 * @see client.LineRenderer#drawLine(client.Vertex2D, client.Vertex2D, windowing.Drawable)
	 *
	 * @pre: p2.x >= p1.x && p2.y >= p1.y
	 */
	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		if(parity == 0) {
			parity = 1;
			firstRenderer.drawLine(p1, p2, drawable);
		}
		else {
			parity = 0;
			secondRenderer.drawLine(p1, p2, drawable);
		}
	}

	public static LineRenderer make() {
		return new AlternatingLineRenderer();
	}

}
