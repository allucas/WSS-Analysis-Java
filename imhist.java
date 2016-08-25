	public int[] imhist(int[][] img) { 
		int[] counter = new int[256]; 
		int pixel; 
		int height = img.length; 
		int width = img[0].length; 

		for (int j = 0; j < 256; j++) { 
			for (int col = 0; col < width; col++) { 
				for (int row = 0; row < height; row++) { 
					pixel = img[row][col]; 

					if (pixel == j) { 
						counter[j]++;
					}
				}
			}
		}
		return counter; 
	}