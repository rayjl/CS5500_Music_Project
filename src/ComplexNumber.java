public class ComplexNumber {

	private double r;
	private double i;
	
	public ComplexNumber(double r, double i){
		this.r = r;
		this.i = i;
	}
	
	public ComplexNumber add(ComplexNumber c2){
		return new ComplexNumber(this.r + c2.r, this.i + c2.i);
	}
	
	public ComplexNumber multiply(ComplexNumber c2){
		return new ComplexNumber(this.r*c2.r - this.i*c2.i, 
				this.r*c2.i + this.i*c2.r);
	}
	
	public ComplexNumber divide(ComplexNumber c2){
		return new
		 ComplexNumber((this.r*c2.r + this.i*c2.i) 
				 			/ (Math.pow(c2.r,2) + Math.pow(c2.i,2)),
				 		(this.i*c2.r - this.r*c2.i) 
				 			/ (Math.pow(c2.r,2) + Math.pow(c2.i,2)));
	}
	
	public String toString(){
		return this.r + " + " + this.i + "i";
	}
	
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

}