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

public class WSS2_0 implements PlugIn { 
	static File dir; 

	public void run(String arg) { 
		String[] fileinfo = myfilechooser(); 
		String path = fileinfo[1]; 
		String dirname = fileinfo[0]; 
		ImageStack mystack = new ImageStack(); 
		AVI_Reader myreader = new AVI_Reader(); 

		mystack = myreader.makeStack(path,1,0,false,true,false);  

		int[][][] img = videoReader(mystack); 
		
		int[][][] img1 = imagefunction(img,dirname); 

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

	public int[][][] videoReader(ImageStack imp1) { 
		int height = imp1.getHeight(); 
		int width = imp1.getWidth(); 
		int sz = imp1.getSize(); 

		int[][][] imgout = new int[height][width][sz]; 

		int[][] dummyarr = new int[height][width]; 

		int ind1; 
		for (int nn = 1; nn <= sz; nn++) { 
			ImageProcessor myip = imp1.getProcessor(nn); 
			dummyarr = imread(myip,height,width); 
			ind1 = nn -1; 
			for (int ii = 0; ii < height; ii++) { 
				for (int jj = 0; jj < width; jj++) { 
					imgout[ii][jj][ind1] = dummyarr[ii][jj]; 
				}
			}
		}
		return imgout; 	
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

	public int[][] medfilt2(int[][] img1,int row, int col) { 
		int height = img1.length; 
		int width = img1[0].length; 
		int[][] img1filt = new int[height][width];

		if (row< 3) { 
			row = 3; 
		}

		if (col < 3) { 
			col = 3; 

		}

		if (row > height || col > width) { 
			JOptionPane.showMessageDialog(null, "KERNEL OUT OF BOUNDS");
			System.exit(0); 
		}		

		int vecsz = row*col; 
		int[][][] myconvolution = conv2(img1,row,col);

		int newpxl; 
		int[] dummyarr = new int[vecsz]; 
		int[] dummy = new int[2];
		int median; 
		int leftmed; 
		int rightmed; 


		if ((vecsz % 2) == 1) {
			dummy[0] = (int) (vecsz-1)/2; 
			dummy[1] = 0; 
		} else {
			dummy[0] = (int) Math.floor((vecsz-1)/2); 
			dummy[1] = (int) Math.ceil((vecsz-1)/2); 
		}
				
		for (int ii = 0 ; ii < height; ii ++) { 
			for (int jj = 0; jj < width; jj++) { 
				for (int kk = 0; kk < vecsz; kk++) { 
					dummyarr[kk] = myconvolution[ii][jj][kk]; 
				}
				Arrays.sort(dummyarr); 
				if ((vecsz % 2) == 1) { 
					median = (int) dummyarr[dummy[0]];
				} else {	
					leftmed = (int) dummyarr[dummy[0]];
					rightmed = (int) dummyarr[dummy[1]];
					median = (int) Math.floor((leftmed/2)+(rightmed/2));
				}
				img1filt[ii][jj] = median;
			}
		}
		return img1filt; 
	}	

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

	public int[] imhist(int[][] img) { 
		int[] counter = new int[256]; 
		int pixel; 
		int height = img.length; 
		int width = img[0].length; 

		for (int j = 0; j < 256; j++) { 
			for (int col = 0; col < width; col++) { 
				for (int row = 0; row < height; row++) { 
					pixel = img[row][col]; 

					if (pixel == j) { 
						counter[j]++;
					}
				}
			}
		}
		return counter; 
	}

	public int[][] imadjust(int[][] img, double minvalin, double maxvalin, double minvalout, double maxvalout) { 

		if (minvalin < 0 || minvalin > 1 || maxvalin < 0 || maxvalout > 1 || minvalout < 0 || minvalout > 1 || maxvalout < 0 || maxvalin > 1) { 
			JOptionPane.showMessageDialog(null,"ERR: imadjust INPUTS MUST BE BETWEEN 0 and 1");
			System.exit(0);
		}
		minvalin = 255*minvalin; 
		maxvalin = 255*maxvalin; 
		minvalout = 255*minvalout; 
		maxvalout = 255*maxvalout; 

		int height = img.length; 
		int width = img[0].length; 

		double slope = (maxvalout-minvalout)/(maxvalin-minvalin); 
		double intercept = (maxvalout) - (slope*maxvalin); 

		int pixel; 
		double newpix; 
		int[][] scldimg = new int[height][width]; 
		 
		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				pixel = img[ii][jj]; 

				if (pixel < minvalin) { 
					scldimg[ii][jj] = (int) Math.round((minvalout)); 
				} else if (pixel > (maxvalin)) { 
					scldimg[ii][jj] = (int) Math.round((maxvalout));
				} else { 
					newpix = slope*(pixel) + intercept; 
					scldimg[ii][jj] = (int) Math.round(newpix); 
				}
			}
		}

		return scldimg; 
	}

