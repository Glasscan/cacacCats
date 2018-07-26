package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

public interface LineRenderer {
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable panel);
}
