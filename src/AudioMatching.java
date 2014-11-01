import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Rapid Prototype
 * This prototype will need re-factoring, especially with
 * managing the files to compare.
 * WAVE file format is the conical for to be used.
 * 
 * Last Edited: 31 October 2014
 */

public class AudioMatching {

	// Global variables
	private static String file_name1;
	private static String file_name2;
	private static String path1;
	private static String path2;
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
		
		// Initialize paths from the params
		path1 = args[0];
		path2 = args[1];
		
		// Compare songs
		CompareFiles(path1, path2);
		
		// Report results and exit program
		summaryReport(match);
		System.exit(0);
	}
	
	/* boolean -> Void
	 * Given: true if the files match, false otherwise
	 * Returns: Void
	 * Note: only prints to System.out if files match
	 */
	private static void summaryReport(boolean b) {
		if (b)
			System.out.println("MATCH" + " " 
					+ file_name1 + " " + file_name2);
		else
			return;
	}
	
	/* String String -> Void
	 * Given: 2 file paths passed into Java application
	 * Returns: Void
	 * Note: this function is the main operation
	 */
	private static void CompareFiles(String p1, String p2) {
		
		// Grab file names from paths
		file_name1 = shortFileName(p1);
		file_name2 = shortFileName(p2);
		
		// Create file objects from file paths
		File file1 = new File(path1);
		File file2 = new File(path2);
				
		// Read files into byte arrays and compare to 
		// WAVE and MP3 formats
		// Each index contains decimal value stored in corresponding
		// byte offset
		try {
					
			byte[] b1 = getByteArray(file1);
			byte[] b2 = getByteArray(file2);
			AudioFile af1 = new AudioFile(b1, file_name1, path1);
			AudioFile af2 = new AudioFile(b2, file_name2, path2);
					
			// Set parameters for AudioFile
			// Method includes a file extension check
			setAudioFileParams(af1);
			setAudioFileParams(af2);
					
			// Format Check for Data
			fileFormatCheck(af1);
			fileFormatCheck(af2);
			
			// Convert mp3 file type to wave format
			convertToWave(af1);
			convertToWave(af2);
			
			// Convert little-endian data to int format
			int[] audio_data1 = littleEndianToInt(af1.getData());
			int[] audio_data2 = littleEndianToInt(af2.getData());
			
			// Hanning Window before application of FFT
			// In-place mutator
			hanningWindow(audio_data1);
			hanningWindow(audio_data2);
			
			// Get the next largest power of 2 from the sample
			// array lengths
			// This is necessary for FFT usage
			int nextPOT1 = nextPowerOfTwo(audio_data1.length);
			int nextPOT2 = nextPowerOfTwo(audio_data2.length);
			
			// Create double arrays for FFT implementation usage
			double[] real1 = new double[nextPOT1];
			double[] imag1 = new double[nextPOT1];
			
			double[] real2 = new double[nextPOT2];
			double[] imag2 = new double[nextPOT2];
			
			// In-place modification
			makeFFTAble(audio_data1, real1, imag1);
			makeFFTAble(audio_data2, real2, imag2);
			
			// Use FFT Implementation
			FFT fft1 = new FFT(nextPOT1);
			FFT fft2 = new FFT(nextPOT2);
				
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
	
	/* AudioFile -> Void
	 * Given: an AudioFile to be converted to WAVE format
	 * Returns: Void
	 * Notes: in-place mutator
	 */
	private static void convertToWave(AudioFile af) {
		// File is already in WAVE format
		if (af.getFormat() == Format.WAVE)
			return;
		else {
			// Call helper to convert non-wave file to wave format
			String destPath = convertToWaveHelper(af);

			// Load created temp file into buffer and extract byte data
			File tempFile = new File(destPath);
			tempFile.exists();
			tempFile.canRead();
			System.out.println(tempFile.getAbsolutePath());
			
			try {
				byte[] tempByte = getByteArray(tempFile);
				// Overwrite current AudioFile object data
				af.setData(tempByte);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			// Remove file created in tmp
			ProcessBuilder pb = new ProcessBuilder("rm", "/tmp/temp.wav");
			try {
				Process p = pb.start();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	/* AudioFile -> String
	 * Given: an AudioFile object to convert to wave format
	 * Returns: the String of the destination temp file path
	 */
	private static String convertToWaveHelper(AudioFile af) {
		// Command to execute LAME application in CCIS box
		String command = "/course/cs4500f14/bin/lame";
		String op = "--decode";
		String sourcePath = af.getPath();
		String destPath = "/tmp/temp.wav";
		
		// Execute file conversion with ProcessBuilder
		ProcessBuilder pb = new ProcessBuilder(command, op, 
				sourcePath, destPath);
		try {
			Process p = pb.start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// Return the file path of the temp file
		return destPath;
	}
	
	/* int[] double[] double[] -> Void
	 * Given: sample array and 2 empty double arrays to be filled
	 * to ready for FFT use
	 * Returns: Void
	 * Notes: In-place mutator ; does not need to be modified
	 */
	private static void makeFFTAble(int[] sample, 
			double[] real, double[] imag) {
		
		// Iterate through to fill arrays
		for (int i = 0; i < real.length; i++) {
			// Step exceeds sample range,
			// fill with 0 value
			if (i >= sample.length)
				real[i] = 0;
			// Step is within sample range
			else
				real[i] = (double) sample[i];
			imag[i] = 0;
		}
		
	}
	
	/* int -> int
	 * Given: length of the sample array
	 * Returns: the next power of 2 value from the length
	 * Notes: Fixed - this function does not need to be modified
	 */
	private static int nextPowerOfTwo(int len) {
		// Using Log of the number to calculate
		// Round the 'height' up and computer 2^height
		double pow = Math.ceil(Math.log(len) / Math.log(2));
		int val = (int) Math.pow(2, pow);
		
		// return the calculated next power of 2 value
		return val;
	}
	
	/* int[] -> Void
	 * Given: sample array
	 * Returns: Void
	 * Note: in-place mutator
	 * This is a frame of data.
	 */
	private static void hanningWindow(int[] sample) {
		// Iterate through the sample with the hanning window function
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
		int dataLength = (b.length - 44) / 4;
		
		int[] sample = new int[dataLength];
		
		// Counter to iterate across byte[]
		int offset = 44;
		
		// add converted data to sample[]
		for (int i = 0; i < sample.length; i++) {
			sample[i] = convertLittleEndian(b, offset, 2, true);
			offset += 4;
		}
		
		return sample;
	}
	
	/* byte[] int int boolean -> int
	 * Given: the byte array, the offset location, size of the chunk
	 * @param bool : true - little-endian / false - big-endian
	 * Returns: the int value form of the little-endian chunk
	 * Note: helper function for littleEndianToInt
	 * This can be used for BigEndian to int conversion
	 */
	private static int convertLittleEndian(byte[] b,
			int offset, int size, boolean bool) {
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
			
			// set bits based on endian-ness
			if (bool)
				sample = (val << 8*i) + sample;
			else
				sample = (sample << 8*i) + val;
		}
		
		// Return int value of the little-endian chunk
		return sample;
	}

	/* double[] double[] -> FingerPrint[]
	 * Given: the real and imag part of the the sample data after FFT
	 * Returns: a FingerPrint[] that contains the FingerPrints 
	 * of the samples
	 * 
	 * TODO - 
	 * See FingerPrint Object for method of "finger printing"
	 * Currently using power density of signal as a finger print
	 * Will need a more robust approach
	 */
	private static FingerPrint[] 
			makeFingerPrints(double[] real, double[] imag) {
		// Load data into a FingerPrint[]
		FingerPrint[] f = new FingerPrint[real.length];
		
		// Just looping through
		for (int i = 0; i < real.length; i++) {
			f[i] = new FingerPrint(real[i], imag[i]);
		}
		
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
		
		// Iterate through and check each FingerPrint
		for (int i = 0; i < a.length; i++) {
			
			// Print statement for visual check
//			if (i < 21)
//				System.out.println(Math.abs(a[i].getPowerDensity() 
//						- b[i].getPowerDensity()));
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
	 * This function is fixed - does not need to be modified
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
		// the file name with n characters in O(n^2) time
		return temp;	
	}
	
	/* AudioFile -> Void
	 * Given: an AudioFile object to have its fields set
	 * Returns: Void
	 * Notes: calls helper methods based on file extension
	 */
	private static void setAudioFileParams(AudioFile af) {
		// Get the file extension of the file
		String fileExtension = getFileExtension(af.getFileName());
		
		// Check file extensions - .wav .mp3 or .ogg
		// Set parameters based on extension
		if (fileExtension.equals(".wav"))
			setWaveFileParams(af);
		else if (fileExtension.equals(".mp3"))
			setMP3FileParams(af);
		else if (fileExtension.equals(".ogg"))
			setOGGFileParams(af);
		else
			error(1, fileExtension);
	}
	
	/* AudioFile -> Void
	 * Given: the AudioFile object with a file extension of ".wav"
	 * Returns: Void
	 * Notes: Helper function for setAudioFileParams
	 */
	private static void setWaveFileParams(AudioFile af) {
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
	
	/* AudioFile -> Void
	 * Given: the AudioFile object with a file extension of ".mp3"
	 * Returns: Void
	 * Notes: Helper function for setAudioFileParams
	 * 
	 */
	private static void setMP3FileParams(AudioFile af) {
		// Grab the byte array from the object
		byte[] data = af.getData();
		
		// MPEG-1 Layer III Corresponds to first 2 bytes reading :
		// 0xFFFA or 0xFFFB depending on CRC
		String hex1 = "FFFA";
		String hex2 = "FFFB";
		
		// Parse Hex Strings to int values for comparison
		int dec1 = Integer.parseInt(hex1, 16);
		int dec2 = Integer.parseInt(hex2, 16);
		
		
		// Read the first 2 bytes (16bits) of the file
		// Convert the big-endian value to process
		int val = convertLittleEndian(data, 0, 2, false);
		
		if ((val == dec1) || (val == dec2))
			af.setFormat("MP3");
		else
			error(2, af.getFileName());
	}
	
	/* AudioFile -> Void
	 * Given: the AudioFile object with a file extension of ".gg"
	 * Returns: Void
	 * Notes: Helper function for setAudioFileParams
	 * 
	 * TODO - 
	 * 
	 */
	private static void setOGGFileParams(AudioFile af) {
		
	}

	/* String -> String
	 * Given: the file name to check its extension
	 * Returns: the file extension
	 */
	private static String getFileExtension(String fileName) {
		// Use a StringBuilder for file extension
		StringBuilder temp = new StringBuilder();
		
		// Loop through from end of string and append to StringBuilder
		for (int i = fileName.length() - 1; i >= 0; i--) {
			if (fileName.charAt(i) == '.') {
				temp.append(fileName.charAt(i));
				break;
			}
			else
				temp.append(fileName.charAt(i));
		}
		// Reverse the appended string to be returned
		temp.reverse();
		
		// Return the file extension
		return temp.toString();
	}
	
	/* AudioFile -> Void
	 * Given: an AudioFile object to check its format
	 * Returns: Void
	 */
	private static void fileFormatCheck(AudioFile af) {
		// Validate to check if its either in wave, mp3, or ogg format
		if (waveFormatCheck(af) || mp3FormatCheck(af) 
				|| oggFormatCheck(af))
			return;
		else
			error(2, af.getFileName());
	}
	
	/* AudioFile -> boolean
	 * Given: an AudioFile to check if its in WAVE format
	 * Returns: true if it is, false otherwise 
	 */
	private static boolean waveFormatCheck(AudioFile af) {
		return (af.getRIFFval() 
				&& af.getFormat() == Format.WAVE);
	}
	
	
	/* AudioFile -> boolean
	 * Given: an AudioFile to check if its in MP3 format
	 * Returns: true if it is, false otherwise 
	 * 
	 * TODO - 
	 * This and setMP3FileParams is redundant in a way.
	 * Refactor this section in the future
	 */
	private static boolean mp3FormatCheck(AudioFile af) {
		return (af.getFormat() == Format.MP3);
	}
	
	/* AudioFile -> boolean
	 * Given: an AudioFile to check if its in OGG format
	 * Returns: true if it is, false otherwise 
	 * 
	 * TODO - 
	 * This is for later use for OGG file formats.
	 */
	private static boolean oggFormatCheck(AudioFile af) {
		
		return false;
	}
	
	/* File -> byte[]
	 * Given: a file that is read 
	 * Returns: the byte stream representation of the file
	 * Note: Fixed - does not need to be modified
	 */
	private static byte[] getByteArray(File file) throws IOException {
		// Create FileInputStream from file argument
		FileInputStream fis = null;
		fis = new FileInputStream(file);

		// Read bytes from buffer
		byte[] b = new byte[fis.available()];
		fis.read(b);
		fis.close();

		// return the array read from the buffer
		return b;
	}	

	/* int String -> Void
	 * Given: an int value that will determine the error to throw
	 * for the given file String s
	 * Returns: Void
	 * Note: this function is for printing error messages to stderr
	 */
	private static void error(int e, String s) {
		// switch block for error messages
		switch (e) {
			case 1:
				System.err.println("ERROR : " + s + " " 
					+ "is not a supported extension.");
				break;
			case 2:
				System.err.println("ERROR : " + s + " "
					+ "has incorrect file format.");
				break;
			default:
				System.err.println("ERROR");
				break;
		}
		// Terminate the application
		System.exit(1);
	}
	
}
