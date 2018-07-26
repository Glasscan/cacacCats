package client;

import windowing.drawable.Drawable;
import windowing.drawable.DepthCueingDrawable;
import windowing.graphics.Dimensions;
import geometry.Vertex3D;
import polygon.Polygon;

import java.util.ArrayList;

public class Clipper {
  private static double xMin, yMin, zMin;
  private static double xMax, yMax, zMax;

  public Clipper(){}

  public Clipper(Drawable panel){
    Clipper.xMin = Clipper.yMin = 0.0;
    Clipper.xMax = panel.getWidth();
    Clipper.yMax = panel.getHeight();
    Clipper.zMin = -200.0;
    Clipper.zMax = 0.0;
  }

  //directly input the values (assn3)
  public void updateClipper(double hither, double yon){
    Clipper.zMin = yon;
    Clipper.zMax = hither;
  }
  public void updateClipper(double xMin, double yMin, double xMax, double yMax){
    Clipper.xMin = xMin;
    Clipper.xMax = xMax;
    Clipper.yMin = yMin;
    Clipper.yMax = yMax;
  }

  //i am only handling the really trivial cases
  public int cull(Vertex3D p1, Vertex3D p2){ //for lines
    int cull = 0;
    if(p1.getX() < xMin && p2.getX() < xMin) {cull = 1; return cull;}
    else if(p1.getX() > xMax && p2.getX() > xMax) {cull = 1; return cull;}
    else if(p1.getY() < yMin && p2.getY() < yMin) {cull = 1; return cull;}
    else if(p1.getY() > yMax && p2.getY() > yMax) {cull = 1; return cull;}
    else if(p1.getZ() < zMin && p2.getZ() < zMin) {cull = 1; return cull;}
    else if(p1.getZ() > zMax && p2.getZ() > zMax) {cull = 1; return cull;}
    return cull;
  }

  public int cull(Vertex3D p1, Vertex3D p2, Vertex3D p3){ //for polygons
    int cull = 0;

    if(p1.getX() < xMin && p2.getX() < xMin && p3.getX() < xMin) {cull = 1; return cull;}
    else if(p1.getX() > xMax && p2.getX() > xMax  && p3.getX() > xMax) {cull = 1; return cull;}
    else if(p1.getY() < yMin && p2.getY() < yMin  && p3.getY() < yMin) {cull = 1; return cull;}
    else if(p1.getY() > yMax && p2.getY() > yMax  && p3.getY() > yMax) {cull = 1; return cull;}
    else if(p1.getZ() < zMin && p2.getZ() < zMin  && p3.getZ() < zMin) {cull = 1; return cull;}
    else if(p1.getZ() > zMax && p2.getZ() > zMax  && p3.getZ() > zMax) {cull = 1; return cull;}
    else
      return cull;
  }
  public static Boolean pixelClip(int x, int y, double z){
    Boolean clip = false;
    if(x < xMin || x >= xMax){
      clip = true;
      return clip;
    }
    else if(y < yMin || y >= yMax){
      clip = true;
      return clip;
    }
    else if(z < zMin || z > zMax){
      clip = true;
      return clip;
    }
    else return clip;
  }

