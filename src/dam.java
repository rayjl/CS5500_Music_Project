/* 
 * Rapid Prototype
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class dam {

	public static void main(String[] args) {
		
		// Read wave files in to File objects
		// This section needs to be modified so that it can read/load \
		// the path name from the unix command
		File file1 = new File("/Users/Ray/Dropbox/Programming/"
				+ "CS5500/Team Unicorn/Music Project/"
				+ "wayfaring2.wav");
		File file2 = new File("/Users/Ray/Dropbox/Programming/"
				+ "CS5500/Team Unicorn/Music Project/"
				+ "wayfaring2.mp3");
		
		// Read files into byte arrays and compare to WAVE format
		try {
			byte[] b1 = getByteStream(file1);
			boolean waveCheck1 = fileFormatCheck(b1);
			System.out.println("File is in WAVE format : "
					+ waveCheck1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			byte[] b2 = getByteStream(file2);
			boolean waveCheck2 = fileFormatCheck(b2);
			System.out.println("File is in Wave format : "
					+ waveCheck2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	/* byte[] -> boolean
	 * Given: a byte array to check its format
	 * Returns: true if data is in wave format
	 * Note: this function will need to be modified for final
	 */
	private static boolean fileFormatCheck(byte[] b) {
		
	}
	
	/* File -> byte[]
	 * Given: a file that is read 
	 * Returns: the byte stream representation of the file
	 */
	private static byte[] getByteStream(File file) throws IOException {
		// Create FileInputStream from file argument
		FileInputStream fis = null;
		fis = new FileInputStream(file);
		System.out.println("File size read in bytes : "
				+ fis.available());
		
		// Read FileInputStream into byte array
		byte[] b = new byte[fis.available()];
		fis.read(b);
		fis.close();

		return b;
	}
	
}
