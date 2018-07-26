package windowing.drawable;

import geometry.Point2D;
import windowing.graphics.Dimensions;

public class TranslatingDrawable extends DrawableDecorator {
	private final Point2D origin;
	private final Dimensions size;

	public TranslatingDrawable(Drawable delegate, Point2D origin, Dimensions size) {
		super(delegate);
		this.origin = origin;
		this.size = size;
	}
	
	public int transformedX(int x) {
		return x + origin.getIntX();
	}	
	public int transformedY(int y) {
		return y + origin.getIntY();
	}
	
	
	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		delegate.setPixel(transformedX(x), transformedY(y), z, argbColor);
	}
	@Override
	public int getPixel(int x, int y) {
		return delegate.getPixel(transformedX(x), transformedY(y));
	}
	@Override
	public double getZValue(int x, int y) {
		return delegate.getZValue(transformedX(x), transformedY(y));
	}
	@Override
	public int getWidth() {
		return size.getWidth();
	}
	@Override
	public int getHeight() {
		return size.getHeight();
	}
	@Override
	public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
		delegate.setPixelWithCoverage(transformedX(x), transformedY(y), z, argbColor, coverage);
	}
}
