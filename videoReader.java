	public int[][][] videoReader(ImageStack imp1) { 
		int height = imp1.getHeight(); 
		int width = imp1.getWidth(); 
		int sz = imp1.getSize(); 

		int[][][] imgout = new int[height][width][sz]; 

		int[][] dummyarr = new int[height][width]; 

		int ind1; 
		for (int nn = 1; nn <= sz; nn++) { 
			ImageProcessor myip = imp1.getProcessor(nn); 
			dummyarr = imread(myip,height,width); 
			ind1 = nn -1; 
			for (int ii = 0; ii < height; ii++) { 
				for (int jj = 0; jj < width; jj++) { 
					imgout[ii][jj][ind1] = dummyarr[ii][jj]; 
				}
			}
		}
		return imgout; 	
	}