  //using same convention as my FilledPolygonRenderer of finding things CCW
  //assumes initial is a triangle
  public static Vertex3D[] polygonClip(Polygon polygon){
    ArrayList<Vertex3D> tempVertexList = new ArrayList<>(); //6 is the worst case scenario for triangles
    ArrayList<Vertex3D> newVertexList = new ArrayList<>();

    Vertex3D p1 = getP1(polygon);
    Vertex3D p2 = getP2(polygon, p1);
    Vertex3D p3 = getP3(polygon, p1, p2);
    int i = 0;
    int listSize = 0;

    if(p1.getY() == p3.getY()){ //annoying edge case
      Vertex3D temp = p2;
      p2 = p3;
      p3 = temp;
    }
    //start at line p1 to p2

    tempVertexList.add(p1); //intial
    tempVertexList.add(p2);
    tempVertexList.add(p3);
    listSize = tempVertexList.size();
    for(i = 0; i < listSize - 1; i++){
      cutRightEdge(tempVertexList.get(i), tempVertexList.get(i+1), newVertexList);
    }
    cutRightEdge(tempVertexList.get(listSize - 1), tempVertexList.get(0), newVertexList); //last element + first one
    if(transfer(newVertexList, tempVertexList)) return null; //use the new values

    listSize = tempVertexList.size();
    for(i = 0; i < listSize - 1; i++){
      cutTopEdge(tempVertexList.get(i), tempVertexList.get(i+1), newVertexList);
    }
    cutTopEdge(tempVertexList.get(listSize - 1), tempVertexList.get(0), newVertexList);
    if(transfer(newVertexList, tempVertexList)) return null;

    listSize = tempVertexList.size();
    for(i = 0; i < listSize - 1; i++){
      cutLeftEdge(tempVertexList.get(i), tempVertexList.get(i+1), newVertexList);
    }
    cutLeftEdge(tempVertexList.get(listSize - 1), tempVertexList.get(0), newVertexList);
    if(transfer(newVertexList, tempVertexList)) return null;

    listSize = tempVertexList.size();
    for(i = 0; i < listSize - 1; i++){
      cutBottomEdge(tempVertexList.get(i), tempVertexList.get(i+1), newVertexList);
    }
    cutBottomEdge(tempVertexList.get(listSize - 1), tempVertexList.get(0), newVertexList);
    if(transfer(newVertexList, tempVertexList)) return null;

    listSize = tempVertexList.size();
    for(i = 0; i < listSize - 1; i++){
      cutNearEdge(tempVertexList.get(i), tempVertexList.get(i+1), newVertexList);
    }
    cutNearEdge(tempVertexList.get(listSize - 1), tempVertexList.get(0), newVertexList);
    if(transfer(newVertexList, tempVertexList)) return null;

    listSize = tempVertexList.size();
    for(i = 0; i < listSize - 1; i++){
      cutFarEdge(tempVertexList.get(i), tempVertexList.get(i+1), newVertexList);
    }
    cutFarEdge(tempVertexList.get(listSize - 1), tempVertexList.get(0), newVertexList);
    if(listSize < 3) return null;

    Vertex3D[] polygons = new Vertex3D[newVertexList.size()];
    polygons = newVertexList.toArray(polygons);
    return polygons;
  }

  private static Boolean outOfBounds(Vertex3D point){
    Boolean outOfBounds = false;
    if(point.getX() >= xMax || point.getY() >= yMax || point.getZ() >= zMax){
      outOfBounds = true;
      return outOfBounds;
    }
    else if(point.getX() < xMin || point.getY() < yMin || point.getZ() < zMin){
      outOfBounds = true;
      return outOfBounds;
    }
    return outOfBounds;
  }

  //Overload
  private static Boolean outOfBounds(double x, double y, double z){
    Boolean outOfBounds = false;
    if(x >= xMax || y >= yMax || z >= zMax){
      outOfBounds = true;
      return outOfBounds;
    }
    else if(x < xMin || y < yMin || z < zMin){
      outOfBounds = true;
      return outOfBounds;
    }
    return outOfBounds;
  }

  public static Boolean transfer(ArrayList<Vertex3D> listToTransfer, ArrayList<Vertex3D> listToKeep){
    int i = 0;
    Boolean empty = false;
    int tempSize = listToTransfer.size();
    listToKeep.clear();
    for(i = 0; i < tempSize; i++){
      listToKeep.add(listToTransfer.get(i));
    }
    listToTransfer.clear(); //need to empty it out for reuse
    if(tempSize < 3) empty = true;
    else empty = false;
    return empty;
  }

  //add to the arrayList given the cohen-sutherland style clipping
  private static void cutRightEdge(Vertex3D p1, Vertex3D p2, ArrayList<Vertex3D> list){
    double y = 0.0;
    double z = 0.0; //yes I am actually integrating the z values
    if(p1.getX() <= xMax && p2.getX() <= xMax){ //in  in
      list.add(p2);
    }
    else if(p1.getX() <= xMax && p2.getX() > xMax){ //in out
      double xToEdge = xMax - p1.getX();
      double slope = (p2.getY() - p1.getY())/(p2.getX() - p1.getX());
      double zSlope = (p2.getZ() - p1.getZ())/(p2.getX() - p1.getX());

      y = p1.getY() + xToEdge*slope; //add the difference
      z = p1.getZ() + xToEdge*zSlope;
      Vertex3D newVertex = new Vertex3D(xMax, y, z, p1.getColor());
      list.add(newVertex);
    }

    else if(p1.getX() > xMax && p2.getX() > xMax){ // out out
      //do nothing
    }
    else if(p1.getX() > xMax && p2.getX() <= xMax){ //out in
      double xToEdge = xMax - p2.getX();
      double slope = (p1.getY() - p2.getY())/(p1.getX() - p2.getX());
      double zSlope = (p1.getZ() - p2.getZ())/(p1.getX() - p2.getX());
      y = p2.getY() + xToEdge*slope; //add the difference
      z = p2.getZ() + xToEdge*zSlope;

      Vertex3D newVertex = new Vertex3D(xMax, y, z, p1.getColor());
      list.add(newVertex);
      list.add(p2);
    }
  }

