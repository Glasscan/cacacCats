package windowing.drawable;

public class InvertedYDrawable extends DrawableDecorator {
	public InvertedYDrawable(Drawable delegate) {
		super(delegate);
	}
	
	// note that below, delegate is a protected field of DelegatingDrawable
	public int invert(int y) {
		return delegate.getHeight() - 1 - y;
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		delegate.setPixel(x, invert(y), z, argbColor);
	}
	@Override
	public int getPixel(int x, int y) {
		return delegate.getPixel(x, invert(y));
	}
	@Override
	public double getZValue(int x, int y) {
		return delegate.getZValue(x, invert(y));
	}
	@Override
	public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
		delegate.setPixelWithCoverage(x, invert(y), z, argbColor, coverage);
	}	
}