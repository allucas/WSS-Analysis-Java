import ij.plugin.*;
import ij.*;
import ij.io.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.lang.*;
import java.awt.*;
import java.awt.image.*;
import java.text.DecimalFormat; 
import java.util.Arrays;
import java.util.*; 
import java.util.ArrayList;

import ij.plugin.AVI_Reader;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;

import javax.swing.JOptionPane;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import java.io.File;
import java.awt.List;
import java.lang.Math;  

public class bwboundaries_test implements PlugIn { 
	static File dir; 

	public void run(String arg) { 
		IJ.register(bwboundaries_test.class); 
		String[] fileinfo = myfilechooser(); 
		String path = fileinfo[1]; 
		String dirname = fileinfo[0];

		ImagePlus imp = new ImagePlus(); 

		imp = IJ.openImage(path); 
		int type = imp.getType(); 
		if (type != 0) { 
			JOptionPane.showMessageDialog(null,"REQUIRES 8 BIT IMAGE"); 
		}

		ImageProcessor ip = imp.getProcessor(); 
		int width = ip.getWidth(); 
		int height = ip.getHeight(); 
		
		int[][] img = imread(ip,height,width); 

		ArrayList<int[][]> edges = bwboundaries(img,dirname); 
		int[][] img_edge = contour_img(edges,height,width); 
		String path1 = dirname + "/contour_test";
		boolean save = createimage(img_edge,path1); 
		
		JOptionPane.showMessageDialog(null,"DONE!"); 
	}

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

	public int[][] imread(ImageProcessor ip1, int height, int width) { 
		int[][] imagedata = new int[height][width]; 
		for (int iii = 0; iii < height; iii++){

			for (int jjj = 0; jjj < width; jjj++) { 
				imagedata[iii][jjj] = ip1.getPixel(jjj,iii); 
			}
		}
		return imagedata; 	
	}

