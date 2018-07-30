package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	private class ObjVertex {
		// TODO: fill this class in.  Store indices for a vertex, a texture, and a normal.  Have getters for them.
		//there are 3 seperate lists. This class tells you which list value to use
		private int vertexIndex;
		private int textureIndex;
		private int normalIndex;

		public int getVertexIndex(){
			return this.vertexIndex;
		}
		public int getTextureIndex(){
			return this.textureIndex;
		}
		public int getNormalIndex(){
			return this.normalIndex;
		}
		public ObjVertex(int vIndex, int tIndex, int nIndex){
			this.vertexIndex = vIndex;
			this.textureIndex = tIndex;
			this.normalIndex = nIndex;
		}
	}
	//ObjFace is a list where each item is just a set of the 3 values above
	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;
	}
	private LineBasedReader reader;

	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;

	ObjReader(String filename, Color defaultColor) {
		// TODO: Initialize an instance of this class.

		reader = new LineBasedReader(filename);
		this.defaultColor = defaultColor;

		this.objVertices = new ArrayList<Vertex3D>();
		this.transformedVertices = new ArrayList<Vertex3D>();
		this.objNormals = new ArrayList<Point3DH>();
		this.objFaces = new ArrayList<ObjFace>();

		objVertices.add(new Vertex3D(0.0, 0.0, 0.0, Color.BLACK));
		objNormals.add(new Point3DH(0.0, 0.0, 0.0));
	}

	public void render(SimpInterpreter SimpInterpreter) {
		int i, j;
		int index;
		int vertexIndex, textureIndex, normalIndex;
		ArrayList<Vertex3D> polygons = new ArrayList<>(); //a more convenient list for the vertices
		Vertex3D p1, p2, p3, temp;
		Point3DH tempNormal;

		for(i = 0; i < objFaces.size(); i++){ //objFaces.get(i) is a face with j vertices
			for(j = 0; j < objFaces.get(i).size(); j++){
				vertexIndex = objFaces.get(i).get(j).getVertexIndex();
				normalIndex = objFaces.get(i).get(j).getNormalIndex();
				temp = objVertices.get(vertexIndex);
				temp.setNormal(objNormals.get(normalIndex));

				polygons.add(temp);
			}
			p1 = polygons.get(0);
			for(j = 0; j < polygons.size() - 2; j++){
				p2 = polygons.get(j + 1);
				p3 = polygons.get(j + 2);
				SimpInterpreter.interpretObjPolygon(p1, p2, p3);
			}
			polygons.clear();
		}
		// TODO: Implement.  All of the vertices, normals, and faces have been defined.
		// First, transform all of the vertices.
		// Then, go through each face, break into triangles if necessary, and send each triangle to the renderer.
		// You may need to add arguments to this function, and/or change the visibility of functions in SimpInterpreter.

	}

	private Polygon polygonForFace(ObjFace face) {
		// TODO: This function might be used in render() above.  Implement it if you find it handy.
		Vertex3D p1 = new Vertex3D(0.0, 0.0, 0.0, Color.RED); //temp
		Vertex3D p2 = new Vertex3D(1.0, 1.0, 0.0, Color.RED);
		Vertex3D p3 = new Vertex3D(10.0, 10.0, 0.0, Color.RED);
		return Polygon.make(p1, p2, p3);
	}

	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		int i;

		for(i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");

			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());

			ObjVertex newObjVertex = new ObjVertex(vertexIndex, textureIndex, normalIndex);
			face.add(newObjVertex);
		}
		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		// TODO: write this.  subtokens[tokenIndex], if it exists, holds a string for an index.
		// use Integer.parseInt() to get the integer value of the index.
		// Be sure to handle both positive and negative indices.
		int subIndex;
		if(subtokens.length <= tokenIndex) return 0;
		if(subtokens[tokenIndex] == null || subtokens[tokenIndex].isEmpty()) {return 0;}
		else{
			if(Integer.parseInt(subtokens[tokenIndex]) < 0){ //negative 1-indexing
				subIndex = baseForNegativeIndices + Integer.parseInt(subtokens[tokenIndex]) + 1; //adding a NEGATIVE number
				return subIndex;
			}
			else{ //standard 1-indexing
				subIndex = Integer.parseInt(subtokens[tokenIndex]);
				return subIndex;
			}
		}
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		objNormals.add(normal);
		// TODO: I added literally just the above line of code
	}

	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);
		double x = point.getX();	//for flavour
		double y =  point.getY();
		double z = point.getZ();

		Vertex3D newVertex = new Vertex3D(x, y, z, color);
		objVertices.add(newVertex);

		// TODO:  dones monunso
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}
