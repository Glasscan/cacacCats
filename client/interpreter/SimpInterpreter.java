package client.interpreter;

import java.util.Stack;

import client.interpreter.LineBasedReader; //ok
import geometry.Point3DH;
import geometry.Rectangle;
import geometry.Vertex3D;
import line.LineRenderer;
import line.DDALineRenderer;
import client.Clipper;
import windowing.drawable.DepthCueingDrawable;
import client.RendererTrio;
import client.interpreter.BadObjFileException; //ASSN3
import client.interpreter.ObjReader;
import geometry.Transformation;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.WireframePolygonRenderer;

import shader.Shader;
import shader.Shaders;
import shader.Light;
import shader.Lighting;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private RenderStyle renderStyle;
	public static ShaderStyle shaderStyle; //assn4

	private static Transformation CTM;
	private static Transformation worldToCamera; //assn3 camera; world to camera
	private static Transformation projectedToScreen; //assn3 camera; sim to world to screen
	private static Transformation worldToScreen;
	private static Transformation perspectiveMatrix; //blazingly simple
	private static Stack<Transformation> matrixStack; //because we need it

	private static Boolean usingCamera = false;
	private Shader ambientShader;

	private static int WORLD_LOW_X = -100; //set these as default values
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;

	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;

	private static Color defaultColor = Color.WHITE;
	private static double kSpec;
	private static double p; //specular exponent

	private static Color ambientLight = Color.BLACK;

	private Drawable drawable;
	private Drawable depthCueingDrawable;

	private LineRenderer lineRenderer;
	private PolygonRenderer filledRenderer;
	private PolygonRenderer wireframeRenderer;
	private static Transformation cameraToScreen;
	private static Clipper clipper; //TO DO EVENTUALLY

	public enum RenderStyle { //was alrady here so I'll use it
		FILLED,
		WIREFRAME;
	}

	public enum ShaderStyle { //assn4
		PHONG,
		GOURAUD,
		FLAT;
	}

	public SimpInterpreter(String filename,
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = new DepthCueingDrawable(drawable, 0, -200, Color.WHITE);
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		defaultColor = Color.WHITE;
		kSpec = 0.3;
		p = 8;

		reader = new LineBasedReader(filename);
		readerStack = new Stack<>(); //already here so I'll use this
		renderStyle = RenderStyle.FILLED;
		shaderStyle = ShaderStyle.PHONG;

		CTM = Transformation.identity(); //already here so using this
		makeWorldToScreenTransform(drawable.getDimensions());
		matrixStack = new Stack<>(); // I put this in
		clipper = new Clipper(depthCueingDrawable);

		Light.lightListClear(); //reset the lighting
	}
//worldToScreen is to be updated;
//This is M (V <- W)
	private void makeWorldToScreenTransform(Dimensions dimensions) {
		worldToScreen = Transformation.identity();
		Transformation transMatrix = new Transformation();
		int width = dimensions.getWidth();
		int height = dimensions.getHeight();
		double scaleX = width/(WORLD_HIGH_X - WORLD_LOW_X);
		double scaleY = height/(WORLD_HIGH_Y - WORLD_LOW_Y);

		transMatrix = Transformation.identity();
		transMatrix.set(0, 3, width/2); //to translate x and y by the center value
		transMatrix.set(1, 3, height/2);
		transMatrix.set(2, 3, 0);

		worldToScreen = Transformation.matrixMultiply(worldToScreen, transMatrix);

		transMatrix = Transformation.identity();
		transMatrix.set(0, 0, scaleX); //and the scale
		transMatrix.set(1, 1, scaleY);
		transMatrix.set(2, 2, 1);

		worldToScreen = Transformation.matrixMultiply(worldToScreen, transMatrix);
		CTM = Transformation.matrixMultiply(worldToScreen, CTM);

	}

	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;

		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;

		case "light" : interpretLight(tokens); break;
		case "phong" : phong(); break;
		case "gouraud" : gouraud(); break;
		case "flat" : flat(); break;

		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void push() { // I assume it's push matrix (CTM) onto stack
		matrixStack.push(CTM);
	}
	private void pop() {
		CTM = matrixStack.pop(); //return to the previous matrix
	}
	private void wire() {
		renderStyle = RenderStyle.WIREFRAME;
	}
	private void filled() {
		renderStyle = RenderStyle.FILLED;
	}

	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}