	public int[][][] conv2(int[][] imgin, int row, int col) { 

		int height = imgin.length;
		int width = imgin[0].length;

		if (row < 3) { 
			row = 3; 
		}

		if (col < 3) { 
			col = 3; 
		}

		if (row > height || col > width) { 
			JOptionPane.showMessageDialog(null,"KERNEL DIM TOO ERROR");
			System.exit(0); 
		}

		int vecsz = row*col; 
		int[][][] outconv = new int[height][width][vecsz]; 

		int uprow = (int) Math.floor(row/2); 
		int dwnrow = (int) row - uprow-1; 
		int lcol = (int) Math.floor(col/2); 
		int rcol = (int) col - lcol -1; 
		int[] rowvec = new int[row]; 
		int[] colvec = new int[col]; 

		for (int x = 0; x < row; x++) { 
			if (x == 0){ 
				rowvec[x] = -1*uprow; 
			} else { 
				rowvec[x] = rowvec[x-1] + 1; 
			}
		}

		for (int y = 0; y < col; y++) { 
			if (y == 0) { 
				colvec[y] = -1*lcol; 
			} else { 
				colvec[y] = colvec[y-1] + 1;  
			}
		}

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

			for(int dd = 0; dd < rcol ; dd++) { 
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

		int ind10; 
		int ind11; 
		int ind12;
		int ind13;
		int[][] filtermat = new int[row][col]; 
		int[] filtervec = new int[vecsz]; 
		int indvec;
		
		for (int pp = 0; pp < height; pp++) { 
			ind10 = pp + uprow; 
			for (int qq = 0; qq < width; qq++) { 
				ind11 = qq + lcol; 
				for (int rr = 0; rr < row; rr ++) { 
					ind12 = rowvec[rr] + ind10; 
					for (int ss = 0; ss < col; ss++) { 
						ind13 = colvec[ss] + ind11; 
						filtermat[rr][ss] = imgpad[ind12][ind13]; 
					}
				}
				for (int tt = 0; tt < row; tt++) {  
					for (int uu = 0; uu < col; uu++) { 
						indvec = (tt*col) + uu; 
						filtervec[indvec] = filtermat[tt][uu];
					}
				}
				for (int vv = 0; vv < vecsz; vv++) { 
					outconv[pp][qq][vv] = filtervec[vv]; 
				}

			} 
		}
		return outconv; 
	}	

	public ArrayList<int[][]> bwboundaries(int[][] imgin, String dirname) { 
		ArrayList<int[][]> coordinates = new ArrayList<int[][]>();

		int sz_arr = coordinates.size(); 

		int height = imgin.length; 
		int width = imgin[0].length; 

		int[][] imgpad = new int[height+2][width+2];

		int ind1; 
		int ind2; 

		for (int dd = 0; dd < height; dd++) { 
			ind1 = dd + 1; 
			imgpad[ind1][0] = 255; 
			imgpad[ind1][(width+1)] = 255; 
			for (int ee = 0; ee < width; ee++) { 
				ind2 = ee + 1; 
				imgpad[ind1][ind2] = imgin[dd][ee];
				imgpad[0][ind2] = 255; 
				imgpad[(height+1)][ind2] = 255;  
			}
		}

		imgpad[0][0] = 255; 
		imgpad[(height+1)][0] = 255; 
		imgpad[0][(width+1)] = 255; 
		imgpad[(height+1)][(width+1)] = 255; 

		int[][][] myconv = conv2(imgpad,3,3); 
		int sz = myconv[0][0].length; 

		int[][] pxlsum = new int[height][width]; 

		int dummy; 

		for (int aa = 0; aa < height; aa++) { 
			for (int bb = 0; bb < width; bb++) { 
				dummy = 0; 
				for (int cc = 0; cc < sz; cc++) { 
					dummy += myconv[aa][bb][cc]; 
				}
				pxlsum[aa][bb] = dummy; 
			}
		}

		int pxl; 
		int psum;  
		int xorig;
		int yorig; 
		int xbackorig; 
		int ybackorig; 
		int repeat; 
		int dummyx; 
		int dummyy; 
		int x = 0; 
		int y = 0; 

		for (int jj = 0; jj < width; jj++) { 
			for (int ii = (height-1); ii > -1; ii--) {
				pxl = imgpad[ii][jj]; 
				psum = pxlsum[ii][jj]; sz_arr = coordinates.size(); 
				if (pxl == 0 && psum > 0 && sz_arr == 0) { 
					xorig = jj; 
					yorig = ii; 
					x = xorig; 
					y = yorig; 
					xbackorig = xorig; 
					ybackorig = yorig - 1; 
					int[][] points = moore_trace(xorig,yorig,xbackorig,ybackorig,imgpad,myconv);
					coordinates.add(points);
					sz_arr = coordinates.size();  
				} else if (pxl==0 && psum > 0 && sz_arr > 0) { 
					repeat = 0; 
					for (int nn = 0; nn < sz_arr; nn++) { 
						int[][] dummypoints = coordinates.get(nn);
						for (int oo = 0; oo < dummypoints[0].length;oo++) { 
							dummyx = dummypoints[0][oo];
							dummyy = dummypoints[1][oo];
							if ((dummyx == jj) && (dummyy == ii)) { 
								repeat++; 
							}
						}
					}
					if (repeat == 0) { 
						xorig = jj;  
						yorig = ii; 
						xbackorig = x; 
						ybackorig = y; 
						int[][] points = moore_trace(xorig,yorig,xbackorig,ybackorig,imgpad,myconv); 
						coordinates.add(points);
						sz_arr = coordinates.size();  
					}
				}
			}
		}
		return coordinates; 
	}	

	public int[][] moore_trace(int xorig, int yorig, int xbackorig, int ybackorig, int[][] img, int[][][] myconv) { 
		int xback = -1; 
		int yback = -1; 
		int x = xorig; 
		int y = yorig; 
		int count = 0; 
		int sz = myconv[0][0].length; 
		int[] dummyarr = new int[sz];  
		int[] row = new int[]{-1,-1,-1,0,0,0,1,1,1};
		int[] col = new int[]{-1,0,1,-1,0,1,-1,0,1}; 
		int moorepix; 
		int rowind; 
		int colind; 
		ArrayList<Integer> xcoord = new ArrayList<Integer>(); 
		ArrayList<Integer> ycoord = new ArrayList<Integer>(); 
		xcoord.add(xorig); 
		ycoord.add(yorig);
		int[][] points = new int[2][];
		int cond; 
		int[] mooreN = new int[sz-1];
		int[] moorerow = new int[sz-1];
		int[] moorecol = new int[sz-1];
		
		while (xback != xbackorig && yback != ybackorig && x!=xorig && y != yorig) { 
			if (count == 0) { 
				xback = xbackorig; 
				yback = ybackorig; 
			}

			if (xback == x && yback == (y-1)) { 
				cond = 1; 
			} else if (xback == (x-1) && yback == y) { 
				cond = 2;
			} else if (xback == x && yback == (y+1)) { 
				cond = 3;
			} else { 
				cond = 4; 
			}

			for (int kk = 0; kk < sz; kk++) { 
				dummyarr[kk] = myconv[y][x][kk];
			}	
			mooreN = mooreneighbor(dummyarr,cond);
			moorerow = mooreneighbor(row,cond);
			moorecol = mooreneighbor(col,cond); 

			MOORELOOP : {
				for (int ll = 0; ll < mooreN.length; ll++) { 
					moorepix = mooreN[ll];
					rowind = moorerow[ll]+y; 
					colind = moorecol[ll]+x; 
					if (moorepix == 0) {
						count++; 
						x = colind; 
						y = rowind; 
						xcoord.add(x); 
						ycoord.add(y); 
						xback = moorecol[ll-1]+x; 
						yback = moorerow[ll-1]+y; 
						break MOORELOOP; 
					} 
				}
			}
		}
		int[] dummyx = new int[xcoord.size()];
		int[] dummyy = new int[ycoord.size()];
		for (int mm = 0; mm < xcoord.size(); mm++) { 
			dummyx[mm] = xcoord.get(mm); 
			dummyy[mm] = ycoord.get(mm);
		}
		points[0] = dummyx; 
		points[1] = dummyy; 
		return points; 
	}

	public int[] mooreneighbor(int[] myarr, int cond) { 
		
		int[] moore = new int[8]; 
		switch (cond) { 
			case 1: 
				moore[0] = myarr[7];
				moore[1] = myarr[6];
				moore[2] = myarr[3];
				moore[3] = myarr[0];
				moore[4] = myarr[1];
				moore[5] = myarr[2];
				moore[6] = myarr[5];
				moore[7] = myarr[8];
				break; 
			case 2: 
				moore[0] = myarr[3];
				moore[1] = myarr[0];
				moore[2] = myarr[1];
				moore[3] = myarr[2];
				moore[4] = myarr[5];
				moore[5] = myarr[8];
				moore[6] = myarr[7];
				moore[7] = myarr[6];
				break; 
			case 3:
				moore[0] = myarr[1];
				moore[1] = myarr[2];
				moore[2] = myarr[5];
				moore[3] = myarr[8];
				moore[4] = myarr[7];
				moore[5] = myarr[6];
				moore[6] = myarr[3];
				moore[7] = myarr[0];
				break; 						
			case 4: 
				moore[0] = myarr[5];
				moore[1] = myarr[8];
				moore[2] = myarr[7];
				moore[3] = myarr[6];
				moore[4] = myarr[3];
				moore[5] = myarr[0];
				moore[6] = myarr[1];
				moore[7] = myarr[2];
				break; 			
		}
		return moore; 
	}	

	public int[][] contour_img(ArrayList<int[][]> coordinates,int height, int width) { 
		int sz = coordinates.size(); 

		int[][] imgout = new int[height][width];
		for (int aa = 0; aa < height; aa++) {
			for (int bb = 0; bb < width; bb++) { 
				imgout[aa][bb] = 255; 
			}
		}

		int rowind; 
		int colind; 

		for (int ii = 0; ii < sz; ii++) { 
			int[][] dummyarr = coordinates.get(ii); 
			for (int jj = 0; jj < dummyarr[0].length;jj++) { 
				rowind = dummyarr[1][jj];
				colind = dummyarr[0][jj]; 
				imgout[rowind][colind] = 0; 
			}
		}

		return imgout; 
	}	

	public boolean createimage(int[][] img2, String path) { 
		int height = img2.length; 
		int width = img2[0].length; 
		int stacks = 1; 
 
		String title = "My Image";
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

}