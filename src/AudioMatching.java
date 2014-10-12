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
		// This section needs to be modified so that it can read/load
		// the path name from the unix command
		File file1 = new File("/Users/Ray/Dropbox/Programming/"
				+ "CS5500/Team Unicorn/Music Project/"
				+ "wayfaring2.wav");
		File file2 = new File("/Users/Ray/Dropbox/Programming/"
				+ "CS5500/Team Unicorn/Music Project/"
				+ "wayfaring2.mp3");
		
		// Read files into byte arrays and compare to WAVE format
		// Each index contains decimal value stored in corresponding
		// byte offset
		try {
			
			byte[] b1 = getByteStream(file1);
			AudioFile af1 = new AudioFile(b1);
			
			// Set parameters for AudioFile
			setAudioFileParams(af1);
			
			// Assignment 5 Check
			boolean waveCheck1 = fileFormatCheck(af1);
			System.out.println("File is in WAVE format : "
					+ waveCheck1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			byte[] b2 = getByteStream(file2);
			AudioFile af2 = new AudioFile(b2);
			
			// Set parameters for AudioFile
			setAudioFileParams(af2);
			
			// Assignment 5 Check
			boolean waveCheck2 = fileFormatCheck(af2);
			System.out.println("File is in Wave format : "
					+ waveCheck2);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	/* AudioFile -> Void
	 * Given: an AudioFile object to have its fields set
	 * Returns: Void
	 * Notes: This is where all the logic goes to check
	 * what type of file this is. We will only check if it
	 * is in WAVE format for Assignment 5 and set the
	 * fields in the AudioFile object accordingly.
	 * Values in the conditionals were manually looked up
	 * via an ascii table. May need to fix that.
	 * 
	 * This method will need to be fixed for the final. 
	 */
	private static void setAudioFileParams(AudioFile af) {
		// Grab the byte array from the object
		// Values in array are decimal format
		byte[] data = af.getData();
		
		// RIFF scan - offset 0 size 4
		if (data[0] == 82 && data[1] == 73
				&& data[2] == 70 && data[3] == 70)
			af.setRIFFval(true);
		
		// File Format - offset 8 size 4
		if (data[8] == 87 && data[9] == 65
				&& data[10] == 86 && data[11] == 69)
			af.setFormat("WAVE");
		
		// Audio Format - offset 20 size 2
		af.setCompression(data[20]);
		
		// Number of Channels - offset 22 size 2
		af.setChannels(data[22]);
		
		// Bits per Sample - offset 34 size 2
		af.setBPS(data[34]);
		
		// Sample Rate - offset 24 size 4
		// TODO - why does data[25] return a negative?

		
		for (int i = 0; i < 45; i++) {
			System.out.print((char)data[i] + " ");
			System.out.print(i + " ");
			System.out.println(data[i]);
		}
	}
	
	/* AudioFile -> boolean
	 * Given: an AudioFile object to check its format
	 * Returns: true if data is in wave format
	 * 
	 * Note: this method will need to be modified for final
	 */
	private static boolean fileFormatCheck(AudioFile af) {
		if (af.getFormat() == "WAVE")
			return true;
		else
			return false;
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
		
		// Read bytes from buffer
		byte[] b = new byte[fis.available()];
		fis.read(b);
		fis.close();

		return b;
	}
	
}
