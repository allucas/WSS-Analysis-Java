public ArrayList<int[][]> bwboundaries(int[][] imgin) { 
		ArrayList<int[][]> coordinates = new ArrayList<int[][]>();

		int height = imgin.length; 
		int width = imgin[0].length;

		int[][][] myconv = conv2(imgin,3,3);
		int sz = myconv[0][0].lenght;

		int[][] pxlsum = new int[height][width];   

		int dummy; 

		for (int aa = 0; aa < height; aa++) { 
			for (int bb = 0; bb < width; bb++) { 
				dummy = 0; 
				for (int cc = 0; cc < sz; cc++) { 
					dummy += myconv[cc]; 
				}
				pxlsum[ii][jj] = dummy; 
			}
		}

		int[][] coordinates = new int[2][];

		 

				
}