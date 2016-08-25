	public int[][] imread(ImageProcessor ip1, int height, int width) { 
		int[][] imagedata = new int[height][width]; 
		for (int iii = 0; iii < height; iii++){

			for (int jjj = 0; jjj < width; jjj++) { 
				imagedata[iii][jjj] = ip1.getPixel(jjj,iii); 
			}
		}
		return imagedata; 	
	}