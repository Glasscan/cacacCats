package shader;

import geometry.Vertex3D;
import polygon.Polygon;

@FunctionalInterface
public interface VertexShader {
	public Vertex3D shade(Polygon polygon, Vertex3D vertex);

}
