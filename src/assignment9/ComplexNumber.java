/* ComplexNumber class
 * 
 * This class is used to represent a complex number
 * and contains some relevant operations for its use.
 */

public class ComplexNumber {

    private double r; //real part
    private double i; //imaginary part
    
    /* Constructor
     * double double -> ComplexNumber
     * Given: the real and and imaginary number of a complex pair
     */
    public ComplexNumber(double r, double i){
        this.r = r;
        this.i = i;
    }
    
    /* ComplexNumber -> ComplexNumber
     * Given: a ComplexNumber that represents a complex number
     * Returns: the result of adding this ComplexNumber with given
                input
     */
    public ComplexNumber add(ComplexNumber c2){
        return new ComplexNumber(this.r + c2.r, this.i + c2.i);
    }
    
    /* ComplexNumber -> ComplexNumber
     * Given: a ComplexNumber that represents a complex number
     * Returns: the result of multiplying this ComplexNumber with given
                input
     */
    public ComplexNumber multiply(ComplexNumber c2){
        return new ComplexNumber(this.r*c2.r - this.i*c2.i, 
                this.r*c2.i + this.i*c2.r);
    }
    
    /* ComplexNumber -> ComplexNumber
     * Given: a ComplexNumber that represents a complex number
     * Returns: the result of dividing this ComplexNumber with given
                input
     */    
    public ComplexNumber divide(ComplexNumber c2){
        return new
         ComplexNumber((this.r*c2.r + this.i*c2.i) 
                             / (Math.pow(c2.r,2) + Math.pow(c2.i,2)),
                         (this.i*c2.r - this.r*c2.i) 
                             / (Math.pow(c2.r,2) + Math.pow(c2.i,2)));
    }
    
    /*  -> String
     * Given: 
     * Returns: a string representation of this ComplexNumber
     */    
    public String toString(){
        return this.r + " + " + this.i + "i";
    }
    
    // Getters ------------------------------------------------------
    public double getMagnitude(){
        return Math.sqrt(i*i + r*r);
    }
     
    public double getFreq(){
        if(r<0){
            return Math.PI + Math.atan(i/r);
        } else {
            return Math.atan(i/r);
        }
    }
    
    public double getReal() {
        return this.r;
    }
    
    // Setters ------------------------------------------------------
    public double getImag() {
        return this.i;
    }

    public void setReal(double r) {
        this.r = r;
    }
    
    public void setImag(double i) {
        this.i = i;
    }
    
}