  private static void cutTopEdge(Vertex3D p1, Vertex3D p2, ArrayList<Vertex3D> list){
    double x = 0.0;
    double z = 0.0; //yes I am actually integrating the z values
    if(p1.getY() <= yMax && p2.getY() <= yMax){ //in  in
      list.add(p2);
    }
    else if(p1.getY() <= yMax && p2.getY() > yMax){ //in out
      double yToEdge = yMax - p1.getY();
      double xSlope = (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()); //in terms of y
      double zSlope = (p2.getZ() - p1.getZ()) / (p2.getY() - p1.getY());
      x = p1.getX() + yToEdge*xSlope; //add the difference
      z = p1.getZ() + yToEdge*zSlope;

      Vertex3D newVertex = new Vertex3D(x, yMax, z, p1.getColor());
      list.add(newVertex);
    }

    else if(p1.getY() > yMax && p2.getY() > yMax){ // out out
      //do nothing
    }
    else if(p1.getY() > yMax && p2.getY() <= yMax){ //out in
      double yToEdge = yMax - p2.getY();
      double xSlope = (p1.getX() - p2.getX()) / (p1.getY() - p2.getY()); //in terms of y
      double zSlope = (p1.getZ() - p2.getZ()) / (p1.getY() - p2.getY());
      x = p2.getX() + yToEdge*xSlope; //add the difference
      z = p2.getZ() + yToEdge*zSlope;

      Vertex3D newVertex = new Vertex3D(x, yMax, z, p1.getColor());
      list.add(newVertex);
      list.add(p2);
    }
  }

  private static void cutLeftEdge(Vertex3D p1, Vertex3D p2, ArrayList<Vertex3D> list){
    double y = 0.0;
    double z = 0.0; //yes I am actually integrating the z values
    if(p1.getX() >= xMin && p2.getX() >= xMin){ //in  in
      list.add(p2);
    }
    else if(p1.getX() >= xMin && p2.getX() < xMin){ //in out
      double xToEdge = xMin - p2.getX(); //p2 is outside on the left
      double slope = (p1.getY() - p2.getY())/(p1.getX() - p2.getX());
      double zSlope = (p1.getZ() - p2.getZ())/(p1.getX() - p2.getX());

      y = p2.getY() + xToEdge*slope; //add the difference
      z = p2.getZ() + xToEdge*zSlope;
      Vertex3D newVertex = new Vertex3D(xMin, y, z, p2.getColor());
      list.add(newVertex);
    }

    else if(p1.getX() < xMin && p2.getX() < xMin){ // out out
      //do nothing
    }
    else if(p1.getX() < xMin && p2.getX() >= xMin){ //out in
      double xToEdge = xMin - p1.getX(); //p1 is outside on the lft
      double slope = (p2.getY() - p1.getY())/(p2.getX() - p1.getX());
      double zSlope = (p2.getZ() - p1.getZ())/(p2.getX() - p1.getX());
      y = p1.getY() + xToEdge*slope; //add the difference
      z = p1.getZ() + xToEdge*zSlope;

      Vertex3D newVertex = new Vertex3D(xMin, y, z, p2.getColor());
      list.add(newVertex);
      list.add(p2);

    }
  }

