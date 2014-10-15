/* 
 * Rapid Prototype
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioMatching {

	static String file_name1;
	static String file_name2;
	
	public static void main(String[] args) {
		
		// Leave this here for now as a sanity check for shell
		System.out.println(args[0]);
		System.out.println(args[1]);
		System.out.println(args.length);
		
		// Read wave files in to File objects
		// 2 Arguments should be passed in from shell script
		// -f <pathname> -f <pathname>
		if (args.length != 2) {
			System.err.println("ERROR");
			System.exit(1);
		}
		
		// Grab the paths from respective indexes
		String path1 = args[0];
		String path2 = args[1];
		
		file_name1 = shortFileName(path1);
		file_name2 = shortFileName(path2);
		
		File file1 = new File(path1);
		File file2 = new File(path2);
		
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
		if ((char)data[0] == 'R' 
				&& (char)data[1] == 'I'
				&& (char)data[2] == 'F' 
				&& (char)data[3] == 'F')
			af.setRIFFval(true);
		
		// File Format - offset 8 size 4
		if ((char)data[8] == 'W' 
				&& (char)data[9] == 'A'
				&& (char)data[10] == 'V' 
				&& (char)data[11] == 'E')
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
