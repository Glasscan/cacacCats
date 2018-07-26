package windowing.drawable;

import windowing.graphics.Color;

public final class GhostWritingDrawable extends DrawableDecorator {
	private double coverage;

	public GhostWritingDrawable(Drawable delegate, double coverage) {
		super(delegate);
		this.coverage = coverage;
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		int argb = delegate.getPixel(x, y);
		Color currentColor = Color.fromARGB(argb);
		Color newColor = Color.WHITE;
		Color blendColor = newColor.blendInto(coverage, currentColor);
		delegate.setPixel(x,  y, z, blendColor.asARGB());
	}
}