  private static void cutBottomEdge(Vertex3D p1, Vertex3D p2, ArrayList<Vertex3D> list){
    double x = 0.0;
    double z = 0.0; //yes I am actually integrating the z values
    if(p1.getY() >= yMin && p2.getY() >= yMin){ //in  in
      list.add(p2);
    }
    else if(p1.getY() >= yMin && p2.getY() < yMin){ //in out
      double yToEdge = yMin - p2.getY(); //p2 is below the bottom edge
      double xSlope = (p1.getX() - p2.getX()) / (p1.getY() - p2.getY()); //in terms of y
      double zSlope = (p1.getZ() - p2.getZ()) / (p1.getY() - p2.getY());
      x = p2.getX() + yToEdge*xSlope; //add the difference
      z = p2.getZ() + yToEdge*zSlope;

      Vertex3D newVertex = new Vertex3D(x, yMin, z, p2.getColor());
      list.add(newVertex);
    }

    else if(p1.getY() < yMin && p2.getY() < yMin){ // out out
      //do nothing
    }
    else if(p1.getY() < yMin && p2.getY() >= yMin){ //out in
      double yToEdge = yMin - p1.getY();
      double xSlope = (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()); //in terms of y
      double zSlope = (p2.getZ() - p1.getZ()) / (p2.getY() - p1.getY());
      x = p1.getX() + yToEdge*xSlope; //add the difference
      z = p1.getZ() + yToEdge*zSlope;

      Vertex3D newVertex = new Vertex3D(x, yMin, z, p2.getColor());
      list.add(newVertex);
      list.add(p2);
    }
  }

//-200 is the usual minimum
  private static void cutFarEdge(Vertex3D p1, Vertex3D p2, ArrayList<Vertex3D> list){
    double x = 0.0;
    double y = 0.0;

    if(p1.getZ() >= zMin && p2.getZ() >= zMin){ //in  in
      list.add(p2);
    }

    else if(p1.getZ() >= zMin && p2.getZ() < zMin){ //in out
      double zToEdge = zMin - p2.getZ();
      double xSlope = (p1.getX() - p2.getX())/(p1.getZ() - p2.getZ());
      double ySlope = (p1.getY() - p2.getY())/(p1.getZ() - p2.getZ());

      x = p2.getX() + zToEdge*xSlope; //both have same x value
      y = p2.getY() + zToEdge*ySlope;
      Vertex3D newVertex = new Vertex3D(x, y, zMin, p2.getColor());
      list.add(newVertex);
    }

    else if(p1.getZ() < zMin && p2.getZ() < zMin){ // out out
      //do nothing
    }
    else if(p1.getZ() < zMin && p2.getZ() >= zMin){ //out in
      double zToEdge = zMin - p1.getZ();
      double xSlope = (p2.getX() - p1.getX())/(p2.getZ() - p1.getZ());
      double ySlope = (p2.getY() - p1.getY())/(p2.getZ() - p1.getZ());

      x = p1.getX() + zToEdge*xSlope; //both have same x value
      y = p1.getY() + zToEdge*ySlope;
      Vertex3D newVertex = new Vertex3D(x, y, zMin, p1.getColor());
      list.add(newVertex);
      list.add(p2);
    }
  }

  private static void cutNearEdge(Vertex3D p1, Vertex3D p2, ArrayList<Vertex3D> list){
    double x = 0.0;
    double y = 0.0;

    if(p1.getZ() <= zMax && p2.getZ() <= zMax){ //in  in
      list.add(p2);
    }

    else if(p1.getZ() <= zMax && p2.getZ() > zMax){ //in out
      double zToEdge = zMax - p1.getZ();
      double xSlope = (p2.getX() - p1.getX())/(p2.getZ() - p1.getZ());
      double ySlope = (p2.getY() - p1.getY())/(p2.getZ() - p1.getZ());

      x = p1.getX() + zToEdge*xSlope; //both have same x value
      y = p1.getY() + zToEdge*ySlope;
      Vertex3D newVertex = new Vertex3D(x, y, zMax, p2.getColor());
      list.add(newVertex);
    }

    else if(p1.getZ() > zMax && p2.getZ() > zMax){ // out out
      //do nothing
    }
    else if(p1.getZ() > zMax && p2.getZ() <= zMax){ //out in
      double zToEdge = zMax - p2.getZ();
      double xSlope = (p1.getX() - p2.getX())/(p1.getZ() - p2.getZ());
      double ySlope = (p1.getY() - p2.getY())/(p1.getZ() - p2.getZ());

      x = p2.getX() + zToEdge*xSlope; //both have same x value
      y = p2.getY() + zToEdge*ySlope;
      Vertex3D newVertex = new Vertex3D(x, y, zMax, p1.getColor());
      list.add(newVertex);
      list.add(p2);
    }
  }

