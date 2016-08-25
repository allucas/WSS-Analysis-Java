	public int[][] im2bw(int[][] scldimg, double threshold) { 
		int height = scldimg.length; 
		int width = scldimg[0].length; 
		int[][] newimg = new int[height][width]; 
		threshold = threshold*255; 

		int pxl; 

		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				pxl = scldimg[ii][jj]; 

				if (pxl > threshold) { 
					newimg[ii][jj] = 255; 				
				} else { 
					newimg[ii][jj] = 0; 
				}
			}

		}

		return newimg; 
	}