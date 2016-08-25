	public int[][] imgaussfilt(int[][] imgin, double sigma) { 
		int height = imgin.length;
		int width = imgin[0].length; 
		int[][] imgout = new int[height][width]; 

		if (sigma < 1) { 
			sigma = 1; 
		}

		int max_sigmah = (int) Math.ceil(height/3); 
		int max_sigmaw = (int) Math.ceil(width/3); 

		if (sigma > max_sigmah || sigma > max_sigmaw) { 
			JOptionPane.showMessageDialog(null,"ERR: imgausfilt STDEV SIGMA TOO LARGE"); 
			System.exit(0);
		}

		int kernelsz = (int) (3*sigma);  
		int vecsz = (int) Math.round(Math.pow(kernelsz,2)); 
		int[][][] myconvolution = conv2(imgin,kernelsz,kernelsz);

		int span = (int) Math.floor(kernelsz/2); 
		int[] rowvec = new int[kernelsz]; 
		int[] colvec = new int[kernelsz]; 

		for (int xx = 0; xx < kernelsz; xx++) { 
			if (xx == 0) { 
				rowvec[xx] = -1*span;
				colvec[xx] = -1*span;
			} else { 
				rowvec[xx] = rowvec[xx-1] + 1; 
				colvec[xx] = colvec[xx-1] + 1; 
			}
		}

		int x; 
		int y; 
		int ind1; 

		double[] dist = new double[vecsz]; 

		for (int ii = 0; ii < kernelsz; ii++) { 
			x = rowvec[ii]; 
			for (int jj = 0; jj < kernelsz; jj++) { 
				y = colvec[jj]; 
				ind1 = (ii*kernelsz) + jj; 
				dist[ind1] = Math.pow(x,2) + Math.pow(y,2); 
			}

		}

		double den = 2*Math.PI*Math.pow(sigma,2); 
		double[] gaussvec = new double[vecsz]; 
		double pwr = -1/(2*Math.pow(sigma,2)); 
		double gausssum = 0; 

		for (int kk = 0; kk < vecsz; kk++) { 
			gaussvec[kk] = (1/den)*Math.exp(pwr*dist[kk]); 
			gausssum += gaussvec[kk];  
		}	

		for (int ll = 0; ll < vecsz; ll++) { 
			gaussvec[ll] = gaussvec[ll]/gausssum; 
		}


		double pxlval = 0; 

		for (int aa = 0; aa < height; aa++) { 
			for (int bb = 0; bb < width; bb++) { 
				pxlval = 0; 
				for (int cc = 0; cc < vecsz; cc++) { 
					pxlval +=  myconvolution[aa][bb][cc]*gaussvec[cc]; 
				}
				imgout[aa][bb] = (int) Math.round(pxlval); 
			}
		}

		return imgout; 
	}