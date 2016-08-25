	public int[][] medfilt2(int[][] img1,int row, int col) { 
		int height = img1.length; 
		int width = img1[0].length; 
		int[][] img1filt = new int[height][width];

		if (row< 3) { 
			row = 3; 
		}

		if (col < 3) { 
			col = 3; 

		}

		if (row > height || col > width) { 
			JOptionPane.showMessageDialog(null, "KERNEL OUT OF BOUNDS");
			System.exit(0); 
		}		

		int vecsz = row*col; 
		int[][][] myconvolution = conv2(img1,row,col);

		int newpxl; 
		int[] dummyarr = new int[vecsz]; 
		int[] dummy = new int[2];
		int median; 
		int leftmed; 
		int rightmed; 


		if ((vecsz % 2) == 1) {
			dummy[0] = (int) (vecsz-1)/2; 
			dummy[1] = 0; 
		} else {
			dummy[0] = (int) Math.floor((vecsz-1)/2); 
			dummy[1] = (int) Math.ceil((vecsz-1)/2); 
		}
				
		for (int ii = 0 ; ii < height; ii ++) { 
			for (int jj = 0; jj < width; jj++) { 
				for (int kk = 0; kk < vecsz; kk++) { 
					dummyarr[kk] = myconvolution[ii][jj][kk]; 
				}
				Arrays.sort(dummyarr); 
				if ((vecsz % 2) == 1) { 
					median = (int) dummyarr[dummy[0]];
				} else {	
					leftmed = (int) dummyarr[dummy[0]];
					rightmed = (int) dummyarr[dummy[1]];
					median = (int) Math.floor((leftmed/2)+(rightmed/2));
				}
				img1filt[ii][jj] = median;
			}
		}
		return img1filt; 
	}	