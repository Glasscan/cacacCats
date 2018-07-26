package geometry;

public class Point2D implements Point {
	private double x;
	private double y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getIntX() {
		return (int) Math.round(x);
	}
	public int getIntY() {
		return (int) Math.round(y);
	}
	
	public Point2D round() {
		double newX = Math.round(x);
		double newY = Math.round(y);
		return new Point2D(newX, newY);
	}
	public Point2D add(Point point) {
		double newX = x + point.getX();
		double newY = y + point.getY();
		return new Point2D(newX, newY);
	}
	public Point2D subtract(Point point) {
		double newX = x - point.getX();
		double newY = y - point.getY();
		return new Point2D(newX, newY);
	}
	public Point2D scale(double scalar) {
		double newX = x * scalar;
		double newY = y * scalar;
		return new Point2D(newX, newY);
	}
}
