	public int[][] erosion(int[][][] struc_element, int rad) { 
		int height = struc_element.length; 
		int width = struc_element[0].length; 
		int vecsz = struc_element[0][0].length; 
		int[][] imgout = new int[height][width]; 

		int[] dummyarr = new int[vecsz]; 

		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				for (int kk = 0; kk < vecsz; kk++) { 
					dummyarr[kk] = struc_element[ii][jj][kk];
				}
				Arrays.sort(dummyarr); 
				imgout[ii][jj] = dummyarr[0]; 
			}
		}

		return imgout; 
	}