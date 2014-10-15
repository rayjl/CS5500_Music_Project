/* 
 * FingerPrint Object
 * -Contains the "FingerPrint" of an audio file
 * -Object is for storage purposes
 */

public class FingerPrint {

	// TODO - fix dummy threshold values of 5
	private double FREQ_THRESHOLD = 1;
	private double MAG_THRESHOLD = 5;
	private double magnitude;
	private double freq;
	
	// Constructor
	public FingerPrint(ComplexNumber c) {
		this.magnitude = c.getMagnitude();
		this.freq = c.getFreq();	
	}
	
	// FingerPrint -> boolean
	// Given: compares this FingerPrint to the given FingerPrint
	// Returns: true if the FingerPrints are "similar"
	public boolean similarTo(FingerPrint f) {
		
		System.out.print(magnitude + " " + f.getMagnitude() + "  ");
		System.out.println(freq + " " + f.getFreq());
		
		// Check thresholds
		if (Math.abs(magnitude - f.getMagnitude()) < this.MAG_THRESHOLD
				&& Math.abs(freq - f.getFreq()) < this.FREQ_THRESHOLD)	
			return true;
		return false;
	}
	
	// Getters --------------------------------------------
	public double getFreq() {
		return this.freq;
	}
	
	public double getMagnitude() {
		return this.magnitude;
	}
	
}
