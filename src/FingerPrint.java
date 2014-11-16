
public class FingerPrint {
    
    private double THRESHOLD = 6;
    
    private ComplexNumber[] window;
    private double value;
    
    public FingerPrint(ComplexNumber[] window, int size){
        this.window = formatWindow(window, size); 
        this.value = findMaxMagnitude(this.window);
    }
    
    private ComplexNumber[] formatWindow(ComplexNumber[] window, int size){
        ComplexNumber[] list = new ComplexNumber[size/2];
        
        for (int i = 1; i <= size/2; i++){
            list[i-1] = window[i];
        }

        return list;
    }
    
    private double findMaxMagnitude(ComplexNumber[] window){
        double maxValue = 0;
        System.out.println(window.length);
        System.out.println("entering for loop");
        for(int i = 0; i < window.length; i++){
        	System.out.println(i);
            maxValue = Math.max(findPowerDensity(window[i].getMagnitude()), maxValue);
        }
        System.out.println("returning max value");
        return maxValue;
    }
    
    private double findPowerDensity(double mag){
        return 10 * Math.log10(mag);
    }
    
    // Getter
    public double getValue(){
        return value;
    }
    
    // Accessor
    public boolean similarTo(FingerPrint other){
        return  this.value - other.getValue() < this.THRESHOLD;
    }
    
}