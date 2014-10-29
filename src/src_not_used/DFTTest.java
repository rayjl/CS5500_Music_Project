public class DFTTest{

	public static void main(String[] args){
	
		ComplexNumber[] input = new ComplexNumber[4];
		input[0] = new ComplexNumber(-0.03480425839330703,0);
		input[1] = new ComplexNumber(0.07910192950176387,0);
		input[2] = new ComplexNumber(0.7233322451735928,0);
		input[3] = new ComplexNumber(0.1659819820667019,0);
		
		
		ComplexNumber[] output = DFT.dft(input);
		
		for(int i=0; i<output.length; i++){
			System.out.println(output[i]);
		}
	}

}