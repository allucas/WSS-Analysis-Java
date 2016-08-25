	public int[][][] strel_disk(int[][] imgin, int radius) { 
		int height = imgin.length; 
		int width = imgin[0].length; 

		if (radius < 1) { 
			radius = 1; 
		} else if ((radius > (height/2)) || (radius > (width/2))) { 
			JOptionPane.showMessageDialog(null,"ERR strel: RADIUS TOO LARGE"); 
			System.exit(0); 
		}

		int vecsz = (2*radius) + 1; 

		int[][] imgpad = createimgpad(imgin,vecsz,vecsz); 

		int y; 
		int x; 
		double dist; 
		int ind1 = 0; 
		double rad_squared = Math.pow(radius,2); 
		int span = (int) Math.floor(vecsz/2);
		int szradvec = 0; 


		for (int aa = 0; aa < vecsz; aa++) {  
			y = aa - radius; 
			for (int bb = 0; bb < vecsz; bb++) {
				x = bb - radius; 
				dist = Math.pow(x,2) + Math.pow(y,2); 
				if (dist < rad_squared) { 
					szradvec++; 
				}
			}
		}	
	
		int[] rowvec = new int[szradvec]; 
		int[] colvec = new int[szradvec];		 

		for (int aa = 0; aa < vecsz; aa++) {  
			y = aa - radius; 
			for (int bb = 0; bb < vecsz; bb++) {
				x = bb - radius; 
				dist = Math.pow(x,2) + Math.pow(y,2); 
				if (dist < rad_squared) { 
					rowvec[ind1] = y; 
					colvec[ind1] = x; 
					ind1++; 
				}
			}
		}

		int ind2; 
		int ind3; 
		int ind4; 
		int ind5; 
		int[][][] conv_circl = new int[height][width][szradvec];  

		for (int cc = 0; cc < height; cc++) { 
			ind2 = cc + span; 
			for (int dd = 0; dd < width; dd++) {
				ind3 = dd + span; 
				for (int ee = 0; ee < rowvec.length; ee++) { 
					ind4 = ind2 + rowvec[ee];
					ind5 = ind3 + colvec[ee]; 
					conv_circl[cc][dd][ee] = imgpad[ind4][ind5]; 
				}

			}
		}

		return conv_circl; 
	}