	public int[][] im2bw(int[][] scldimg, double threshold) { 
		int height = scldimg.length; 
		int width = scldimg[0].length; 
		int[][] newimg = new int[height][width]; 
		threshold = threshold*255; 

		int pxl; 

		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				pxl = scldimg[ii][jj]; 

				if (pxl > threshold) { 
					newimg[ii][jj] = 255; 				
				} else { 
					newimg[ii][jj] = 0; 
				}
			}

		}

		return newimg; 
	}

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

	public int[][] erosion(int[][][] struc_element, int rad) { 
		int height = struc_element.length; 
		int width = struc_element[0].length; 
		int vecsz = struc_element[0][0].length; 
		int[][] imgout = new int[height][width]; 

		int[] dummyarr = new int[vecsz]; 

		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				for (int kk = 0; kk < vecsz; kk++) { 
					dummyarr[kk] = struc_element[ii][jj][kk];
				}
				Arrays.sort(dummyarr); 
				imgout[ii][jj] = dummyarr[0]; 
			}
		}

		return imgout; 
	}

	public int[][] dilation(int[][][] struc_element, int rad) { 
		int height = struc_element.length; 
		int width = struc_element[0].length; 
		int vecsz = struc_element[0][0].length; 
		int[][] imgout = new int[height][width]; 

		int[] dummyarr = new int[vecsz]; 

		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				for (int kk = 0; kk < vecsz; kk++) { 
					dummyarr[kk] = struc_element[ii][jj][kk];
				}
				Arrays.sort(dummyarr); 
				imgout[ii][jj] = dummyarr[(vecsz-1)]; 
			}
		}

		return imgout; 	
	}

	public int[][] imclose(int[][] imgin, int rad) {

		int height = imgin.length; 
		int width = imgin[0].length;
		double stoph = height*0.5; 
		double stopw = width*0.5; 

		if (rad < 1) { 
			rad = 1;
		} else if (rad > stoph || rad > stopw) { 
			JOptionPane.showMessageDialog(null,"ERR: imclose RADIUS TOO LARGE"); 
			System.exit(0); 
		}

		int[][][] my_strel = strel_disk(imgin,rad); 

		int[][] imgout = dilation(my_strel,rad); 

		int[][][] my_strel2 = strel_disk(imgout,rad); 

		imgout = erosion(my_strel2,rad); 

		return imgout; 
	}

	public int[][] imopen(int[][] imgin, int rad) {

		int height = imgin.length; 
		int width = imgin[0].length; 
		double stoph = height*0.5; 
		double stopw = width*0.5;  

		if (rad < 1) { 
			rad = 1;
		} else if (rad > stoph || rad > stopw) { 
			JOptionPane.showMessageDialog(null,"ERR: imclose RADIUS TOO LARGE"); 
			System.exit(0); 
		}

		int[][][] my_strel = strel_disk(imgin,rad); 

		int[][] imgout = erosion(my_strel,rad); 

		int[][][] my_strel2 = strel_disk(imgout,rad); 

		imgout = dilation(my_strel2,rad); 

		return imgout; 
	}

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

}