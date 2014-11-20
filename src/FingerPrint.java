/* FingerPrint Class
 * 
 * This class will be used to implement a finger printing routine
 * for AudioMatching.java
 * 
 */
public class FingerPrint {

    private double THRESHOLD = 3;
    private ComplexNumber[] window;
    private double value;
    
    /* Constructor
     * ComplexNumber[] int -> FingerPrint
     * Given: ComplexNumber[] representing the sample data for the segment
     * and the size of the segment
     */ 
    public FingerPrint(ComplexNumber[] window, int size){
        this.window = formatWindow(window, size); 
        this.value = findMaxMagnitude(this.window);
    }
    
    /* ComplexNumber[] int -> ComplexNumber[]
     * Given: the sample data array of ComplexNumber objects and the size
     * of the window
     * Returns: the first half of the segment (half to be analyzed) in a 
     * new array
     */
    private ComplexNumber[] formatWindow(ComplexNumber[] window, int size){
        // Initialize a new array with half the size
    	ComplexNumber[] list = new ComplexNumber[size/2];
        
    	// Iterate through and pass sample data to new array
    	// First value of the sample data is irrelevant (DC Offset)
        for (int i = 1; i <= size/2; i++){
            list[i-1] = window[i];
        }

        // Return new array
        return list;
    }
    
    /* ComplexNumber[] -> double
     * Given: a ComplexNumber[] to find the maximum power density
     * in the segment
     * Returns: a double representing the maximum power density
     */
    private double findMaxMagnitude(ComplexNumber[] window){
        double maxValue = 0;
        for(int i = 0; i < window.length; i++){
            maxValue = Math.max(findPowerDensity(window[i].getMagnitude()), maxValue);
        }
        return maxValue;
    }
    
    /* double -> double
     * Given: the double to scale using log
     * Returns: the scaled value
     */
    private double findPowerDensity(double mag){
        return 10 * Math.log10(mag);
    }
    
    // Getter -------------------------------------------------------
    public double getValue(){
        return value;
    }
    
    // Accessor
    public boolean similarTo(FingerPrint other){
//      System.out.println(this.value - other.getValue());
        return  Math.abs(this.value - other.getValue()) < this.THRESHOLD;
    }
    
}