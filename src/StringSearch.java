//KMP implementation

import java.util.*;

public class StringSearch{




	public static int[] search(List<FingerPrint> l1, List<FingerPrint> l2){
		//answer has two indexes of offsets
		int[] answer = new int[2];
		// offset for l1
		int i1 = 0;
		// offset for l2
		int i2 = 0;
		//preprocessed table
		int[] t = new int[l1.size()];
		buildTable(l1,t);

		while(i1+i2<l2.size()){
			if(l1.get(i1).similarTo(l2.get(i2))){
				if(i1==l1.size()-1){
					answer[1] = i2;
					answer[0] = i1;
					return answer;
				}
				i1 = i1+1;
			} else {
				if(t[i1]>-1){
					i2 = i2 + i1 - t[i1];
					i1 = t[i1];
				} else {
					i1 = 0;
					i2 = i2 + i1;
				}
			}
			
		 }
		 return null;
	}
	
	

	public static void buildTable(List<FingerPrint> l1, int[] t){
	
		//TODO fill out
		
		
	}
	
	

}