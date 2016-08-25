	public int[][] imopen(int[][] imgin, int rad) {

		int height = imgin.length; 
		int width = imgin[0].length; 
		double stoph = height*0.5; 
		double stopw = width*0.5;  

		if (rad < 1) { 
			rad = 1;
		} else if (rad > stoph || rad > stopw) { 
			JOptionPane.showMessageDialog(null,"ERR: imclose RADIUS TOO LARGE"); 
			System.exit(0); 
		}

		int[][][] my_strel = strel_disk(imgin,rad); 

		int[][] imgout = erosion(my_strel,rad); 

		int[][][] my_strel2 = strel_disk(imgout,rad); 

		imgout = dilation(my_strel2,rad); 

		return imgout; 
	}