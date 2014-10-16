import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/* 
 * Rapid Prototype
 * Last Edited: 15 October 2014
 */

public class AudioMatching {

	// Global variables
	static String file_name1;
	static String file_name2;
	static boolean match;
	
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
					
			// Convert data to Little-Endian Form
			long[] sample1 = getLittleEndianForm(b1);
			long[] sample2 = getLittleEndianForm(b2);
		
			// Convert data to complex numbers
			ComplexNumber[] cn1 = 
					convertToComplexNumber(b1);
			ComplexNumber[] cn2 =
					convertToComplexNumber(b2);
					
			// Transform ComplexNumbers
			// DFT operation time is too slow
//			ComplexNumber[] dft1 = DFT.dft(cn1);
//			ComplexNumber[] dft2 = DFT.dft(cn2);
					
			// Use FFT Implementation
//			FFT temp = new FFT(cn1.length);
					
			double[] real1 = new double[cn1.length];
			double[] imag1 = new double[cn1.length];
					
			double[] real2 = new double[cn2.length];
			double[] imag2 = new double[cn2.length];
					
			for (int i = 0; i < cn1.length; i++) {
				real1[i] = cn1[i].getReal();
				imag1[i] = cn1[i].getImag();
			}
					
			for (int i = 0; i < cn2.length; i++) {
				real2[i] = cn2[i].getReal();
				imag2[i] = cn2[i].getImag();
			}
					
//			ComplexNumber[] transformed1 = 
//					temp.fft(real1, imag1);
//			ComplexNumber[] transformed2 = 
//					temp.fft(real2,imag2);
					
			// Convert to FingerPrints
			FingerPrint[] fp1 =
					makeFingerPrints(cn1);
			FingerPrint[] fp2 =
					makeFingerPrints(cn2);
					
			// Compare FingerPrints
			compareFingerPrints(fp1, fp2);
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	
	/* byte[] -> ComplexNumber[]
	 * Given: array of bytes
	 * Returns: corresponding array of complex numbers
	 */
	private static ComplexNumber[] convertToComplexNumber(byte[] a) {
		int N = a.length;
		
		ComplexNumber[] complexArray = new ComplexNumber[N];
		
		for(int i=0; i<N; i++){
			complexArray[i] = new ComplexNumber((double) a[i], 0);
		}
		
		return complexArray;	
	}

	/* ComplexNumber[] -> FingerPrint[]
	 * Given: ComplexNumber[] that contains the data 
	 * transformed by the DFT
	 * Returns: a FingerPrint[] that contains the FingerPrints 
	 * of the samples
	 */
	private static FingerPrint[] makeFingerPrints(ComplexNumber[] ca) {
		FingerPrint[] fa = new FingerPrint[ca.length];
		
		for(int i = 0; i < ca.length; i++){
			fa[i] = new FingerPrint(ca[i]);
		}
		return fa;
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
