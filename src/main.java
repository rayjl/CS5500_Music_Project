/**
 * Rapid Prototype
 *
 */
public class main {

	public static void main(String[] args) {

		// FileInputStream, get songs to compare header
		// use header to check if both songs are wave file format
		// --what is the input data format in?
		// --endianness of header?
		// WAVE -> 0x57415645 
		
		
		// Time similarity? 2-3 sec precision
		
		// Fingerprinting to match frequencies
		
	}
	
	/* FingerPrint FingerPrint -> boolean
	 * Given: 2 FingerPrints to compare
	 * Returns: true if the FingerPrints match
	 */
	public static boolean compareFingerPrints(FingerPrint f1,
			FingerPrint f2) {		
		//TO DO
	}
	
	// This method may be removed
	// Check use of FileInputStream in main method
	/* bitstream -> int[]
	 * Given: the bit stream of an audio file
	 * Returns: the bitstream represented as an int array
	 */
	public static int[] getBitStream(...) {
		//TO DO	
	}

	
}
