package windowing.graphics;

import java.util.Random;

public class Color {
	public static final Color BLACK = new Color(0.0, 0.0, 0.0);
	public static final Color WHITE = new Color(1.0, 1.0, 1.0);
	public static final Color RED   = new Color(1.0, 0.0, 0.0);
	public static final Color GREEN = new Color(0.0, 1.0, 0.0);
	public static final Color BLUE  = new Color(0.0, 0.0, 1.0);
	
	private static final long SEED = 12341754L;
	private static final Random random = new Random(SEED);
	
	private static final double MIN_NORMAL_CHANNEL_VALUE = 0.0;
	private static final double MAX_NORMAL_CHANNEL_VALUE = 1.0;

	private static final int MIN_CHANNEL_INT_VALUE = 0x00;
	private static final int MAX_CHANNEL_INT_VALUE = 0xff;
	private static final int CHANNEL_MASK          = 0xff;
	
	private static final int ALPHA_SHIFT       = 24;
	private static final int RED_SHIFT         = 16;
	private static final int GREEN_SHIFT       = 8;
	private static final int BLUE_SHIFT        = 0;
	
	final double r;
	final double g;
	final double b;
	
	public Color(double r, double g, double b) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	
	////////////////////////////////////////////////////////////////
	//
	// getters (2 formats)
	//
	
	public double getR() {
		return r;
	}
	public double getG() {
		return g;
	}
	public double getB() {
		return b;
	}
	
	public int getIntR() {
		return integerChannel(r);
	}
	public int getIntG() {
		return integerChannel(g);
	}
	public int getIntB() {
		return integerChannel(b);
	}
	
	////////////////////////////////////////////////////////////////
	//
	// vector operations
	//
	public Color scale(double scaleFactor) {
		double r = scaleFactor * getR();
		double g = scaleFactor * getG();
		double b = scaleFactor * getB();
		return new Color(r, g, b);
	}
	public Color add(Color otherColor) {
		double r = getR() + otherColor.getR();
		double g = getG() + otherColor.getG();
		double b = getB() + otherColor.getB();
		return new Color(r, g, b);
	}	
	public Color subtract(Color otherColor) {
		double r = getR() - otherColor.getR();
		double g = getG() - otherColor.getG();
		double b = getB() - otherColor.getB();
		return new Color(r, g, b);
	}

	public Color multiply(Color otherColor) {
		double r = getR() * otherColor.getR();
		double g = getG() * otherColor.getG();
		double b = getB() * otherColor.getB();
		return new Color(r, g, b);
	}
	
	// could implement as: this.scale(thisAlpha).add(otherColor.scale(1-thisAlpha))
	public Color blendInto(double thisAlpha, Color otherColor) {
		double r = this.getR() * thisAlpha + otherColor.getR() * (1-thisAlpha);
		double g = this.getG() * thisAlpha + otherColor.getG() * (1-thisAlpha);
		double b = this.getB() * thisAlpha + otherColor.getB() * (1-thisAlpha);
		return new Color(r, g, b);
	}
	
	public Color clamp() {
		double r = clampChannel(getR());
		double g = clampChannel(getG());
		double b = clampChannel(getB());
		return new Color(r, g, b);
	}
	private double clampChannel(double value) {
		if(value >= MAX_NORMAL_CHANNEL_VALUE) {
			return MAX_NORMAL_CHANNEL_VALUE;
		}
		if(value <= MIN_NORMAL_CHANNEL_VALUE) {
			return MIN_NORMAL_CHANNEL_VALUE;
		}
		return value;
	}


	////////////////////////////////////////////////////////////////
	//
	// coordinate and format conversions
	//
	public int asARGB() {
		int alpha = MAX_CHANNEL_INT_VALUE << ALPHA_SHIFT;
		int intR  = integerChannel(r) << RED_SHIFT;
		int intG  = integerChannel(g) << GREEN_SHIFT;
		int intB  = integerChannel(b) << BLUE_SHIFT;
		
		return alpha | intR | intG | intB;
	}
	private static int integerChannel(double value) {
		if(value >= MAX_NORMAL_CHANNEL_VALUE) {
			return MAX_CHANNEL_INT_VALUE;
		}
		if(value <= MIN_NORMAL_CHANNEL_VALUE) {
			return MIN_CHANNEL_INT_VALUE;
		}
		double scale = ((double)(MAX_CHANNEL_INT_VALUE - MIN_CHANNEL_INT_VALUE)) / (MAX_NORMAL_CHANNEL_VALUE - MIN_NORMAL_CHANNEL_VALUE);
		double convertedValue = ((value - MIN_NORMAL_CHANNEL_VALUE) * scale) + MIN_CHANNEL_INT_VALUE; 
		return (int) (Math.round(convertedValue)) & CHANNEL_MASK;
	}
	public static Color fromARGB(int argb) {
		double r = extractDoubleChannel(argb, RED_SHIFT);
		double g = extractDoubleChannel(argb, GREEN_SHIFT);
		double b = extractDoubleChannel(argb, BLUE_SHIFT);
		return new Color(r, g, b);
	}
	public static double extractDoubleChannel(int argb, int channelShift) {
		int intChannel = (argb >> channelShift) & CHANNEL_MASK;
		return (double)intChannel / (double)MAX_CHANNEL_INT_VALUE;
	}


	public static int makeARGB(int r, int g, int b) {
		int alpha = MAX_CHANNEL_INT_VALUE << ALPHA_SHIFT;
		int intR  = (r & CHANNEL_MASK) << RED_SHIFT;
		int intG  = (g & CHANNEL_MASK) << GREEN_SHIFT;
		int intB  = (b & CHANNEL_MASK) << BLUE_SHIFT;
		
		return alpha | intR | intG | intB;
	}

	
	////////////////////////////////////////////////////////////////
	//
	// toString
	//
	
	public String toString() {
		return String.format("(%4.2f, %4.2f, %4.2f)", r, g, b);
	}
	public String toIntString() {
		String rString = "" + integerChannel(r);
		String gString = "" + integerChannel(g);
		String bString = "" + integerChannel(b);
		return "(" + rString + ", " + gString + ", " + bString + ")";
	}
	
	////////////////////////////////////////////////////////////////
	//
	// random color: repeatable (you supply the generator) and nonrepeatable
	//
	public static Color random(Random generator) {
		double r = generator.nextDouble();
		double g = generator.nextDouble();
		double b = generator.nextDouble();
		
		return new Color(r, g, b);		
	}
	public static Color random() {
		return random(random);
	}

}
