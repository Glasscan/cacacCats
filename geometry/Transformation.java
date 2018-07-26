package geometry;

//assuming that everything is 4x4 matrices and last row is: 0 0 0 1
/* for convenience xy:
00 01 02 03
10 11 12 13
20 21 22 23
30 31 32 33
*/
import geometry.Vertex3D;

public class Transformation {
  private double[][] matrix = new double[4][4];

  public Transformation(){ //all 0's by default
  }

  //for an identity matrix
  public static Transformation identity(){
    Transformation newMatrix = new Transformation();
    for(int i = 0; i < 4; i++){ //reset to all 0
      for(int j = 0; j < 4; j++){
        newMatrix.set(i, j, 0.0);
      }
    }
    for(int i = 0; i < 4; i++){
      newMatrix.set(i, i, 1.0); //1s on the diagonal
    }
    return newMatrix;
  }

  //painful
  public static Transformation inverse(Transformation matrix){
    Transformation tempMatrix = new Transformation(); //make a copy of old one
    Transformation newMatrix = Transformation.identity();
    double temp = 0.0;
    int i, j = 0;

    for(i = 0; i < 4; i++){
      for(j = 0; j < 4; j++){
        tempMatrix.set(i, j, matrix.get(i, j)); //deep copy
      }
    }

//round 1, first column
    temp = 1.0/tempMatrix.get(0, 0);
    Transformation.rowScale(tempMatrix, 0, temp); //scale the 0,0 value to 1
    Transformation.rowScale(newMatrix, 0, temp);

    temp = tempMatrix.get(1, 0); //what we subtract from the second row
    Transformation.rowAddition(tempMatrix, 0, 1, (-1)*temp); //add first row to second row
    Transformation.rowAddition(newMatrix, 0, 1, (-1)*temp);

    temp = tempMatrix.get(2, 0); //what we subtract from the third row
    Transformation.rowAddition(tempMatrix, 0, 2, (-1)*temp); //add first row to third row
    Transformation.rowAddition(newMatrix, 0, 2, (-1)*temp);

//round 2: second column; first column is 1 0 0 0
    temp = 1.0/tempMatrix.get(1, 1);
    Transformation.rowScale(tempMatrix, 1, temp); //scale the 0,0 value to 1
    Transformation.rowScale(newMatrix, 1, temp);

    temp = tempMatrix.get(0, 1);
    Transformation.rowAddition(tempMatrix, 1, 0, (-1)*temp); //add 2nd row to 1st row
    Transformation.rowAddition(newMatrix, 1, 0, (-1)*temp);

    temp = tempMatrix.get(2, 1); //what we subtract from the third row
    Transformation.rowAddition(tempMatrix, 1, 2, (-1)*temp); //add first row to third row
    Transformation.rowAddition(newMatrix, 1, 2, (-1)*temp);

    //round 3: third column; first column is 1 0 0 0 and second column 0 1 0 0
    temp = 1.0/tempMatrix.get(2, 2);
    Transformation.rowScale(tempMatrix, 2, temp); //scale the 0,0 value to 1
    Transformation.rowScale(newMatrix, 2, temp);

    temp = tempMatrix.get(0, 2);
    Transformation.rowAddition(tempMatrix, 2, 0, (-1)*temp); //add 2nd row to 1st row
    Transformation.rowAddition(newMatrix, 2, 0, (-1)*temp);

    temp = tempMatrix.get(1, 2); //what we subtract from the third row
    Transformation.rowAddition(tempMatrix, 2, 1, (-1)*temp); //add first row to third row
    Transformation.rowAddition(newMatrix, 2, 1, (-1)*temp);

    //round 4
    temp = 1.0/tempMatrix.get(3, 3);
    Transformation.rowScale(tempMatrix, 2, temp); //scale the 0,0 value to 1
    Transformation.rowScale(newMatrix, 2, temp);

    temp = tempMatrix.get(0, 3);
    Transformation.rowAddition(tempMatrix, 3, 0, (-1)*temp); //add 2nd row to 1st row
    Transformation.rowAddition(newMatrix, 3, 0, (-1)*temp);

    temp = tempMatrix.get(1, 3); //what we subtract from the third row
    Transformation.rowAddition(tempMatrix, 3, 1, (-1)*temp); //add first row to third row
    Transformation.rowAddition(newMatrix, 3, 1, (-1)*temp);

    temp = tempMatrix.get(2, 3); //what we subtract from the third row
    Transformation.rowAddition(tempMatrix, 3, 2, (-1)*temp); //add first row to third row
    Transformation.rowAddition(newMatrix, 3, 2, (-1)*temp);

  /*  System.out.println("AFTER");
    tempMatrix.printMatrix();
    newMatrix.printMatrix();
*/
    return newMatrix;
  }

  public void set(int x, int y, double value){
    this.matrix[x][y] = value;
  }

  public double get(int x, int y){
    return this.matrix[x][y];
  }

  public static void rowScale(Transformation matrix, int row, double scale){
    for(int i = 0; i < 4; i++){
      matrix.set(row, i, matrix.get(row, i)*scale);
    }
  }

  //add rowA to rowB
  public static void rowAddition(Transformation matrix, int rowA, int rowB, double scale){
    for(int i = 0; i < 4; i++){
      matrix.set(rowB, i, matrix.get(rowB, i) + matrix.get(rowA, i)*scale);
    }
  }

  public double[][] getMatrix(){
    return matrix;
  }

//assuming 4x4 and matB on the right for postmultiplication
  public static Transformation matrixMultiply(Transformation matA, Transformation matB){
    double[][] matrixA = matA.getMatrix();
    double[][] matrixB = matB.getMatrix();
    Transformation newMatrix = new Transformation();
    double value;

    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 4; j++){ //it's just easier to read like this
        value = 0.0; //reset
        value = value + matrixA[i][0]*matrixB[0][j];
        value = value + matrixA[i][1]*matrixB[1][j];
        value = value + matrixA[i][2]*matrixB[2][j];
        value = value + matrixA[i][3]*matrixB[3][j];
        if(value < 0.0005 && value > -0.0005){
          value = 0.0;
        }
        else if(value > 0.99 && value < 1.01){
          value = 1.0;
        }
        newMatrix.set(i, j, value);
      }
    }
    return newMatrix;
  }

  public static Vertex3D vectorMultiply(double[][] transform, Vertex3D vector){
    double x = (vector.getX()*transform[0][0]) + (vector.getY()*transform[0][1]) + (vector.getZ()*transform[0][2]) + (transform[0][3]);
    double y = (vector.getX()*transform[1][0]) + (vector.getY()*transform[1][1]) + (vector.getZ()*transform[1][2]) + (transform[1][3]);
    double z = (vector.getX()*transform[2][0]) + (vector.getY()*transform[2][1]) + (vector.getZ()*transform[2][2]) + (transform[2][3]);
    Vertex3D newVector = new Vertex3D(x, y, z, vector.getColor());
    return newVector;
  }

  public void printMatrix(){
    		for(int i = 0; i < 4; i++){
    			System.out.println(matrix[i][0] + " " + matrix[i][1] + " " +
    			matrix[i][2] + " " + matrix[i][3]);
    		}
  }

}
