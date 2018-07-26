package windowing.drawable;

import windowing.graphics.Color;
import client.Clipper;

public class DepthCueingDrawable extends DrawableDecorator {
  private int argbColor;
  private final double front;
  private final double back; //both planes
  private final int width;
  private final int height;
  private boolean useDepth;
  //array
  private double[][] ZBuffer;
  private double near; //not the same as the front
  private double far; //not the same as the back
  private Color farColor;

  public DepthCueingDrawable (Drawable delegate, double front, double back, Color argbColor){
    super(delegate); //construct from parent
    this.argbColor = argbColor.asARGB(); //color at the front
    this.front = front;
    this.back = back;
    this.width = delegate.getWidth();
    this.height = delegate.getHeight();

    this.near = back; //because he said so

    this.ZBuffer = new double[width+1][height+1];
    initializeBuffer();
  }
  //override
  public DepthCueingDrawable (Drawable delegate, double front, double back,
    Color argbColor, double near, double far, Color farColor){

    super(delegate); //construct from parent
    this.argbColor = argbColor.asARGB(); //color at the front
    this.front = front;
    this.back = back;
    this.width = delegate.getWidth();
    this.height = delegate.getHeight();

    this.near = near;
    this.far = far;
    this.farColor = farColor;
    this.useDepth = true;

    this.ZBuffer = new double[width+1][height+1];
    initializeBuffer();
  }

  private void initializeBuffer(){
    int i;
    int j;
    for(i = 0; i <= width; i++){
      for(j = 0; j <= height; j++){
        ZBuffer[i][j] = back;
      }
    }
  }

  @Override
	public void setPixel(int x, int y, double z, int argbColor) {
    if(useDepth){
      Color newColor = Color.fromARGB(argbColor);
      //check the color
      if(z >= near){
        //do nothing
      }
      else if(z <= far){
        newColor = farColor;
      }
      else{
        newColor = lerpColor(Color.fromARGB(argbColor), z);
      }

      if(!Clipper.pixelClip(x, y, z)){
        if(z > ZBuffer[x][y]) {
           delegate.setPixel(x, y, z, newColor.asARGB());
           ZBuffer[x][y] = z;
         }
      } //use a clipper
      else{
        return;
      }
    }
    else{
      Color newColor = Color.fromARGB(this.argbColor).scale((-back + z)/(-back)); //darker at the back (ignore this)
      if(!Clipper.pixelClip(x, y, z)){
        if(z > ZBuffer[x][y]) {
           delegate.setPixel(x, y, z, argbColor);
           ZBuffer[x][y] = z;
         }
      } //use a clipper
      else{
        return;
      }
    }
	}
  public Color lerpColor(Color oldColor, double zValue){
    double oldRed = oldColor.getR();
    double oldGreen = oldColor.getG();
    double oldBlue = oldColor.getB();
    double dz = near - far; //100% value at near and 0% at far
    double dzNear = near - zValue; //how close it is to the near value

    double redSlope = (farColor.getR() - oldRed)/dz;
    double greenSlope = (farColor.getG() - oldGreen)/dz;
    double blueSlope = (farColor.getB() - oldBlue)/dz;

    double newRed = oldRed + (dzNear)*redSlope;
    double newGreen = oldGreen + (dzNear)*greenSlope;
    double newBlue = oldBlue + (dzNear)*blueSlope;

    Color newColor = new Color(newRed, newGreen, newBlue);

    return newColor;
  }

  @Override
  public void clear() {
    fill(argbColor, Double.MAX_VALUE);
  }

}
