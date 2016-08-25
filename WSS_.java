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

/* Things to Implement Later:
1. Check if Grayscale, needs to be grayscale
3. Run this code through all the images in the folder created in part 2 
4. Run multiple videos at one time 
5. Modify extractpixels to create a 3D array for image stacks as opposed to a 2D array 

*/


public class WSS_ implements PlugIn { 

	static File dir;  // for file selection
	
	public void run(String arg) { 
		IJ.register(WSS_.class);  //imagej setup

		String[] fileinfo = myfilechooser();   //file chooser
		String path = fileinfo[1]; 
		String dirname = fileinfo[0];  

		ImagePlus imp = new ImagePlus();  // define imagej image type

		imp = IJ.openImage(path);  // open image

		//optional 
		// imp.show();  // show image

		ImageProcessor ip = imp.getProcessor();   //extract image processor
		int width = ip.getWidth();  // get width of image
		int height = ip.getHeight(); // get height of image 

		int[][] img = extractpixels(ip,height,width);  // extract pixels from image as integer array
		int[][] img2 = medfilt2(img,3,3); // note that the window size is 3 
		
		int[][] img3 = enhanceIm(img2); 
		// int[][] img4 = im2bw(img3,0.25); 
		int[][] img5 = imgaussfilt(img3,5,7);

		String newpath = dirname+"/gaussfilt_sigma7"; 
		boolean success = createimage(img5,newpath);  
		// boolean excel = create_xls(dirname,img4); 

		JOptionPane.showMessageDialog(null, "DONE!"); 

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

	public int[][] extractpixels(ImageProcessor ip1, int height, int width) { 
		int[][] imagedata = new int[height][width]; 
		for (int iii = 0; iii < height; iii++){

			for (int jjj = 0; jjj < width; jjj++) { 
				imagedata[iii][jjj] = ip1.getPixel(jjj,iii); 
			}
		}
		return imagedata; 	
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

		double lrowspan1 = Math.floor((row*0.5)-0.5); 
		double rrowspan1 = row - lrowspan1 - 1;
		int lrowspan = (int) lrowspan1; 
		int rrowspan = (int) rrowspan1; 

		double lcolspan1 = Math.floor((col*0.5)-0.5); 
		double rcolspan1 = col - lcolspan1 - 1; 
		int lcolspan = (int) lcolspan1; 
		int rcolspan = (int) rcolspan1; 

		int vecsz = row*col; 
		int[] filtervec = new int[vecsz]; 
		int[][] filtermat = new int[row][col]; 
		int dummy1; 
		int dummy2; 
		double[] dummy4 = new double[2]; 
		double median1;
		int median;  
		int index; 

		// Finding the index for the median: 
		if ((vecsz % 2) == 1) {
			dummy4[0] = (vecsz-1)/2; 
			dummy4[1] = 0; 
		} else {
			dummy4[0] = Math.floor((vecsz-1)/2); 
			dummy4[1] = Math.ceil((vecsz-1)/2); 
		}

		int[] dummy3 = new int[2]; 
		dummy3[0] = (int) dummy4[0]; 
		dummy3[1] = (int) dummy4[1];

		for (int ii = 0; ii < height; ii ++) { 
			for (int jj = 0; jj < width; jj++) { 
				if (ii > (lrowspan-1) && ii < (height - rrowspan) && jj > (lcolspan -1) && jj < (width - rcolspan)) { 
					for (int kk = 0; kk < row; kk++) { 
						for (int ll = 0; ll < col; ll++) { 
							if (kk < rrowspan && ll < rcolspan) { 
								dummy1 = (kk-lrowspan) + ii; 
								dummy2 = (ll-lcolspan) + jj; 	
							} else if (kk > lrowspan && ll < rcolspan) {
								dummy1 = (kk-rrowspan) + ii; 
								dummy2 = (ll - lcolspan) + jj;
							} else if (kk < rrowspan && ll > lcolspan) {
								dummy1 = (kk-lrowspan) + ii; 
								dummy2 = (ll-rcolspan) + jj; 
							} else {
								dummy1 = (kk-rrowspan) + ii; 
								dummy2 = (ll-rcolspan) + jj; 
							}
							filtermat[kk][ll] = img1[dummy1][dummy2]; 	
						}
					}	
					for (int aa = 0; aa < row; aa++) { 
						for (int bb = 0; bb < col; bb ++) {
							index = (aa*row) + bb;
							filtervec[index] = filtermat[aa][bb]; 
						}
					}
					Arrays.sort(filtervec); 
					if ((vecsz % 2) == 1) {
						median = filtervec[dummy3[0]]; 
					} else {
						median1 = Math.floor((filtervec[dummy3[0]]+(filtervec[dummy3[1]])/2)); 
						median = (int) median1; 
					}
					img1filt[ii][jj] = median; 
				} else {
					img1filt[ii][jj] = img1[ii][jj]; 
				}
			} 
		}

		return img1filt; 

	}

	public int[][] enhanceIm(int[][] img) { 
		int[] counter = new int[256];
		int pixel;  
		int height = img.length; 
		int width = img[0].length;
	

		for (int j = 0; j < 256; j++) { 
			for (int col = 0; col < width; col++) { 
				for (int row = 0; row < height; row++) {
					pixel = img[row][col];
					
					if (pixel == j) { 
						counter[j] ++; 
					} 
				}
			}
		}

		int[] arr = new int[256]; 

		for (int i=0; i < 256; i++) { 
			arr[i] = counter[i]*i; 
		}

		int sum = 0; 
		int summ = 0; 

		for (int k = 0; k < 256; k++) { 
			sum += counter[k]*k; 
			summ += counter[k]; 
		}

		double mean = sum/summ; 

		double num = 0; 
		double den = 0; 

		for (int m = 0; m < 256; m++) { 
			num += counter[m]*Math.pow((m-mean),2);
			den += counter[m]; 
		}

		double stdev = Math.pow(num/(den-1),0.5); 

		double min = mean - stdev; 
		double max = mean;  

		double lowbound = min/255; 
		double highboudn = max/255; 

		int[][] scldimg = new int[height][width]; 
		int pixel1; 
		double newpix; 
		double m = 1/(max-min); 
		m = 255*m; 
		double b = 1 - (max/(max-min)); 
		b = 255*b; 

		for (int ii = 0; ii < height; ii++) { 
			for (int jj = 0; jj < width; jj++) { 
				pixel1 = img[ii][jj]; 
				if (pixel1 < (min+1)) { 
					scldimg[ii][jj] = 0; 
				}else if (pixel1 > (max-1)) { 
					scldimg[ii][jj] = 255; 
				} else { 
					newpix = m*pixel1 + b; 
					newpix = Math.round(newpix);
					scldimg[ii][jj] = (int) newpix;   
				}
			}
		}
		return scldimg; 
	}

	public int[][] im2bw(int[][] scldimg, double threshold) { 
		int height = scldimg.length; 
		int width = scldimg[0].length; 
		int[][] newimg = new int[height][width]; 

		double pxl; 

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

	public int[][] imgaussfilt(int[][] imgin, int kernelsz, double sigma) {
		int height = imgin.length; 
		int width = imgin[0].length; 
		int[][] imgout = new int[height][width];
		double[][] imgoutdummy = new double[height][width];  

		if (kernelsz < 3) { 
			kernelsz = 3; 
		}

		if (kernelsz > height || kernelsz > width) {
			JOptionPane.showMessageDialog(null, "KERNEL OUT OF BOUNDS");
			System.exit(0); 
		} 

		double dlspan; 
		dlspan = Math.floor((kernelsz*0.5) - 0.5); 
		double drspan = kernelsz - dlspan - 1; 
		int lspan = (int) dlspan; 
		int rspan = (int) drspan; 

		int vecsz = kernelsz*kernelsz; 
		int[] filtervec = new int[vecsz]; 
		int[][] filtermat = new int[kernelsz][kernelsz]; 
		int dummy1; 
		int dummy2; 
		double normalization = 0; 
		int index; 
		double xdist; 
		double ydist; 
		double[][] gaussmat = new double[kernelsz][kernelsz]; 
		double[] gaussvec = new double[vecsz];
		double denom = 2*Math.PI*Math.pow(sigma,2); 
		double powerdenom = 2*Math.pow(sigma,2); 
		double power; 
		double gaussnum; 
		double midgausspxl; 

		for (int ii = 0; ii < height; ii ++) { 
			for (int jj = 0; jj < width; jj++) { 
				if (ii > (lspan-1) && ii < (height - rspan) && jj > (lspan -1) && jj < (width - rspan)) { 
					for (int kk = 0; kk < kernelsz; kk++) { 
						for (int ll = 0; ll < kernelsz; ll++) { 
							if (kk < rspan && ll < lspan) { 
								dummy1 = (kk-lspan) + ii; 
								dummy2 = (ll-lspan) + jj; 	
							} else if (kk > lspan && ll < rspan) {
								dummy1 = (kk-rspan) + ii; 
								dummy2 = (ll - lspan) + jj;
							} else if (kk < rspan && ll > lspan) {
								dummy1 = (kk-lspan) + ii; 
								dummy2 = (ll-rspan) + jj; 
							} else {
								dummy1 = (kk-rspan) + ii; 
								dummy2 = (ll-rspan) + jj; 
							}
							xdist = (dummy1-ii)*(dummy1-ii); 
							ydist = (dummy2-jj)*(dummy2-jj); 
							power = (-1)*(xdist+ydist)/powerdenom;
							gaussnum = Math.exp(power)/denom; 
							filtermat[kk][ll] = imgin[dummy1][dummy2]; 	
							gaussmat[kk][ll] = gaussnum; 
						}
					}	

					for (int aa = 0; aa < kernelsz; aa++) { 
						for (int bb = 0; bb < kernelsz; bb ++) {
							index = (aa*kernelsz) + bb;
							filtervec[index] = filtermat[aa][bb]; 
							gaussvec[index] = gaussmat[aa][bb]; 
						}
					}
					normalization = 0; 
					for (int ff = 0; ff < vecsz; ff++) {
						normalization = normalization + filtervec[ff]*gaussvec[ff]; 
					} 
					midgausspxl = gaussmat[(lspan+1)][(lspan+1)]; 
					imgoutdummy[ii][jj] =  normalization;

				} else  {
					imgoutdummy[ii][jj] = imgin[ii][jj]; 
				}
			} 
		}

		double roundpxl; 
		for (int cc = 0; cc < height; cc++) { 
			for (int dd = 0; dd < width; dd++) { 
				roundpxl = Math.round(imgoutdummy[cc][dd]); 
				imgout[cc][dd] = (int) imgoutdummy[cc][dd]; 
			}
		}

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