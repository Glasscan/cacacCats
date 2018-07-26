package line;

import geometry.Point3DH;
import geometry.Vertex3D;
import windowing.drawable.DrawableDecorator;
import windowing.drawable.Drawable;

enum Octant {
	OctantI(){
		public double octantIx(double x, double y) { return x; }
		public double octantIy(double x, double y) { return y; }
		public int inverseX(int x, int y) { return x; }
		public int inverseY(int x, int y) { return y; }
	},
	OctantII(){
		public double octantIx(double x, double y) { return y; }
		public double octantIy(double x, double y) { return x; }
		public int inverseX(int x, int y) { return y; }
		public int inverseY(int x, int y) { return x; }
	},
	OctantIII(){
		public double octantIx(double x, double y) { return y; }
		public double octantIy(double x, double y) { return -x; }
		public int inverseX(int x, int y) { return -y; }
		public int inverseY(int x, int y) { return x; }
	},
	OctantIV(){
		public double octantIx(double x, double y) { return -x; }
		public double octantIy(double x, double y) { return y; }
		public int inverseX(int x, int y) { return -x; }
		public int inverseY(int x, int y) { return y; }
	};
	
	abstract public double octantIx(double x, double y);
	abstract public double octantIy(double x, double y);
	abstract public int inverseX(int x, int y);
	abstract public int inverseY(int x, int y);
	
	static public Octant octantForVector(int x, int y) {
		assert y >= 0 : "enum Octant: assumption of vector in first four octants violated";
		if(x > 0) {
			return (x >= y) ? OctantI : OctantII;
		}
		else {
			return (-x >= y) ? OctantIV : OctantIII;
		}
	}
	
	Vertex3D toOctant1(Vertex3D vertex) {
		double transformedX = octantIx(vertex.getX(), vertex.getY());
		double transformedY = octantIy(vertex.getX(), vertex.getY());
		return vertex.replacePoint(new Point3DH(transformedX, transformedY, vertex.getZ()));
	}
	
	public Drawable invertingDrawable(Drawable drawable) {
		return new DrawableDecorator(drawable) {
			@Override
			public void setPixel(int x, int y, double z, int color) {
				delegate.setPixel(inverseX(x, y), inverseY(x, y), z, color);
			}
			@Override
			public int getPixel(int x, int y) {
				return delegate.getPixel(inverseX(x, y), inverseY(x, y));
			}			
			@Override
			public double getZValue(int x, int y) {
				return delegate.getZValue(inverseX(x, y), inverseY(x, y));
			}
			@Override
			public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
				delegate.setPixelWithCoverage(inverseX(x, y), inverseY(x, y), z, argbColor, coverage);
			}

		};
	}
}