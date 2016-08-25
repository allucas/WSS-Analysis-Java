	public int[][] imadjust(int[][] img, double minvalin, double maxvalin, double minvalout, double maxvalout) { 

		if (minvalin < 0 || minvalin > 1 || maxvalin < 0 || maxvalout > 1 || minvalout < 0 || minvalout > 1 || maxvalout < 0 || maxvalin > 1) { 
			JOptionPane.showMessageDialog(null,"ERR: imadjust INPUTS MUST BE BETWEEN 0 and 1");
			System.exit(0);
		}
		minvalin = 255*minvalin; 
		maxvalin = 255*maxvalin; 
		minvalout = 255*minvalout; 
		maxvalout = 255*maxvalout; 

		int height = img.length; 
		int width = img[0].length; 

		double slope = (maxvalout-minvalout)/(maxvalin-minvalin); 
		double intercept = (maxvalout) - (slope*maxvalin); 

		int pixel; 
		double newpix; 
		int[][] scldimg = new int[height][width]; 
		 
		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				pixel = img[ii][jj]; 

				if (pixel < minvalin) { 
					scldimg[ii][jj] = (int) Math.round((minvalout)); 
				} else if (pixel > (maxvalin)) { 
					scldimg[ii][jj] = (int) Math.round((maxvalout));
				} else { 
					newpix = slope*(pixel) + intercept; 
					scldimg[ii][jj] = (int) Math.round(newpix); 
				}
			}
		}

		return scldimg; 
	}