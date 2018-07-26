package client.testPages;

import geometry.Vertex3D;
import java.util.Random;
import polygon.PolygonRenderer;
import polygon.Polygon;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class MeshPolygonTest {
	private final PolygonRenderer renderer;
	private final Drawable panel;
  private final Boolean perturbation;
  private static final int MARGIN = 20; //20 pixels for the margins
	private static final Random ColorRNG = new Random(12341754L);

  public static final boolean NO_PERTURBATION = false;
  public static final boolean USE_PERTURBATION = true;

	Vertex3D center;

	public MeshPolygonTest(Drawable panel, PolygonRenderer renderer, boolean perturbation) {
		this.panel = panel;
		this.renderer = renderer;
    this.perturbation = perturbation;

		makeCenter();
		render();
	}

	private void render() {

    Vertex3D p1; //p1 will remain static
    Vertex3D p2;
    Vertex3D p3;
    Color colorA, colorB;

    int leftEdge = MARGIN;
    int rightEdge = panel.getWidth() - MARGIN;
    Point points = new Point();

    for(int j = 0; j < 10; j++){ //initialize the grid
      points.x[j] = (j)*(32) + 20;
      points.y[j] = (j)*(32) + 20;
    }

    ColorRNG.setSeed(12341754L);

//modify colors for ASSN2
    if(perturbation){
      Random RNG = new Random(12345);
			int index = 0;
			while(index < 100){
	      for(int j = 0; j < 10; j++, index++){ //mess with the points
	        points.index[index][0] = (int)points.x[j] + (RNG.nextInt(24) - 12);
	        points.index[index][1] = (int)points.y[index/10] + (RNG.nextInt(24) - 12); //adjust rows every 10 iterations
	        //System.out.println(index + " x: " + points.index[index][0] + " y: " + points.index[index][1]);
					points.index[index][0] = 2*points.index[index][0];
					points.index[index][1] = 2*points.index[index][1];
					points.color[index] = Color.random(ColorRNG);
	      }
			}
      for(int i = 0; i < 9; i++){ //i determines row or y value
        for(int j = 0; j < 9; j++){

          p1 = new Vertex3D(points.index[i*10 + j][0], points.index[i*10 + j][1], 0, points.color[i*10 + j]); //start
					p2 = new Vertex3D(points.index[i*10 + j + 1][0], points.index[i*10 + j + 1][1], 0, points.color[i*10 + j + 1]); //to the right of start...
					p3 = new Vertex3D(points.index[(i+1)*10 + j][0], points.index[(i + 1)*10 + j][1], 0, points.color[(i+1)*10 + j]); //and below the start...

					renderer.drawPolygon(Polygon.make(p1, p2, p3), panel, c-> Color.WHITE);
					p1 = new Vertex3D(points.index[i*10 + j + 11][0], points.index[i*10 + j + 11][1], 0, points.color[i*10 + j + 11]); //shift p1 to opposite corner
					//p2 = p2.replaceColor(colorB);
          //p3 = p3.replaceColor(colorC);
					renderer.drawPolygon(Polygon.make(p1, p2, p3), panel, c-> c);
        }
      }
    }

    else{
        //p1 = new Vertex3D(10, 10, 0, Color.WHITE);
      for(int i = 0; i < 9; i++){ //i determines row or y value
        for(int j = 0; j < 9; j++){
          colorA = Color.random(ColorRNG);
          p1 = new Vertex3D(points.x[j], points.y[10 - i - 1], 0,colorA);
          p2 = new Vertex3D(points.x[j + 1], points.y[10 - i - 1], 0, colorA);
          p3 = new Vertex3D(points.x[j], points.y[10 - i - 2], 0, colorA);

          renderer.drawPolygon(Polygon.make(p1, p2, p3), panel, c-> Color.WHITE);
          // the adjacent triangle
          colorB = Color.random(ColorRNG);
          p1 = new Vertex3D(points.x[j + 1], points.y[10 - i - 2], 0, colorB); //shift
          p2 = p2.replaceColor(colorB);
          p3 = p3.replaceColor(colorB);
          renderer.drawPolygon(Polygon.make(p1, p2, p3), panel, c-> c);
        }
      }
    }
  }

    private void makeCenter() {
      int centerX = panel.getWidth() / 2;
      int centerY = panel.getHeight() / 2;
      center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
    }

    private class Point{ //make life easier
      int[][] index = new int[100][2]; //index[index][0] == x, index[index][1] == y
			Color[] color = new Color[100];
      double[] x = new double[10]; //don't need these actually
      double[] y = new double[10];
    }
	}
