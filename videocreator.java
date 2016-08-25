	public boolean videocreator(int[][][] vidin, String dirname, String folder) { 
		int height = vidin.length; 
		int width = vidin[0].length; 
		int sz = vidin[0][0].length; 

		String newdir = dirname+"/Processed_Videos/"+folder; 
		File dir1 = new File(newdir); 
		boolean success = dir1.mkdirs(); 

		if (success == false) { 
			JOptionPane.showMessageDialog(null,"ERR: videocreator, Directory cannot be created"); 
			System.exit(0); 
		}

		String path = newdir+"/img";
		String num;  
		boolean success1; 
		int[][] dummyarr = new int[height][width]; 
		String path1; 

		for (int ii = 0; ii < sz; ii++) { 
			num = Integer.toString(ii); 
			path1 = path+num; 
			for (int jj = 0; jj < height; jj++) { 
				for (int kk = 0; kk < width; kk++) { 
					dummyarr[jj][kk] = vidin[jj][kk][ii]; 
				}
			}

			success1 = createimage(dummyarr,path1); 
		}

		return true; 
	}