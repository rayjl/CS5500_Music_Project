/* 
 * FingerPrint Object
 * -Contains the "FingerPrint" of an audio file
 * -Object is for storage purposes
 */

public class FingerPrint {

	// TODO - fix dummy threshold value?
	private double THRESHOLD = 12;
	private double real;
	private double imag;
	private double powerDensity;
	
	// Constructor
	public FingerPrint(double real, double imag) {
		this.real = real;
		this.imag = imag;	
		this.powerDensity = this.setPowerDensity(real, imag);
	}
	
	/* double double -> double
	 * Given: real and imaginary part of a frame of signal
	 * Returns: the power density of that frame
	 */
	public double setPowerDensity(double real, double imag) {
		double a = real * real;
		double b = imag * imag;
		double mag = Math.sqrt(a + b);
		
		// Magnitude value in dB - is this way of computing it correct?
		double magdB = 10 * Math.log(mag);
		return magdB;
	}
	
	/* FingerPrint -> boolean
	 * Given: compares this FingerPrint to the given FingerPrint
	 * Returns: true if the FingerPrints are "similar"
	 */
	public boolean similarTo(FingerPrint f) {
		// Check thresholds
		if (Math.abs(powerDensity - f.getPowerDensity()) < this.THRESHOLD)
			return true;
		return false;
	}
	
	// Getters --------------------------------------------
	public double getImag() {
		return this.imag;
	}
	
	public double getReal() {
		return this.real;
	}
	
	public double getPowerDensity() {
		return this.powerDensity;
	}
	
}
