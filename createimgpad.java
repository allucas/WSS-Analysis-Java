	public int[][] createimgpad(int[][] imgin,int row, int col) { 
		int height = imgin.length; 
		int width = imgin[0].length; 

		if (row < 3) { 
			row = 3; 
		}

		if (col < 3) { 
			col = 3; 
		}

		if (row > height || col > width) { 
			JOptionPane.showMessageDialog(null,"ERR: createimgpad KERNEL DIM TOO LARGE"); 
			System.exit(0); 
		}

		int vecsz = row*col; 

		int uprow = (int) Math.floor(row/2);
		int dwnrow = (int) row - uprow - 1; 
		int lcol = (int) Math.floor(col/2); 
		int rcol = (int) col - lcol - 1; 

		int height1 = height+uprow+dwnrow; 
		int width1 = width+lcol+rcol; 
		int[][] imgpad = new int[height1][width1]; 

		int ind1; 
		int ind2; 
		int ind3; 
		int ind4; 
		int ind5; 

		for (int aa = 0; aa < height; aa++) { 
			ind1 = aa + uprow; 

			for (int bb = 0; bb < width; bb++) { 
				ind2 = bb + lcol; 
				imgpad[ind1][ind2] = imgin[aa][bb]; 
			}

			for (int cc = 0; cc < lcol; cc++) { 
				imgpad[ind1][cc] = imgin[aa][0];
			}

			for (int dd = 0; dd < rcol; dd++) { 
				ind3 = width1 - dd - 1; 
				imgpad[ind1][ind3] = imgin[aa][(width-1)]; 
			}
		}

		for (int ee = 0; ee < width; ee++) { 
			ind4 = ee + lcol; 

			for (int ff = 0; ff < uprow; ff++) { 
				imgpad[ff][ind4] = imgin[0][ee]; 
			}

			for (int gg = 0; gg < dwnrow; gg++) { 
				ind5 = height1 - gg - 1; 
				imgpad[ind5][ind4] = imgin[(height-1)][ee]; 
			}
		}

		for (int hh = 0; hh < uprow; hh++) { 
				for (int ii = 0; ii < lcol; ii++) { 
					imgpad[hh][ii] = imgin[0][0];
				}
			}

			int ind6; 
			for (int jj = 0; jj < uprow; jj++) { 
				for (int kk = 0; kk < rcol; kk++) { 
					ind6 = width1 - kk - 1; 
					imgpad[jj][ind6] = imgin[0][(width-1)]; 
				}
			}

			int ind7;
			for (int ll= 0; ll < dwnrow; ll++) { 
				ind7 = height1 - ll - 1; 
				for (int mm = 0; mm < lcol; mm++) { 
					imgpad[ind7][mm] = imgin[(height-1)][0]; 
				}
			}

			int ind8; 
			int ind9; 
			for (int nn=0; nn < dwnrow; nn++) { 
				ind8 = height1 - nn - 1; 
				for (int oo=0; oo < rcol; oo++) { 
					ind9 = width1 - oo - 1; 
					imgpad[ind8][ind9] = imgin[(height-1)][(width-1)]; 
				}
			}
		return imgpad; 	
	}