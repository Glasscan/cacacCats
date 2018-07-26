package windowing.drawable;

import windowing.graphics.Color;
import windowing.graphics.Dimensions;

// A Drawable is a rectangular area that you can draw on.
// Standard drawables go from 0 to width - 1 (inclusive) in X,
// and from 0 to height - 1 in Y.  Nonstandard drawables may
// have different X- and Y- ranges (but should have the given
// width and height).
//
// Drawables *do not*, by default, clip pixels outside of their rectangle.
// Of course, one may write a PixelClippingDrawable which does exactly that.

public interface Drawable {
	public void setPixel(int x, int y, double z, int argbColor);
	public int getPixel(int x, int y);
	public double getZValue(int x, int y);
	
	public int getWidth();
	public int getHeight();
	
	
	// Precise but inefficient -- do in [0, 255] space if you can tolerate some small errors instead.
	// Note that this does not give access to hardware alpha channel.
	
	default public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
		int oldArgb = getPixel(x,  y);
		Color oldColor = Color.fromARGB(oldArgb);
		Color thisColor = Color.fromARGB(argbColor);
		
		Color blend = thisColor.blendInto(coverage, oldColor);
		setPixel(x,  y,  z, blend.asARGB());
	}

	
	// The following methods are convenience methods that should rarely
	// if ever be overridden.

	default public Dimensions getDimensions() {
		return new Dimensions(getWidth(), getHeight());
	}
	default public boolean contains(int x, int y) {  
		return (0 <= x && x < getWidth()) && (0 <= y && y < getHeight());
	}

	public static int ARGB_BLACK = 0xFF_00_00_00;
	default public void clear() {
		fill(ARGB_BLACK, Double.MAX_VALUE);
	}
	default public void fill(int argbColor, double z) {
		for(int x = 0; x < getWidth(); x++) {
			for(int y = 0; y < getHeight(); y++) {
				setPixel(x, y, z, argbColor);
			}
		}
	}
}
