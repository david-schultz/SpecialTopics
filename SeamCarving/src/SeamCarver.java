import java.awt.Color;

public class SeamCarver {
	
	private int width;
	private int height;
	
	private int[] rPix;
	private int[] gPix;
	private int[] bPix;
	
	private double[][] energy;
	
	private double[] distTo;
	private Integer[] prev;
	
	//private SmC_Picture picture;
	
	public SeamCarver(SmC_Picture pictureP) {
		if(pictureP == null)
			throw new IllegalArgumentException();
		
		width = pictureP.width();
		height = pictureP.height();
		 
		rPix = new int[width*height];
		gPix = new int[width*height];
		bPix = new int[width*height];
		
		energy = new double[height][width];
		
		picClone(pictureP);
		//picture = picClone(pictureP);
		
		
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				energy[j][i] = energy(i, j);
			}
		}
		
	}
	
		private void picClone(SmC_Picture base){
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					rPix[pixelToInt(i, j)] = base.get(i, j).getRed();
					gPix[pixelToInt(i, j)] = base.get(i, j).getGreen();
					bPix[pixelToInt(i, j)] = base.get(i, j).getBlue();
				}
			}
		}
		
		private int pixelToInt(int w, int h) {
			return (h * width) + w;
		}
		
		private int pixelToIntMODIFIED(int w, int h) {
			return (h * height) + w;
		}
		
		private int intToX(int index) {
			return index % width;
		}
		private int intToY(int index) {
			return index / width;
		}
			
		private void transpose() {
			
			int t = width;
			width = height;
			height = t;
			
			double[][] inverseE = new double[height][width];
			
			int[] inverseRPix = new int[width*height];
			int[] inverseGPix = new int[width*height];
			int[] inverseBPix = new int[width*height];
			
			
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					inverseE[j][i] = energy[i][j];
					inverseRPix[pixelToInt(i, j)] = rPix[pixelToIntMODIFIED(j, i)];
					inverseGPix[pixelToInt(i, j)] = gPix[pixelToIntMODIFIED(j, i)];
					inverseBPix[pixelToInt(i, j)] = bPix[pixelToIntMODIFIED(j, i)];
				}
			}
			
			//width = inverseW;
			//height = inverseH;
			
			energy = inverseE;
			
			rPix = inverseRPix;
			gPix = inverseGPix;
			bPix = inverseBPix;
		}
	
	public SmC_Picture picture() {
		SmC_Picture pic = new SmC_Picture(width, height);
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				int point = pixelToInt(i, j);
				Color color = new Color(rPix[point], gPix[point], bPix[point]);
				pic.set(i, j, color);
			}
		}
		return pic;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	
	
	/* E = (root)(I + J)
	 * I = R^2 + G^2 + B^2;
	 * R = pixel.R(x - 1) - pixel.R(x + 1)			<-- abs value
	 */
	public double energy(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height)
			throw new IndexOutOfBoundsException();
		
		if(x == 0 || y == 0 || x == width-1 || y == height-1)
			return 1000;
		
		
		int xMinus 	= pixelToInt(x - 1, y);
		int xPlus 	= pixelToInt(x + 1, y);
		int yMinus 	= pixelToInt(x, y - 1);
		int yPlus 	= pixelToInt(x, y + 1);
		
		int rx = Math.abs(rPix[xMinus] - rPix[xPlus]);
			rx *= rx;
		int ry = Math.abs(rPix[yMinus] - rPix[yPlus]);
			ry *= ry;
		
		int gx = Math.abs(gPix[xMinus] - gPix[xPlus]); 
			gx *= gx;
		int gy = Math.abs(gPix[yMinus] - gPix[yPlus]);
			gy *= gy;
		
		int bx = Math.abs(bPix[xMinus] - bPix[xPlus]); 
			bx *= bx;
		int by = Math.abs(bPix[yMinus] - bPix[yPlus]);
			by *= by;
		
		int deltaX = (rx + gx + bx);
		int deltaY = (ry + gy + by);
		
		return Math.sqrt(deltaX + deltaY);
	}

	
	//for : top pixel, find the distance of the route to the bottom. compare each top pixel
	//or
	//create an asp, then go through each bottom pixel
	public int[] findVerticalSeam() {
		resetASP();

		for(int u = 0; u < width*height; u++) {
			if(intToY(u) != height -1) {
				int[] edges = edges(u);
				for(int v : edges) {
					relax(u, v);
				}
			}
		}
		
		Integer node = -1;
		double shortestPath = Double.MAX_VALUE;
		for(int i = 0; i < width; i++) {
			//at the bottom row, find index w/ shortest path
			//TODO: here's the issue with seams
			
			int index = pixelToInt(i, height - 1);
			if(distTo[index] < shortestPath) {
			
				node = index;
				shortestPath = distTo[index];
			}
		}
		//System.out.println(node + "|" + shortestPath);
		int[] seam = findSeam(node, shortestPath);
		/*
		int index = height - 1;
		while(node != null) {	//or while(index != -1) {
			seam[index] = intToX(node);
			node = prev[node];
			index--;
		}
		*/
		return seam;
	}
	
	public int[] findHorizontalSeam() {
		transpose();
		int[] seam = findVerticalSeam();
		transpose();
		return seam;
	}

		//-------------//
	
		private int[] findSeam(int node, double shortestPath) {
			int[] seam = new int[height];
			System.out.println(node);
			int i = height - 1;
			while(i >= 0) {
				seam[i] = intToX(node);
				node = prev[node];
				i--;
			}
			
			return seam;
		}
		
		private void resetASP() {
			distTo = new double[width*height];
			prev = new Integer[width*height];
			
			for(int i = 0; i < width; i++) {
				distTo[i] = 1000;
				prev[i] = -1;
			}
			for(int i = width; i < width*height; i++) {
				distTo[i] = Double.MAX_VALUE;
				prev[i] = -1;
			}
		}
		
		private int[] edges(int u) {
			int x = intToX(u);
			//int y = intToY(u) + 1;
			
			int size = 3;
			if(x == 0) size--;
			if(x == (width-1)) size--;
			
			/*
			int[] edges = new int[size];
			if(x==0) {
				edges[0] = pixelToInt(x, y);
				if(x!=(width-1))
					edges[1] = pixelToInt(x+1, y);
			}
			else if(x==(width-1)) {
				edges[0] = pixelToInt(x-1, y);
				edges[1] = pixelToInt(x, y);
			}
			else {
				edges[0] = pixelToInt(x-1, y);
				edges[1] = pixelToInt(x, y);
				edges[2] = pixelToInt(x+1, y);
			}
			return edges;
			*/
			
			int[] edges = new int[size];
			if(x==0) {
				edges[0] = u + (width);
				if(x!=(width-1))
					edges[1] = u + (width+1);
			}
			else if(x==(width-1)) {
				edges[0] = u + (width-1);
				edges[1] = u + (width);
			}
			else {
				edges[0] = u + (width-1);
				edges[1] = u + (width);
				edges[2] = u + (width+1);
			}
			return edges;
		}
		
		private void relax(int u, int v) {
			double dist = distTo[u] + energy(intToX(v), intToY(v));
			
			//System.out.println("--");
			//System.out.println("u: " + u);
			//System.out.println((int)(dist) + "   |  " + (int)(distTo[v]));
			
			if(prev[v] == null) {
				distTo[v] = dist;
				prev[v] = u;
			}
			else {
				if(dist < distTo[v]) {
					//System.out.println("gottem");
					distTo[v] = dist;
					prev[v] = u;
				}
			}
		}
		
		//-------------//
		/*
		public double dist(int w, int h) {
			return (int)(distTo[pixelToInt(w, h)]);
		}
		public double pre(int w, int h) {
			if(prev[pixelToInt(w, h)] == null)
				return -1;
			return (int)(prev[pixelToInt(w, h)]);
		} */
		
		//-------------//
		
		private int pixelToIntMODIFIED2(int w, int h) {
			return (h * (width-1)) + w;
		}
		
	public void removeVerticalSeam(int[] a) {
		if(width <= 1)
			throw new IllegalArgumentException();
		if(a == null)
			throw new NullPointerException();
		if(a.length != height)
			throw new IllegalArgumentException();
		int lastValue = a[0];
		for(int i = 0; i < a.length; i++) {
			if (a[i] < 0 || a[i] >= width)
				throw new IllegalArgumentException();
			if(Math.abs(lastValue - a[i]) > 1)
				throw new IllegalArgumentException();
			lastValue = a[i];
		}
		
		
		int[] newRPix = new int[(width-1)*(height)];
		int[] newGPix = new int[(width-1)*(height)];
		int[] newBPix = new int[(width-1)*(height)];
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(x != a[y]) {
					int oldP = pixelToInt(x, y);
					if(x < a[y]) {
						newRPix[pixelToIntMODIFIED2(x, y)] = rPix[oldP];
						newGPix[pixelToIntMODIFIED2(x, y)] = gPix[oldP];
						newBPix[pixelToIntMODIFIED2(x, y)] = bPix[oldP];
					}
					else {
						int newP = pixelToIntMODIFIED2(x-1, y);
						newRPix[newP] = 
								rPix[oldP];
						newGPix[newP] = gPix[oldP];
						newBPix[newP] = bPix[oldP];
					}
				}
			}
		}
		width--;
		rPix = newRPix;
		gPix = newGPix;
		bPix = newBPix;
	}
		public void removeHorizontalSeam(int[] a) {
			if(height <= 1)
				throw new IllegalArgumentException();
			
			transpose();
			removeVerticalSeam(a);
			transpose();
		}
}