//adjust the CTM via these transformations (always assmume 4x4)
	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);

		Transformation scaleMatrix = new Transformation();
		scaleMatrix = Transformation.identity();
		scaleMatrix.set(0, 0, sx);
		scaleMatrix.set(1, 1, sy);
		scaleMatrix.set(2, 2, sz);

		CTM = Transformation.matrixMultiply(CTM, scaleMatrix);
		// TODO: I THINK I did it right
	}
	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);

		Transformation transMatrix = new Transformation();
		transMatrix = Transformation.identity();
		transMatrix.set(0, 3, tx);
		transMatrix.set(1, 3, ty);
		transMatrix.set(2, 3, tz);

		CTM = Transformation.matrixMultiply(CTM, transMatrix);
		// TODO: le doneso finish this method
	}

//will deal with all 3 cases
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1]; // X Y or Z
		double angleInDegrees = cleanNumber(tokens[2]);

		double angleRadians = 2*(Math.PI)*(angleInDegrees/360);
		Transformation rotateMatrix = new Transformation();
		rotateMatrix = Transformation.identity();

		switch(axisString){
			case "X":
				rotateMatrix.set(1, 1, Math.cos(angleRadians));
				rotateMatrix.set(1, 2, -Math.sin(angleRadians));
				rotateMatrix.set(2, 1, Math.sin(angleRadians));
				rotateMatrix.set(2, 2, Math.cos(angleRadians));
				CTM = Transformation.matrixMultiply(CTM, rotateMatrix);
				break;

			case "Y":
				rotateMatrix.set(0, 0, Math.cos(angleRadians));
				rotateMatrix.set(0, 2, Math.sin(angleRadians));
				rotateMatrix.set(2, 0, -Math.sin(angleRadians));
				rotateMatrix.set(2, 2, Math.cos(angleRadians));
				CTM = Transformation.matrixMultiply(CTM, rotateMatrix);
				break;

			case "Z":
				rotateMatrix.set(0, 0, Math.cos(angleRadians));
				rotateMatrix.set(0, 1, -Math.sin(angleRadians));
				rotateMatrix.set(1, 0, Math.sin(angleRadians));
				rotateMatrix.set(1, 1, Math.cos(angleRadians));
				CTM = Transformation.matrixMultiply(CTM, rotateMatrix);
				break;

			default:
				break;
		}
	}

	private static double cleanNumber(String string) {
		return Double.parseDouble(string);
	}

	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);

		private int numTokensPerVertex;

		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}

