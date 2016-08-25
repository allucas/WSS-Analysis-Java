	public int[][] enhanceImg(int[][] img) {  
		int pixel; 
		int height = img.length; 
		int width = img[0].length; 
		int[] counter = imhist(img); 
		int[][] imgout = new int[height][width]; 

		int sum = 0; 
		int summ = 0; 

		for (int k = 0; k < 256; k++) { 
			sum += counter[k]*k;
			summ += counter[k]; 
		}

		int mean = (int) Math.round(sum/summ); 

		double num = 0; 
		double den = 0; 

		for (int m =0; m < 256; m++) { 
			num += counter[m]*Math.pow((m-mean),2); 
			den += counter[m]; 
		}

		double var = num/(den-1); 
		double stdev = Math.pow(var,0.5); 
		double min = mean - stdev; 
		double max = mean; 

		double lowbound = min/255; 
		double highbound = max/255;

		imgout = imadjust(img,lowbound,highbound,0,1); 

		return imgout;  
	}