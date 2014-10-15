/* 
 * FingerPrint Object
 * -Contain the "FingerPrint" of an audio file
 * -Object is for storage purposes
 */

public class FingerPrint {

	private double FREQ_THRESHOLD;
	private double MAG_THRESHOLD;
	private double magnitude;
	private double freq;
	
	
	public FingerPrint(ComplexNumber c){
		this.magnitude = c.getMagnitude();
		this.freq = c.getFreq();
		
	}
	
	public boolean similarTo(FingerPrint f){
		if (magnitude - f.getMagnitude() < this.MAG_THRESHOLD
				&& freq - f.getFreq() < this.FREQ_THRESHOLD){
			
			return true;
		}
		
		return false;
	}
	
	
	public double getFreq(){
		return this.freq;
	}
	
	public double getMagnitude(){
		return this.magnitude;
	}
	
}
