	public boolean createimage(int[][] img2, String path) { 
		int height = img2.length; 
		int width = img2[0].length; 
		int stacks = 1; 
 
		String title = "Median Filtered Image";
		ImagePlus myimp = NewImage.createByteImage(title,width,height,stacks,NewImage.FILL_WHITE);

		ImageProcessor myimg1 = myimp.getProcessor(); 

		for (int i = 0; i < height; i++) { 
			for (int j = 0; j < width; j++) {
				 int intensity = img2[i][j]; 
				 myimg1.putPixel(j,i,intensity); 
			}
		}

		IJ.saveAsTiff(myimp,path); 
		return true;  
	}