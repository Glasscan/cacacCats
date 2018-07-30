package polygon;

import geometry.Vertex3D;
import geometry.Point3DH;
import client.interpreter.SimpInterpreter;
import line.DDALineRenderer;
import shader.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

import shader.FaceShader;
import shader.VertexShader;
import shader.PixelShader;
import shader.Shaders;

// assumes polygon is ccw.
public class FilledPolygonRenderer implements PolygonRenderer{
	private FilledPolygonRenderer(){}

	public void drawPolygon(Polygon polygon, Drawable drawable, Shader ambientShader) {

		FaceShader faceShader = c -> Shaders.NullFaceShader(c);
		VertexShader vertexShader = (d, e) -> Shaders.NullVertexShader(d, e);
		PixelShader pixelShader = (f, g) -> Shaders.NullPixelShader(f, g);

		Vertex3D p1 = getP1(polygon);
		Vertex3D p2 = getP2(polygon, p1);
		Vertex3D p3 = getP3(polygon, p1, p2);

		// must update for assn4
/*
		p1 = p1.replaceColor(ambientShader.shade(p1.getColor()));
		p2 = p2.replaceColor(ambientShader.shade(p2.getColor()));
		p3 = p3.replaceColor(ambientShader.shade(p3.getColor()));
*/
		switch(SimpInterpreter.shaderStyle){
		case FLAT:
			faceShader = c -> Shaders.FlatFaceShader(c);
			vertexShader = (d, e) -> Shaders.NullVertexShader(d, e);
			pixelShader = (f, g) -> Shaders.FlatPixelShader(f, g);
			break;
		case GOURAUD:
			faceShader = c -> Shaders.NullFaceShader(c);
			vertexShader = (d, e) -> Shaders.GouraudVertexShader(d, e);
			pixelShader = (f, g) -> Shaders.GouraudPixelShader(f, g);
			break;
		default: //PHONG
			faceShader = c -> Shaders.NullFaceShader(c);
			vertexShader = (d, e) -> Shaders.PhongVertexShader(d, e);
			pixelShader = (f, g) -> Shaders.PhongPixelShader(f, g);
			break;
		}

		if(p1.getY() == p3.getY()){ //annoying edge case
			Vertex3D temp = p2;
			p2 = p3;
			p3 = temp;
		}

		Polygon newPolygon = faceShader.shade(Polygon.make(p1, p2, p3)); //rearrange the order
		Color tempShadeColor = newPolygon.getShadeColor();

		p1 = vertexShader.shade(newPolygon, p1);
		p2 = vertexShader.shade(newPolygon, p2);
		p3 = vertexShader.shade(newPolygon, p3);

		newPolygon = Polygon.make(p1, p2, p3);
		//transfer the color into the new polygon (Flat only; Does nothing in Phong/Gouraud)
		newPolygon.setShadeColor(tempShadeColor);

		Lerper leftLerp = new Lerper(p1, p2);
		Lerper rightLerp = new Lerper(p1, p3);
		double leftBottom = Math.abs(p2.getY() - p1.getY());
		double rightBottom = Math.abs(p3.getY() - p1.getY());

		//fraw the first pixel
		drawable.setPixel(p1.getIntX(), p1.getIntY(), p1.getIntZ(), pixelShader.shade(newPolygon, p1).asARGB());

		double fx = 0.0;
		double fy = 0.0;
		double csz = 0.0; //will be inverse when retrieved
		double dx;

		double red;
		double green;
		double blue;

		double cszSlope;
		double redSlope;
		double greenSlope;
		double blueSlope;
		boolean hasNormal = p1.hasNormal();

//assn4
double normalX, normalY, normalZ, cameraX, cameraY, cameraZ;
double normalXSlope, normalYSlope, normalZSlope, cameraXSlope, cameraYSlope, cameraZSlope;
		while(leftBottom > 0.0 || rightBottom > 0.0){
			fx = leftLerp.getLerpX();
			fy = leftLerp.getLerpY();
			csz = leftLerp.getLerpCsz(); // 1/csz
			dx = Math.abs(leftLerp.getLerpX() - rightLerp.getLerpX());

			red = leftLerp.getLerpRed();
			green = leftLerp.getLerpGreen();
			blue = leftLerp.getLerpBlue();

			//for extra precision
			double redLeft = leftLerp.getLerpRed()*(1.0/leftLerp.getLerpCsz());
			double greenLeft = leftLerp.getLerpGreen()*(1.0/leftLerp.getLerpCsz());
			double blueLeft = leftLerp.getLerpBlue()*(1.0/leftLerp.getLerpCsz());

			double redRight = rightLerp.getLerpRed()*(1.0/rightLerp.getLerpCsz());
			double greenRight = rightLerp.getLerpGreen()*(1.0/rightLerp.getLerpCsz());
			double blueRight = rightLerp.getLerpBlue()*(1.0/rightLerp.getLerpCsz());

			cszSlope = (rightLerp.getLerpCsz() - leftLerp.getLerpCsz())/dx;
			redSlope = (redRight*rightLerp.getLerpCsz() - redLeft*leftLerp.getLerpCsz())/dx; //(red/csz - red/csz)/dx
			greenSlope = (greenRight*rightLerp.getLerpCsz() - greenLeft*leftLerp.getLerpCsz())/dx;
			blueSlope = (blueRight*rightLerp.getLerpCsz() - blueLeft*leftLerp.getLerpCsz())/dx;

			//new for assignment 4
			normalX = leftLerp.getNormal().getX();
			normalY = leftLerp.getNormal().getY();
			normalZ = leftLerp.getNormal().getZ();
			cameraX = leftLerp.getCameraPoint().getX();
			cameraY = leftLerp.getCameraPoint().getY();
			cameraZ = leftLerp.getCameraPoint().getZ();

			normalXSlope = (rightLerp.getNormal().getX() - leftLerp.getNormal().getX())/dx;
			normalYSlope = (rightLerp.getNormal().getY() - leftLerp.getNormal().getY())/dx;
			normalZSlope = (rightLerp.getNormal().getZ() - leftLerp.getNormal().getZ())/dx;
			cameraXSlope = (rightLerp.getCameraPoint().getX() - leftLerp.getCameraPoint().getX())/dx;
			cameraYSlope = (rightLerp.getCameraPoint().getY() - leftLerp.getCameraPoint().getY())/dx;
			cameraZSlope = (rightLerp.getCameraPoint().getZ() - leftLerp.getCameraPoint().getZ())/dx;

			Vertex3D lerpVertex;

			for(int i = 0; i < Math.abs(dx); i++){
				Color color = new Color(red*(1.0/csz), green*(1.0/csz), blue*(1.0/csz));

				lerpVertex = new Vertex3D(fx, fy, 1.0/csz, color);

				if(hasNormal) lerpVertex.setNormal(new Point3DH(normalX, normalY, normalZ));
				lerpVertex.setCameraPoint(new Point3DH(cameraX, cameraY, cameraZ));
				color = pixelShader.shade(newPolygon, lerpVertex);

				drawable.setPixel((int)Math.round(fx), (int)Math.round(fy), 1.0/csz, color.asARGB());

				fx = fx + 1.0; //move over to the right so ADD the slopes
				csz = csz + cszSlope;
				red = red + redSlope;
				green = green + greenSlope;
				blue = blue + blueSlope;

				//assn4
				normalX = normalX + normalXSlope;
				normalY = normalY + normalYSlope;
				normalZ = normalZ + normalZSlope;
				cameraX = cameraX + cameraXSlope;
				cameraY = cameraY + cameraYSlope;
				cameraZ = cameraZ + cameraZSlope;
			}
			if(!leftLerp.incrementDownwards()){
				leftLerp = new Lerper(p2, p3); //this is how I flip things
			}
			if(!rightLerp.incrementDownwards()){
				rightLerp = new Lerper(p3, p2);
			}

			leftBottom = leftBottom - 1;
			rightBottom = rightBottom - 1;
		}
		//System.out.println(p1 + " " +p2 + " "+ p3);
	}


