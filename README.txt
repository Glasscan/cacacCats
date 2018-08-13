Jonathan Wong
301 225 063
https://github.com/Glasscan/cacacCats
Last-revision July-31-2018

javac -classpath "." client/Client.java client/Main.java
java -classpath "." client.Main


Shader

I added a shader package to store both the Shaders and the Light classes.
The shaders.java file contains all of the shading methods needed by the
three styles. Liberties were taken; the shaders do not follow the exact
methodology as shown in lecture.

For Flat shading the normal (unit) vector is calculated as usual, with the
lighting calculation done right after. This value is stored in the "shade" value
of the polygon to be used in its respective pixelShader.

Gouraud and Phong shading have polygonShaders that do nothing, and a VertexShader
which calculates/gets the normal(Gouraud also gives the vertices color).
Their pixelShaders do not interpolate since it was more convenient to use,
and update, the filledPolygonRenderer instead.

In filledPolygonRenderer, the interfaces/lambdas are used to store the functions
necessary via a switch statement.

---------------------------

Light

The Light class stores the information on the Lights, which it keeps in an
arrayList. This list must be reset when loading a new page.

The lighting class has methods for performing the lighting calculation. This
includes finding the unit vectors and getting the Light values. R, G and B
values needed to be calculated seperately. I did not store ambientLight nor the
specular coefficient values in a polygon; I left them within the SimpInterpreter
to be retrieved.

---------------------------

Vectors and Normals

Vertex3D was updated to include the cameraSpacePoint and a normal. The cameraSpacePoint
is added before any matrix multiplication is performed, and the normal is
right-multiplied by the inverse of the CTM as needed. I kept the normals as a
Point3DH instead of defining a new class.

---------------------------

Bugs

The lighting appears to come from the wrong place. If I were to assume why,
its because I missed a transformation somewhere.

---------------------------

page-i

I did not feel like getting really creative so I put a tree surrounded by
some assorted boxes.
