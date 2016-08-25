	public boolean create_xls(String path, int[][] pxls) { 
		String savepath = path+"/im2bw_pxlarray"; 
		int height = pxls.length; 
		int width = pxls[0].length;


		try {
			WritableWorkbook workbook = Workbook.createWorkbook(new File(savepath+".xls")); 
			WritableSheet sheet = workbook.createSheet("First Sheet",0); 

			for (int ii = 0; ii < height; ii++) { 
				for (int jj = 0; jj < width; jj++) { 
					Number number = new Number(jj,ii,pxls[ii][jj]); 
					sheet.addCell(number); 
				}
			}
		workbook.write(); 
		workbook.close(); 
		return true; 		 
		} catch (Exception e) {
			return false; 
		} 
	}