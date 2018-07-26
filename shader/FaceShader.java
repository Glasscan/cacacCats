package shader;

import polygon.Polygon;

@FunctionalInterface
public interface FaceShader {
	public Polygon shade(Polygon polygon);
}
