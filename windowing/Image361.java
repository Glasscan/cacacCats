package windowing;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import windowing.drawable.Drawable;

public final class Image361 extends ImageView implements Drawable {
	public static final int IMAGE_X = 750;
	public static final int IMAGE_Y = 750;
	
	private WritableImage image;
	private PixelReader pixelReader;
	private PixelWriter pixelWriter;
	

	protected Image361() {
		image = new WritableImage(IMAGE_X, IMAGE_Y);
		setImage(image);
		
		pixelReader = image.getPixelReader();
		pixelWriter = image.getPixelWriter();
	}
	
	@Override
	public void setPixel(int x, int y, double z, int color) {
		pixelWriter.setArgb(x, y, color);
	}
	@Override
	public int getPixel(int x, int y) {
		return pixelReader.getArgb(x, y);
	}
	@Override
	public double getZValue(int x, int y) {
		return 0.0;
	}

	@Override
	public int getWidth() {
		return IMAGE_X;
	}
	@Override
	public int getHeight() {
		return IMAGE_Y;
	}

}