	private class Lerper{
		private double x;
		private double y;
		private double csz; //csz for assn3 perspective
		private double redValue; //color values are of form: red/csz
		private double greenValue;
		private double blueValue;

		private double dy;
		private double yCounter; //check to see how far we've gone
		private final double xSlope;
		private final double zSlope;

		private final double redSlope; //color slopes are of form: red/csz
		private final double greenSlope;
		private final double blueSlope;

		//new for assignment 4 (normal and CameraSpacePoint)
		private double cameraX;
		private double cameraY;
		private double cameraZ;
		private double normalX;
		private double normalY;
		private double normalZ;

		private final double cameraXSlope;
		private final double cameraYSlope;
		private final double cameraZSlope;
		private final double normalXSlope;
		private final double normalYSlope;
		private final double normalZSlope;


		public Lerper(Vertex3D p1, Vertex3D p2){ //starting points
			this.x = p1.getX();
			this.y = p1.getY();
			this.csz = 1.0/p1.getCameraSpaceZ();
			this.redValue = p1.getColor().getR()*csz;
			this.greenValue = p1.getColor().getG()*csz;
			this.blueValue = p1.getColor().getB()*csz;

			this.dy = p2.getY() - p1.getY();
			this.yCounter = Math.abs(dy);
			this.xSlope = (p2.getX() - p1.getX())/dy;
			this.zSlope = (1.0/p2.getCameraSpaceZ() - 1.0/p1.getCameraSpaceZ())/dy;
			this.redSlope = (p2.getColor().getR()*(1.0/p2.getZ()) - p1.getColor().getR()*(1.0/p1.getZ()))/dy;
			this.greenSlope = (p2.getColor().getG()*(1.0/p2.getZ()) - p1.getColor().getG()*(1.0/p1.getZ()))/dy;
			this.blueSlope = (p2.getColor().getB()*(1.0/p2.getZ()) - p1.getColor().getB()*(1.0/p1.getZ()))/dy;

			//new for assignment 4 (not using perspective correct for this)
			this.cameraX = p1.getCameraPoint().getX();
			this.cameraY = p1.getCameraPoint().getY();
			this.cameraZ = p1.getCameraPoint().getZ();
			this.normalX = p1.getNormal().getX();
			this.normalY = p1.getNormal().getY();
			this.normalZ = p1.getNormal().getZ();

			this.cameraXSlope = (p2.getCameraPoint().getX() - p1.getCameraPoint().getX())/dy;
			this.cameraYSlope = (p2.getCameraPoint().getY() - p1.getCameraPoint().getY())/dy;
			this.cameraZSlope = (p2.getCameraPoint().getZ() - p1.getCameraPoint().getZ())/dy;
			this.normalXSlope = (p2.getNormal().getX() - p1.getNormal().getX())/dy;
			this.normalYSlope = (p2.getNormal().getY() - p1.getNormal().getY())/dy;
			this.normalZSlope = (p2.getNormal().getZ() - p1.getNormal().getZ())/dy;

		}

