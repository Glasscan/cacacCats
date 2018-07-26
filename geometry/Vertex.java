package geometry;

import windowing.graphics.Color;

public interface Vertex {
	public Vertex rounded();
	public Vertex add(Vertex other);
	public Vertex subtract(Vertex other);
	public Vertex scale(double scalar);
	public String toIntString();
	public int getIntX();
	public int getIntY();
	public double getX();
	public double getY();
	public Color getColor();
	public Point getPoint();
}
