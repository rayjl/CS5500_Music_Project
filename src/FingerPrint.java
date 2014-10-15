/* 
 * FingerPrint Object
 * -Contain the "FingerPrint" of an audio file
 * -Object is for storage purposes
 */

public class FingerPrint {

	// TODO - fix dummy threshold values of 5
	private double FREQ_THRESHOLD = 5;
	private double MAG_THRESHOLD = 5;
	private double magnitude;
	private double freq;
	
	
	public FingerPrint(ComplexNumber c){
		this.magnitude = c.getMagnitude();
		this.freq = c.getFreq();	
	}
	
	public boolean similarTo(FingerPrint f){
		if (Math.abs(magnitude - f.getMagnitude()) < this.MAG_THRESHOLD
				&& Math.abs(freq - f.getFreq()) < this.FREQ_THRESHOLD)	
			return true;
		return false;
	}
	
	
	public double getFreq(){
		return this.freq;
	}
	
	public double getMagnitude(){
		return this.magnitude;
	}
	
}
