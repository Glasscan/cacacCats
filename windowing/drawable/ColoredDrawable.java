package windowing.drawable;

public class ColoredDrawable extends DrawableDecorator {
  private final int argbColor;

  public ColoredDrawable (Drawable delegate, int argbColor){
    super(delegate); //construct from parent
    this.argbColor = argbColor;
  }
  
  @Override
  public void clear() {
    fill(argbColor, Double.MAX_VALUE);
  }

}
