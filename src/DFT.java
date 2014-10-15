public class DFT{

	/* ComplexNumber[] -> ComplexNumber[]
	 * Given: a sequence of complex numbers
	 * Returns: a sequence of complex numbers that is the DFT of input
	*/
	public static ComplexNumber[] dft(ComplexNumber[] input){
	
		int N = input.length;
		ComplexNumber[] output = new ComplexNumber[N];
		
		for(int k = 0; k < N; k++){
			ComplexNumber c = new ComplexNumber(0,0);
			output[k] = c;
			for(int n = 0; n < N; n++){
				double real = Math.cos(-2*Math.PI*n*k/N);
				double imaginary = Math.sin(-2*Math.PI*n*k/N);
				output[k] = 
						output[k].add(input[n].multiply(
								new ComplexNumber(real,imaginary)));
			}
		}
		
		return output;
	
	}
	
}