package polygon;

import shader.Shader;
import shader.FaceShader;
import shader.VertexShader;
import shader.PixelShader;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	// assumes polygon is ccw.
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader);

	default public void drawPolygon(Polygon polygon, Drawable panel) {
		drawPolygon(polygon, panel,  c -> c);
	};
}
