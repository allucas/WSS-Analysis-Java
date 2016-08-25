	public String[] myfilechooser() {
			JFileChooser fc = new JFileChooser();
			fc.setMultiSelectionEnabled(true);

			if (dir==null) {
				String sdir = OpenDialog.getDefaultDirectory(); 
				if (sdir != null)
					dir = new File(sdir); 
			}

			if (dir!=null)
				fc.setCurrentDirectory(dir); 

			int returnVal = fc.showOpenDialog(IJ.getInstance()); 
			if (returnVal!=JFileChooser.APPROVE_OPTION);
				File[] files = fc.getSelectedFiles(); 

			File filepath = fc.getSelectedFile();
			String path = filepath.getAbsolutePath(); 
			String dirname = filepath.getParent(); 
			String[] output = new String[2]; 
			output[0] = dirname; 
			output[1] = path; 

			return output; 
	}