  //trace a Line from one point to another and stop when you hit a boundary
  private static void traceLine(Vertex3D p1, Vertex3D p2, double edge, ArrayList<Vertex3D> list){
    Boolean isOutside = false;
    if(outOfBounds(p1)) isOutside = true; //our starting point is out of bounds
    double dy = p2.getY() - p1.getY();
    double dx = p2.getX() - p1.getX();
    double dz = p2.getZ() - p1.getZ();

    double slope = dy/dx;
    double Zslope = dz/dx;
    double counter = 0.0; //tells the thing when to stop

    double x = p1.getX(); //starting values
    double y = p1.getY();
    double z = p1.getZ();

    //vertical line case
    if(dx == 0.0) {
      int increment = -1; //going downwards
      if(p1.getY() < p2.getY()) increment = 1; //go upwards

      Zslope = dz/dy;
      while(counter <  Math.abs(dy)){
        y = y + increment;
        z = z + Zslope;
        if(outOfBounds(x, y, z) != isOutside){
          if(!outOfBounds(x, y, z)) //get the previous number instead
            list.add(new Vertex3D(x, Math.round(y - increment), Math.round(z - Zslope), p1.getColor()));
          else
            list.add(new Vertex3D(x, Math.round(y), Math.round(z), p1.getColor()));
          isOutside = !isOutside; //flip
        }
        counter++;
      }
    }
    //the horizontal line case
    else if(dy == 0.0) {
      int increment = 1; //going right
      if(p1.getX() > p2.getX()){
        increment = -1; //go left
      }
      while(counter < Math.abs(dx)){
        x = x + increment;
        z = z + Zslope;
        if(outOfBounds(x, y, z) != isOutside){
          if(!outOfBounds(x, y, z)) //get the previous number instead
            list.add(new Vertex3D(Math.round(x - increment), y, Math.round(z - Zslope), p1.getColor()));
          else
            list.add(new Vertex3D(x, y, Math.round(z), p1.getColor()));
          isOutside = !isOutside; //flip
        }
        counter++;
      }
    }
    //every other case
    else{
      int increment = 1; //going right
      if(p1.getX() > p2.getX()){
        increment = -1; //go left
        slope = -slope;
      }
      while(counter <  Math.abs(dx)){
        x = x + increment;
        y = y + slope;
        z = z + Zslope;
        if(outOfBounds(x, y, z) != isOutside){
          if(!outOfBounds(x, y, z)) //get the previous number instead
            list.add(new Vertex3D(Math.round(x - increment), Math.round(y - slope), Math.round(z - Zslope), p1.getColor()));
          else
            list.add(new Vertex3D(x, y, Math.round(z), p1.getColor()));
          isOutside = !isOutside; //flip
        }
        counter++;
      }
    }
  }

  //unused
  private static void checkCorners(Vertex3D p1, Vertex3D p2, Vertex3D p3, ArrayList<Vertex3D> list,
    double cornerX, double cornerY){
    //using cross-products
    Boolean signA, signB, signC;
    signA = signB = signC = false; //less than 0 (negative)
    //p1 to p2
    double lineA = p2.getX() - p1.getX();
    double lineB = p2.getY() - p1.getY();
    double lineD = cornerX - p1.getX();
    double lineE = cornerY - p1.getY();

    if((lineA*lineE - (lineB*lineD)) > 0.0) signA = true;

    //p2 to p3
    lineA = p3.getX() - p2.getX();
    lineB = p3.getY() - p2.getY();
    lineD = cornerX - p2.getX();
    lineE = cornerY - p2.getY();
    if((lineA*lineE - (lineB*lineD)) > 0.0) signB = true;

    lineA = p1.getX() - p3.getX();
    lineB = p1.getY() - p3.getY();
    lineD = cornerX - p3.getX();
    lineE = cornerY - p3.getY();
    if((lineA*lineE - (lineB*lineD)) > 0.0) signC = true;

    if(signA == signB && signA == signC) {
      Vertex3D newVertex = new Vertex3D(cornerX, cornerY,
        (p1.getZ() + p2.getZ() + p3.getZ())/3.0, p1.getColor()); //can't be bothered so just take the average Z
        list.add(newVertex);
    }

    return;
  }

//very useful
  private static Vertex3D getP1(Polygon polygon){
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

  private static Vertex3D getP2(Polygon polygon, Vertex3D p1){ //revision using angles
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
  private static Vertex3D getP3(Polygon polygon, Vertex3D p1, Vertex3D p2){
    Vertex3D x1 = polygon.get(0); //initialize
    Vertex3D x2 = polygon.get(1);
    Vertex3D x3 = polygon.get(2);

    Vertex3D primary = x1;
    if(x1 != p1 && x1 != p2) primary = x1; //x1 not present
    else if(x2 != p1 && x2 != p2) primary = x2;
    else if(x3 != p1 && x3 != p2) primary = x3;

    return primary;
  }
}
