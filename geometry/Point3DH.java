package geometry;

public class Point3DH implements Point {
	private double x;
	private double y;
	private double z;
	private double w;
	
	public Point3DH(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Point3DH(double x, double y, double z) {
		this(x, y, z, 1.0);
	}
	public Point3DH(double[] coords) {
		this(coords[0], coords[1], coords[2], coords[3]);
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public double getW() {
		return w;
	}
	public int getIntX() {
		return (int) Math.round(x);
	}
	public int getIntY() {
		return (int) Math.round(y);
	}
	public int getIntZ() {
		return (int) Math.round(z);
	}
	public Point3DH round() {
		double newX = Math.round(x);
		double newY = Math.round(y);
		double newZ = Math.round(z);
		return new Point3DH(newX, newY, newZ);
	}
	public Point3DH add(Point point) {
		Point3DH other = (Point3DH)point;
		double newX = x + other.getX();
		double newY = y + other.getY();
		double newZ = z + other.getZ();
		return new Point3DH(newX, newY, newZ);
	}
	public Point3DH subtract(Point point) {
		Point3DH other = (Point3DH)point;
		double newX = x - other.getX();
		double newY = y - other.getY();
		double newZ = z - other.getZ();
		return new Point3DH(newX, newY, newZ);
	}
	public Point3DH scale(double scalar) {
		double newX = x * scalar;
		double newY = y * scalar;
		double newZ = z * scalar;
		return new Point3DH(newX, newY, newZ);
	}
	public String toString() {
		return "[" + x + " " + y + " " + z + " " + w + "]t";
	}
	public Point3DH euclidean() {
		if(w == 0) {
			w = .000000001;
			throw new UnsupportedOperationException("attempt to get euclidean equivalent of point at infinity " + this);
		}
		double newX = x / w;
		double newY = y / w;
		double newZ = z / w;
		return new Point3DH(newX, newY, newZ);
	}
}
