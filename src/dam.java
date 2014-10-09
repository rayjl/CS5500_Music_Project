/* 
 * Rapid Prototype
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class dam {

	public static void main(String[] args) {

		// FileInputStream, get songs to compare header
		// use header to check if both songs are wave file format
		// --what is the input data format in?
		// --endianness of header?
		// WAVE -> 0x57415645 <- HEX
		
		// Read wave files in to File objects
		// This section needs to be modified so that it can read/load \
		// the path name from the unix command
		File file1 = new File("/Users/Ray/Dropbox/Programming/"
				+ "CS5500/Team Unicorn/Music Project/"
				+ "wayfaring2.wav");
		File file2 = new File("/Users/Ray/Dropbox/Programming/"
				+ "CS5500/Team Unicorn/Music Project/"
				+ "wayfaring2.mp3");
		
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		
		try {
			fis1 = new FileInputStream(file1);
			System.out.println("File 1 size read in bytes : "
					+ fis1.available());
			
			fis2 = new FileInputStream(file2);
			System.out.println("File 2 size read in bytes : "
					+ fis2.available());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		// Time similarity? 2-3 sec precision
		// Fingerprinting to match frequencies
		
	}
	
	/* FingerPrint FingerPrint -> boolean
	 * Given: 2 FingerPrints to compare
	 * Returns: true if the FingerPrints match
	 */
//	public static boolean compareFingerPrints(FingerPrint f1,
//			FingerPrint f2) {		
		//TO DO
//	}
	
	// This method may be removed
	// Check use of FileInputStream in main method
	/* bitstream -> int[]
	 * Given: the bit stream of an audio file
	 * Returns: the bitstream represented as an int array
	 */
//	public static int[] getBitStream(...) {
		//TO DO	
//	}
	
}
