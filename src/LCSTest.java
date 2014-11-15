public class LCSTest{

 public static void main(String[] args){

  int[] test1 = LCS.lcs("BABA","ABAB");
  printResults(test1);
 
  int[] test2 = LCS.lcs("AAAA", "BBBB");
  printResults(test2);

  int[] test3 = LCS.lcs("ABCDA", "AAABCDA");
  printResults(test3);
		
		
	}
	
 public static void printResults(int[] test){
  System.out.println("offset1: " + test[0] + ", offset2: "   + test[1]);
 }
	
}