		public Boolean incrementDownwards(){ //SUBTRACT since we go downwards
			this.yCounter = yCounter - 1;
			this.y = this.y - 1;
			this.x = this.x - this.xSlope;
			this.csz = this.csz - this.zSlope;

			this.redValue = this.redValue - this.redSlope;
			this.greenValue = this.greenValue - this.greenSlope;
			this.blueValue = this.blueValue - this.blueSlope;

			//new for assignment 4
			this.cameraX = this.cameraX - this.cameraXSlope;
			this.cameraY = this.cameraY - this.cameraYSlope;
			this.cameraZ = this.cameraZ - this.cameraZSlope;
			this.normalX = this.normalX - this.normalXSlope;
			this.normalY = this.normalY - this.normalYSlope;
			this.normalZ = this.normalZ - this.normalZSlope;

			if(yCounter <= 0) return false;
			return true;
		}

		public double getLerpX(){
			return this.x;
		}
		public double getLerpY(){
			return this.y;
		}
		public double getLerpCsz(){
			return this.csz;
		}
		public double getLerpRed(){
			return this.redValue;
		}
		public double getLerpGreen(){
			return this.greenValue;
		}
		public double getLerpBlue(){
			return this.blueValue;
		}
		public Point3DH getNormal(){
			return new Point3DH(this.normalX, this.normalY, this.normalZ);
		}
		public Point3DH getCameraPoint(){
			return new Point3DH(this.cameraX, this.cameraY, this.cameraZ);
		}
	}

