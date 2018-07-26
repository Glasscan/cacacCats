package windowing.drawable;

// This class just sends every message to its delegate.
// It is intended for subclassing; subclasses simply override
// whatever behaviour they wish to modify.

public class DrawableDecorator implements Drawable {
	protected final Drawable delegate;

	public DrawableDecorator(Drawable delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		delegate.setPixel(x,  y,  z, argbColor);
	}
	@Override
	public int getPixel(int x, int y) {
		return delegate.getPixel(x,  y);
	}
	@Override
	public double getZValue(int x, int y) {
		return delegate.getZValue(x, y);
	};
	
	@Override
	public int getWidth() {
		return delegate.getWidth();
	}
	@Override
	public int getHeight() {
		return delegate.getHeight();
	}
}