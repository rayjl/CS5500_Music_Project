import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Rapid Prototype
 * This prototype will need refactoring, especially with
 * managing the files to compare.
 * Current prototype has code that manually compares 2 files
 * 
 * Last Edited: 19 October 2014
 */

public class AudioMatching {

	// Global variables
	private static String file_name1;
	private static String file_name2;
	private static boolean match;
	
	public static void main(String[] args) {
		
		// Read wave files in to File objects
		// 2 Arguments should be passed in from shell script
		// Format: 
		// -f <pathname> -f <pathname>
		if (args.length != 2) {
			System.err.println("ERROR");
			System.exit(1);
		}
		
		// Compare songs
		CompareFiles(args[0], args[1]);
		
		// Report results and exit program
		summaryReport(match);
		System.exit(0);
	}
	
	/* boolean -> Void
	 * Given: true if the files match, false otherwise
	 * Returns: Void
	 */
	private static void summaryReport(boolean b) {
		if (b)
			System.out.println("MATCH" + " " 
					+ file_name1 + " " + file_name2);
		else
			System.out.println("NO MATCH");
	}
	
	/* String String -> Void
	 * Given: 2 file paths passed into Java application
	 * Returns: Void
	 * Note: this function is the main operation
	 */
	private static void CompareFiles(String path1, String path2) {
		
		// Grab file names from paths
		file_name1 = shortFileName(path1);
		file_name2 = shortFileName(path2);
				
		// Create file objects from file paths
		File file1 = new File(path1);
		File file2 = new File(path2);
				
		// Read files into byte arrays and compare to WAVE format
		// Each index contains decimal value stored in corresponding
		// byte offset
		try {
					
			byte[] b1 = getByteArray(file1);
			byte[] b2 = getByteArray(file2);
			AudioFile af1 = new AudioFile(b1);
			AudioFile af2 = new AudioFile(b2);
					
			// Set parameters for AudioFile
			setAudioFileParams(af1);
			setAudioFileParams(af2);
					
			// Assignment 5 Format Check
			fileFormatCheck(af1, file_name1);
			fileFormatCheck(af2, file_name2);
					
			// Test - Sample Rate output - 44100
			System.out.println(convertLittleEndian(b1,24,4));
			
			// Convert data to Little-Endian Form
			int[] audio_data1 = littleEndianToInt(b1);
			int[] audio_data2 = littleEndianToInt(b2);
			
			// Hanning Window before application of FFT
			// In-place mutator
			hanningWindow(audio_data1);
			hanningWindow(audio_data2);

			// Use FFT Implementation
			FFT fft1 = new FFT(audio_data1.length);
			FFT fft2 = new FFT(audio_data2.length);

			// Create double arrays to utilize FFT implementation
			double[] real1 = new double[audio_data1.length];
			double[] imag1 = new double[audio_data1.length];
			
			double[] real2 = new double[audio_data1.length];
			double[] imag2 = new double[audio_data2.length];
					
			// Iterate through to fill arrays before passing
			// to FFT function
			for (int i = 0; i < audio_data1.length; i++) {
				real1[i] = (double) audio_data1[i];
				imag1[i] = 0;
			}
					
			for (int i = 0; i < audio_data2.length; i++) {
				real2[i] = (double) audio_data2[i];
				imag2[i] = 0;
			}
				
			// In-place FFT
			fft1.fft(real1, imag1);
			fft2.fft(real2, imag2);
			
			// Convert to FingerPrints
			FingerPrint[] fp1 =
					makeFingerPrints(real1, imag1);
			FingerPrint[] fp2 =
					makeFingerPrints(real2, imag2);
					
			// Compare FingerPrints
			compareFingerPrints(fp1, fp2);
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/* double[] -> Void
	 * Given: sample array
	 * Returns: Void
	 * Note: in-place mutator
	 */
	private static void hanningWindow(int[] sample) {
		// Iterate through the sample with the hanning window function
		// Needs to be tested. Perhaps plotting the data would be helpful
		double multiplier;
		for (int i = 0; i < sample.length; i++) {
			multiplier = 0.5 
			* (1.0 + Math.cos(2.0 * Math.PI * i / sample.length));
			sample[i] = (int) (sample[i] * multiplier);
		}
	}
	
	/* byte[] -> int[]
	 * Given: the byte stream data of an audio file
	 * Returns: the int form of the file 
	 * Note: The entire byte stream stream is passed in but
	 * only the left channel of the data chunk will be returned
	 */
	private static int[] littleEndianToInt(byte[] b) {
		// First data sample begins at offset 44
		// Sample size is 4 bytes
		// Left channel is first 2 bytes of 4
		int dataLength = (b.length - 44) / 4 / 2;
		
		int[] sample = new int[dataLength];
		
		// Counter to iterate across byte[]
		int offset = 44;
		
		// add converted data to sample[]
		for (int i = 0; i < sample.length; i++) {
			sample[i] = convertLittleEndian(b, offset, 2);
			offset += 4;
		}
		
		return sample;
	}
	
	/* byte[] int int -> int
	 * Given: the byte array, the offset location, size of the chunk
	 * Returns: the int value form of the little-endian chunk
	 * Note: helper function for littleEndianToInt
	 */
	private static int convertLittleEndian(byte[] b,
			int offset, int size) {
		// value to return
		// will add to it based on bitwise operations
		int sample = 0;
		
		for (int i = 0; i < size; i++) {
			// temp value for each byte of data
			int val = 0;
			
			// 2's complement value is negative
			// Use bitwise operations to convert to binary
			if (b[offset + i] < 0) {
				// remove negative value
				val = b[offset + i] * -1;
				
				// reverse bits
				val = val ^ 0xFF;
				
				// add 1 to bits
				val = val + 1;
			}
			else {
				// set value to current byte
				val = b[offset + i] & 0xFF;
			}
			
			// add sample shifted i bytes to the left
			// accordingly
			sample = (val << 8*i) + sample;
		}
		
		// Return int value of the little-endian chunk
		return sample;
	}
	
	/* byte[] -> ComplexNumber[]
	 * Given: array of bytes
	 * Returns: corresponding array of complex numbers
	 * Notes: this method CAN be used but is currently not used
	 */
	private static ComplexNumber[] convertToComplexNumber(byte[] a) {
		int N = a.length;
		
		ComplexNumber[] complexArray = new ComplexNumber[N];
		
		for(int i=0; i<N; i++){
			complexArray[i] = new ComplexNumber((double) a[i], 0);
		}
		
		return complexArray;	
	}

	/* double[] double[] -> FingerPrint[]
	 * Given: the real and imag part of the the sample data after FFT
	 * Returns: a FingerPrint[] that contains the FingerPrints 
	 * of the samples
	 * 
	 * TODO - This method needs to be actually made, that is we need a 
	 * finger printing routine for our FFT data
	 */
	private static FingerPrint[] 
			makeFingerPrints(double[] real, double[] imag) {
		
		
		
		FingerPrint[] f = new FingerPrint[real.length];
		
		return f;
	}
	
	/* FingerPrint[] FingerPrint[] int -> Void
	 * Given: 2 FingerPrint arrays to compare and a threshold
	 * Returns: Void
	 * Note: function can set the variable match to be true
	 */
	private static void compareFingerPrints(FingerPrint[] a,
			FingerPrint[] b) {
	
		// trivial case, different song lengths = different songs
		// return; default match = false
		if (a.length != b.length)
			return;
		
		// If 90% of the array is similar, match found
		int counter = 0;
		int goal = (int) Math.floor(a.length * 0.90);
		
		for (int i = 0; i < a.length; i++) {
			if (a[i].similarTo(b[i]))
				counter++;
		}
		
		// threshold met
		if (counter >= goal)
			match = true;
		
	}

	/* String -> String
	 * Given: the path name of a file
	 * Returns: the short name of the file
	 * Note: the name of the file is contained in the path name
	 */
	private static String shortFileName(String s) {
		// temp string to concatenate file name to
		String temp = "";
		
		// Loop through path from end until '/' is detected
		for (int i = s.length() - 1; i >= 0; i--) {
			char c = s.charAt(i);
			if (c == '/')
				break;
			else
				temp = c + temp;
		}
		
		// Return the concatenated string representing
		// the file name
		return temp;	
	}
	
	/* AudioFile -> Void
	 * Given: an AudioFile object to have its fields set
	 * Returns: Void
	 * Notes: This is where all the logic goes to check
	 * what type of file this is. We will only check if it
	 * is in WAVE format for Assignment 5 and set the
	 * fields in the AudioFile object accordingly.
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

	}
	
	/* AudioFile String -> Void
	 * Given: an AudioFile object to check its format and the name
	 * of the respective file
	 * Returns: Void
	 * 
	 * Note: this method will need to be modified for final
	 */
	private static void fileFormatCheck(AudioFile af, String s) {
		if (af.getFormat() != Format.WAVE) {
			System.err.println("ERROR : " + s + " " 
					+ "is not a supported format.");
			System.exit(1);
		}
	}
	
	/* File -> byte[]
	 * Given: a file that is read 
	 * Returns: the byte stream representation of the file
	 */
	private static byte[] getByteArray(File file) throws IOException {
		// Create FileInputStream from file argument
		FileInputStream fis = null;
		fis = new FileInputStream(file);
		
		// Read bytes from buffer
		byte[] b = new byte[fis.available()];
		fis.read(b);
		fis.close();

		return b;
	}	
	
}