	private Vertex3D getP1(Polygon polygon){
		Vertex3D p1 = polygon.get(0); //initialize
		Vertex3D p2 = polygon.get(1);
		Vertex3D p3 = polygon.get(2);

		Vertex3D primary = p1;
		if(primary.getY() < p2.getY()) primary = p2;
		if(primary.getY() < p3.getY()) primary = p3; //p1 should be at the top

		if(primary.getY() == p2.getY() && primary.getX() < p2.getX()) primary = p2;
		if(primary.getY() == p3.getY() && primary.getX() < p3.getX()) primary = p3;

		return primary;
	}

	private Vertex3D getP2(Polygon polygon, Vertex3D p1){ //revision using angles
		Vertex3D x1 = polygon.get(0); //initialize
		Vertex3D x2 = polygon.get(1);
		Vertex3D x3 = polygon.get(2);
		double slopeA, slopeB = 0.0;
		double angleA, angleB = 0.0;

		Vertex3D primary = x1;
		if(p1 == x1) { //first possibility
			slopeA = (x1.getY() - x2.getY())/(x1.getX() - x2.getX());
			angleA = Math.atan(slopeA)*2*Math.PI; //it's a nicer number
			slopeB = (x1.getY() - x3.getY())/(x1.getX() - x3.getX());
			angleB = Math.atan(slopeB)*2*Math.PI;
			if(x2.getX() < x1.getX()) angleA = 180.0 - angleA;
			if(x3.getX() < x1.getX()) angleB = 180.0 - angleB; //use the largest angle
			if(Math.abs(angleA) > Math.abs(angleB)) primary = x2;
			else primary = x3;

		}
		else if(p1 == x2){
			slopeA = (x2.getY() - x1.getY())/(x2.getX() - x1.getX());
			angleA = Math.atan(slopeA)*2*Math.PI; //it's a nicer number
			slopeB = (x2.getY() - x3.getY())/(x2.getX() - x3.getX());
			angleB = Math.atan(slopeB)*2*Math.PI;
			if(x1.getX() < x2.getX()) angleA = 180.0 - angleA;
			if(x3.getX() < x2.getX()) angleB = 180.0 - angleB; //use the largest angle
			if(Math.abs(angleA) > Math.abs(angleB)) primary = x1;
			else primary = x3;
		}
		else{ //p1 == x3
			slopeA = (x3.getY() - x1.getY())/(x3.getX() - x1.getX());
			angleA = Math.atan(slopeA)*2*Math.PI; //it's a nicer number
			slopeB = (x3.getY() - x2.getY())/(x3.getX() - x2.getX());
			angleB = Math.atan(slopeB)*2*Math.PI;
			if(x1.getX() < x3.getX()) angleA = 180.0 - angleA;
			if(x2.getX() < x3.getX()) angleB = 180.0 - angleB; //use the largest angle
			if(Math.abs(angleA) > Math.abs(angleB)) primary = x1;
			else primary = x2;
		}
		return primary;
	}
	private Vertex3D getP3(Polygon polygon, Vertex3D p1, Vertex3D p2){
		Vertex3D x1 = polygon.get(0); //initialize
		Vertex3D x2 = polygon.get(1);
		Vertex3D x3 = polygon.get(2);

		Vertex3D primary = x1;
		if(x1 != p1 && x1 != p2) primary = x1; //x1 not present
		else if(x2 != p1 && x2 != p2) primary = x2;
		else if(x3 != p1 && x3 != p2) primary = x3;

		return primary;
	}


  public static PolygonRenderer make(){
  	return new FilledPolygonRenderer();
  }
}
