	public int[][][] imagefunction(int[][][] imgin, String dirname) { 
		int height = imgin.length; 
		int width = imgin[0].length; 
		int sz = imgin[0][0].length; 

		int[][][] img1 = new int[height][width][sz];
		int[][][] adj = new int[height][width][sz];
		int[][][] blurr = new int[height][width][sz];
		int[][][] b = new int[height][width][sz];
		int[][][] pre_fg = new int[height][width][sz];
		int[][][] fg = new int[height][width][sz];
		int[][][] fMorph1 = new int[height][width][sz];
		int[][][] fMorph2 = new int[height][width][sz]; 

		int[][] dummyimg1 = new int[height][width]; 
		int[][] dummyimg2 = new int[height][width]; 
		int[][] dummyimg3 = new int[height][width]; 
		int[][] dummyimg4 = new int[height][width]; 
		int[][] dummyimg5 = new int[height][width]; 
		int[][] dummyimg6 = new int[height][width]; 
		int[][] dummyimg7 = new int[height][width]; 
		int[][] dummyimg8 = new int[height][width]; 

		for (int ii =0; ii < sz; ii++) { 
			for (int jj = 0; jj < height; jj++) { 
				for (int kk = 0; kk < width; kk++) { 
					dummyimg1[jj][kk] = imgin[jj][kk][ii]; 
				}
			}
			dummyimg1 = medfilt2(dummyimg1,3,3); 
			dummyimg2 = enhanceImg(dummyimg1); 
			dummyimg3 = imgaussfilt(dummyimg2,7); 
			dummyimg4 = enhanceImg(dummyimg3); 
			dummyimg5 = im2bw(dummyimg4,0.10); 
			dummyimg6 = medfilt2(dummyimg5,5,5); 
			dummyimg7 = imclose(dummyimg6,10); 
			dummyimg8 = imopen(dummyimg7,5); 

			for (int aa = 0; aa < height; aa++) { 
				for (int bb = 0; bb < width; bb++) {
					img1[aa][bb][ii] = dummyimg1[aa][bb]; 
					adj[aa][bb][ii] = dummyimg2[aa][bb];
					blurr[aa][bb][ii] = dummyimg3[aa][bb];
					b[aa][bb][ii] = dummyimg4[aa][bb];
					pre_fg[aa][bb][ii] = dummyimg5[aa][bb];
					fg[aa][bb][ii] = dummyimg6[aa][bb];
					fMorph1[aa][bb][ii] = dummyimg7[aa][bb];
					fMorph2[aa][bb][ii] = dummyimg8[aa][bb];
				}
			}
		}
		boolean success1 = videocreator(img1,dirname,"img1"); 
		boolean success2 = videocreator(adj,dirname,"adj"); 
		boolean success3 = videocreator(blurr,dirname,"blurr"); 
		boolean success4 = videocreator(b,dirname,"b"); 
		boolean success5 = videocreator(pre_fg,dirname,"pre_fg"); 
		boolean success6 = videocreator(fg,dirname,"fg"); 
		boolean success7 = videocreator(fMorph1,dirname,"fMorph1");
		boolean success8 = videocreator(fMorph2,dirname,"fMorph2");  
		return fMorph2; 
	}