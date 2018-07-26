package geometry;

public class Rectangle {
	double xLow;
	double yLow;
	double xHigh;
	double yHigh;
	
	public Rectangle(double xLow, double yLow, double xHigh, double yHigh) {
		super();
		this.xLow = xLow;
		this.yLow = yLow;
		this.xHigh = xHigh;
		this.yHigh = yHigh;
	}

	public double getXLow() {
		return xLow;
	}

	public double getYLow() {
		return yLow;
	}

	public double getXHigh() {
		return xHigh;
	}

	public double getYHigh() {
		return yHigh;
	}
	
	public double width() {
		return xHigh-xLow;
	}
	
	public double height() {
		return yHigh-yLow;
	}
}
