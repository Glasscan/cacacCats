package polygon;

import geometry.Vertex3D;
import line.LineRenderer;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

// assumes polygon is ccw.
public class WireframePolygonRenderer implements PolygonRenderer{
	private WireframePolygonRenderer(){}
	private LineRenderer renderer;

	public void setRenderer(LineRenderer renderer){
		this.renderer = renderer;
	}

	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		Vertex3D p1 = polygon.get(0);
		Vertex3D p2 = polygon.get(1);
		Vertex3D p3 = polygon.get(2);

		/* no ambience for the wireFrameRenderer
		p1 = p1.replaceColor(vertexShader.shade(p1.getColor())); //will be black if there is no ambient light
		p2 = p2.replaceColor(vertexShader.shade(p2.getColor()));
		p3 = p3.replaceColor(vertexShader.shade(p3.getColor()));
		*/
	//	System.out.println(p1 + " " + p2 + " " + p3);
	//the AnyOctantLineDrawer creates some wierd behaviour
		renderer.drawLine(p1, p2, drawable);
		renderer.drawLine(p1, p3, drawable);
		renderer.drawLine(p2, p3, drawable);
	}

	public static WireframePolygonRenderer make(){
		return new WireframePolygonRenderer();
	}
}
