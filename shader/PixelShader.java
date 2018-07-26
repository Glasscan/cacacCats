package shader;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

public interface PixelShader {

	Color shade(Polygon polygon, Vertex3D current);

}
