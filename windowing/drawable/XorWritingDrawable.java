package windowing.drawable;

public final class XorWritingDrawable extends DrawableDecorator {
	XorWritingDrawable(Drawable delegate) {
		super(delegate);
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		int currentColor = delegate.getPixel(x, y);
		int xorColor = (currentColor ^ argbColor) | 0xff000000;
		delegate.setPixel(x, y, z, xorColor);
	}
}