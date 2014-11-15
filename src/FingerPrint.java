public class FingerPrint {
    
    private ArrayList<ComplexNumber> window;
    private double value;
    
    public FingerPrint(ArrayList<ComplexNumber> window, int size){
        this.window = formatWindow(window, size);
        
        this.value = findMaxMagnitude(this.window);
    }
    
    private ArrayList<ComplexNumber> formatWindow(ArrayList<ComplexNumber> window, int size){
        ArrayList<ComplexNumber> list = new ArrayList<ComplexNumber>();
        
        for (int i = 1; i < size/2; i++){
            list.add(window.get(i));
        }
    }
    
    private double findMaxMagnitude(ArrayList<ComplexNumber> window){
        double maxValue = 0;
        for(ComplexNumber comp : window){
            maxValue = Math.max(comp.getMagnitude(), maxValue);
        }
        return maxValue;
    }
    
    
    
    
    public double getValue(){
        return value;
    }
    
}