//alter via the CTM and Call the line(p1, p2) method at the bottom
	private void interpretLine(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);
		Vertex3D newP1, newP2;
		Vertex3D oldP1 = vertices[0];
		Vertex3D oldP2 = vertices[1];
		double x, y, z = 0.0;
		double[][] transform = CTM.getMatrix();

		newP1 = Transformation.vectorMultiply(transform, oldP1);
		newP2 = Transformation.vectorMultiply(transform, oldP2);

		Color newColor = newP1.getColor().scale(1 - newP1.getZ()/(-200.0));
		newP1 = newP1.replaceColor(newColor);
		newColor = newP2.getColor().scale(1 - newP2.getZ()/(-200.0));
		newP2 = newP2.replaceColor(newColor);

		line(newP1, newP2); //CALL THE LINE
		// TODO: finish this method
	}

	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
		Vertex3D newP1, newP2, newP3;
		Vertex3D oldP1 = vertices[0];
		Vertex3D oldP2 = vertices[1];
		Vertex3D oldP3 = vertices[2];
		double x, y, z = 0.0;
		double[][] transform = CTM.getMatrix();

		//for p1 (hardcoding)
		newP1 = Transformation.vectorMultiply(transform, oldP1);
		newP2 = Transformation.vectorMultiply(transform, oldP2);
		newP3 = Transformation.vectorMultiply(transform, oldP3);

		//update the colors
		Color newColor = newP1.getColor().scale(1 - newP1.getZ()/(-200.0));
		newP1 = newP1.replaceColor(newColor);
		newColor = newP2.getColor().scale(1 - newP2.getZ()/(-200.0));
	 	newP2 = newP2.replaceColor(newColor);
	 	newColor = newP3.getColor().scale(1 - newP3.getZ()/(-200.0));
		newP3 = newP3.replaceColor(newColor);

		if(newP1.hasNormal()) newP1.setNormal(unitVector(newP1.getNormal()));
		if(newP2.hasNormal()) newP2.setNormal(unitVector(newP2.getNormal()));
		if(newP3.hasNormal()) newP3.setNormal(unitVector(newP3.getNormal()));
		polygon(newP1, newP2, newP3);
		// TODO: finish this method
	}
	//for the object reader
	public void interpretObjPolygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		Vertex3D newP1, newP2, newP3;
		Vertex3D oldP1 = p1;
		Vertex3D oldP2 = p2;
		Vertex3D oldP3 = p3;

		double[][] transform = CTM.getMatrix();
		double[][] transformInverse = Transformation.inverse(CTM).getMatrix();

		//for p1 (hardcoding)
		newP1 = Transformation.vectorMultiply(transform, oldP1);
		newP2 = Transformation.vectorMultiply(transform, oldP2);
		newP3 = Transformation.vectorMultiply(transform, oldP3);
		//new for assn4

		if(p1.hasNormal()) {
			newP1.setNormal(unitVector(newP1.getNormal()));
			newP1.setNormal(Transformation.normalVectorMultiply(newP1.getNormal(), transformInverse));
		}
		if(p2.hasNormal()) {
			newP2.setNormal(unitVector(newP2.getNormal()));
			newP2.setNormal(Transformation.normalVectorMultiply(newP2.getNormal(), transformInverse));
		}
		if(p3.hasNormal()) {
			newP3.setNormal(unitVector(newP3.getNormal()));
			newP3.setNormal(Transformation.normalVectorMultiply(newP3.getNormal(), transformInverse));
		}


		polygon(newP1, newP2, newP3);

	}

	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);
		Vertex3D vertices[] = new Vertex3D[numVertices];

		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}

	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}

	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}

	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}


	private static Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		Color color;
		if(colored == VertexColors.COLORED) {
		  color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}
		else color = defaultColor;
		Vertex3D newVertex = new Vertex3D(point.getX(), point.getY(), point.getZ(), color); //that it ?
		return newVertex;
		// TODO: ready eddy? finish this method
	}

	public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		if(z == 0.0) z = -0.1; //a default value so we don't divide by 0
		Point3DH newPoint = new Point3DH(x, y, z); //that it ?
		return newPoint;
		// TODO: done ???? finish this method
	}

	public static Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		Color newColor = new Color(r, g, b);
		return newColor;
		// TODO: done??? finish this method
	}

	//new for assn3
	public void interpretAmbient(String[] tokens){
		double r = cleanNumber(tokens[1]); //assume 0 to 1 scale
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);

		this.ambientLight = new Color(r, g, b); //temp
	}
//an overload and PROBABLY where we actually draw to the screen -> called in interpret
//transform one last time for the camera
	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);

		lineRenderer.drawLine(screenP1, screenP2, drawable);
	}

	//ASSN3: since we aren't using parallel projections anymore
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {

		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		Vertex3D screenP3 = transformToCamera(p3);

			if(Math.abs(screenP1.getZ()) + Math.abs(screenP2.getZ()) + Math.abs(screenP3.getZ()) < 20.0){
				return;
			}

		screenP1.setCameraPoint(p1); //assn4
		screenP2.setCameraPoint(p2);
		screenP3.setCameraPoint(p3);

		Polygon newPolygon = Polygon.make(screenP1, screenP2, screenP3);
		Vertex3D[] list;
		int listSize = 0;

		//System.out.println(screenP1 + " \n" + screenP2 + " \n" + screenP3);

		switch(renderStyle){
			case WIREFRAME: //don't bother doing anything special
			list = Clipper.polygonClip(newPolygon); //amazing
			if(list == null) return;

			listSize = list.length;
					if(listSize > 3){
						for(int i = 0; i < listSize - 1; i++){
							lineRenderer.drawLine(list[i], list[i+1], drawable);
						}
						lineRenderer.drawLine(list[listSize - 1], list[0], drawable); //last line
					}
					else
						wireframeRenderer.drawPolygon(newPolygon, depthCueingDrawable,
							ambientShader = c -> ambientLight.multiply(c));
				break;
			default:
				list = Clipper.polygonClip(newPolygon); //amazing
				if(list == null) return;

				listSize = list.length;
				if(listSize > 3){

					for(int i = 0; i < listSize - 2; i++){
						newPolygon = Polygon.make(list[0], list[i+1], list[i+2]);

						filledRenderer.drawPolygon(newPolygon, depthCueingDrawable, ambientShader = c -> ambientLight.multiply(c));
					}
				}
				else{
					filledRenderer.drawPolygon(newPolygon, depthCueingDrawable, ambientShader = c -> ambientLight.multiply(c));
				}
				break;
		}
	}

	//for assn3; EVERYTHING BELOW THIS POINT
	private Vertex3D transformToCamera(Vertex3D vertex) {
		if(!usingCamera){
			return vertex;
		}
		else{
		double d = 1.0/perspectiveMatrix.get(3,2);
		double z = vertex.getCameraSpaceZ();

		//the following is the equation for the simple perspective transformation
		Vertex3D newVertex = Transformation.vectorMultiply(cameraToScreen.getMatrix(), vertex);
 		newVertex = new Vertex3D(newVertex.getX()/(z/d), newVertex.getY()/(z/d), z, vertex.getColor());
		if(vertex.hasNormal()) newVertex.setNormal(vertex.getNormal());

		return newVertex;
		}
	}

	//camera is at the origin
	public static void interpretCamera(String[] tokens){
		usingCamera = true;
		double xlow = cleanNumber(tokens[1]);
		double ylow = cleanNumber(tokens[2]);
		double xhigh = cleanNumber(tokens[3]);
		double yhigh = cleanNumber(tokens[4]);
		double hither = cleanNumber(tokens[5]); //near clipping plane
		double yon = cleanNumber(tokens[6]); //far clipping plane
		int i = 0;

		int tempSize = matrixStack.size();

		perspectiveMatrix = Transformation.identity();
		if(CTM.get(2,3) == 0.0)
			perspectiveMatrix.set(3, 2, 1.0);
		else
			perspectiveMatrix.set(3, 2, 1.0/CTM.get(2,3)); //will be useful later
		perspectiveMatrix.set(3, 3, 0.0);
		//not sure if correct
		perspectiveMatrix.set(0, 0, -perspectiveMatrix.get(3,2));
		perspectiveMatrix.set(1, 1, -perspectiveMatrix.get(3,2));

		Stack<Transformation> tempStack = new Stack<>();
		Transformation tempMatrix = Transformation.identity();

		worldToCamera = Transformation.inverse(CTM); //because I was told to

		for(i = 0; i < tempSize; i++){ //pop all and multiply
			tempMatrix = matrixStack.pop();
			tempStack.push(Transformation.matrixMultiply(worldToCamera, tempMatrix));
		}
		for(i = 0; i < tempSize; i++){ //pop all and multiply
			tempMatrix = tempStack.pop();
			matrixStack.push(tempMatrix);
		}
		CTM = Transformation.matrixMultiply(worldToCamera, CTM);
		//now in viewspace after pre-multiplying the inverse

		clipper.updateClipper(hither, yon); //reset the clipper
		makeProjectedToScreenTransform(xlow, xhigh, ylow, yhigh);
		cameraToScreen = Transformation.matrixMultiply(projectedToScreen, perspectiveMatrix);
	}

	private static void makeProjectedToScreenTransform(double xlow, double xhigh,
		double ylow, double yhigh) {

		projectedToScreen = Transformation.identity();
		Transformation transMatrix = new Transformation();
		int width = 650;
		int height = 650;
		double dx = (xhigh - xlow);
		double dy = (yhigh - ylow);
		double scaleX = width/(xhigh - xlow);
		double scaleY = height/(yhigh - ylow);
		double scaleWindow = 0.0;

		if(dx != dy){ //not a square
			if(dx > dy){ //window is wider
				scaleWindow = dx/dy;
				transMatrix = Transformation.identity();
				transMatrix.set(0, 3, width/dx);
				transMatrix.set(1, 3, height - height/(2*scaleWindow));
				transMatrix.set(2, 3, 0);
				projectedToScreen = Transformation.matrixMultiply(projectedToScreen, transMatrix);

				transMatrix = Transformation.identity();
				transMatrix.set(0, 0, scaleX);
				transMatrix.set(1, 1, scaleY/scaleWindow);
				transMatrix.set(2, 2, 1);

				clipper.updateClipper(0.0,  0.0 + height/(2*scaleWindow), width, height - height/(2*scaleWindow));
			}
			else{ //window is taller
				scaleWindow = dy/dx;
				transMatrix = Transformation.identity();
				transMatrix.set(0, 3, width - width/(2*scaleWindow));
				transMatrix.set(1, 3, height/dy);
				transMatrix.set(2, 3, 0);
				projectedToScreen = Transformation.matrixMultiply(projectedToScreen, transMatrix);

				transMatrix = Transformation.identity();
				transMatrix.set(0, 0, scaleX/scaleWindow);
				transMatrix.set(1, 1, scaleY);
				transMatrix.set(2, 2, 1);

				clipper.updateClipper(0.0 + width/(2*scaleWindow), 0.0, width - width/(2*scaleWindow), height);
			}
		}
		else{ //normal case
			transMatrix = Transformation.identity();
			transMatrix.set(0, 3, width/(xhigh - xlow)); //to translate x and y by the center value
			transMatrix.set(1, 3, height/(yhigh - ylow));
			transMatrix.set(2, 3, 0);
			projectedToScreen = Transformation.matrixMultiply(projectedToScreen, transMatrix);


			transMatrix = Transformation.identity();
			transMatrix.set(0, 0, scaleX); //and the scale
			transMatrix.set(1, 1, scaleY);
			transMatrix.set(2, 2, 1); //redundant
		}
		projectedToScreen = Transformation.matrixMultiply(projectedToScreen, transMatrix);

	}

	public void interpretSurface(String[] tokens){
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);

		double k = cleanNumber(tokens[4]); //assn4
		double p = cleanNumber(tokens[5]);

		defaultColor = new Color(r, g, b);
		kSpec = k;
		this.p = p;
	}

	public void interpretDepth(String[] tokens){
		double near = cleanNumber(tokens[1]);
		double far = cleanNumber(tokens[2]);

		double r = cleanNumber(tokens[3]);
		double g = cleanNumber(tokens[4]);
		double b = cleanNumber(tokens[5]);
		Color farColor = new Color(r, g, b);

		this.depthCueingDrawable = new DepthCueingDrawable(drawable, 0, -200, Color.WHITE, near, far, farColor);
	}
	//given for assn3 and I think Point3D is supposed to be Point3DH
	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		Point3DH point = new Point3DH(x, y, z, w);
		return point;
	}

	private void interpretObj(String[] tokens){
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
		String filename = quotedFilename.substring(1, length-1);
		System.out.println(filename+ ".obj");
		objFile(filename + ".obj");
	}

	private void objFile(String filename) {
		ObjReader objReader = new ObjReader(filename, defaultColor);
		objReader.read();
		objReader.render(this);
	}

	//assn4
	private void interpretLight(String[] tokens){
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);

		double A = cleanNumber(tokens[4]); //attenuation constants
		double B = cleanNumber(tokens[5]);
		Color lightIntensity = new Color(r, g, b);
		Vertex3D lightPoint = new Vertex3D(0.0, 0.0, 0.0, lightIntensity);

		lightPoint = Transformation.vectorMultiply(CTM.getMatrix(), lightPoint);

		Point3DH lightPoint3D = new Point3DH(lightPoint.getX(), lightPoint.getY(), lightPoint.getZ());
		Light newLight = new Light(lightIntensity, lightPoint3D, A, B);

	}

	private void phong(){
		shaderStyle = ShaderStyle.PHONG;
	}
	private void gouraud(){
		shaderStyle = ShaderStyle.GOURAUD;
	}
	private void flat(){
		shaderStyle = ShaderStyle.FLAT;
	}
	private Point3DH unitVector(Point3DH vector){
		double magnitude = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getY(), 2) + Math.pow(vector.getZ(), 2));
		double x = vector.getX()/magnitude;
		double y = vector.getY()/magnitude;
		double z = vector.getZ()/magnitude;
		if(magnitude == 0) x = y = z = 0.0;
		return new Point3DH(x, y, z);
	}

	public static Color getAmbient(){
		return ambientLight;
	}
	public static double getKSpec(){
		return kSpec;
	}
	public static double getSpecExp(){
		return p;
	}
}
