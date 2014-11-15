//KMP implementation

import java.util.*;

public class StringSearch{



	//List<FingerPrint> List<FingerPrint> -> int[]
	//Given: two lists of fingerprints
	//Returns: an integer array with offsets in first and second list,
	//         respectively, that describe where the audio matches
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
					i2 = i2 + 1;
				}
			}
			
		 }
		 return null;
	}
	
	
	//List<FingerPrint> int[] -> void
	//Given: a list of fingerprints that is the pattern to match
	//       against a text and integer array
	//Returns: Void (populates int array in place)
	public static void buildTable(List<FingerPrint> l1, int[] t){
		// index for t
		int ti = 2;
		// index of l1 of next character of current substring
		int l1i = 0;
		
		t[0] = -1;
		t[1] = 0;
		
		while(ti<l1.size()){
			if(l1.get(ti-1).similarTo(l1.get(l1i))){
				l1i = l1i + 1;
				t[ti] = l1i;
				l1i = l1i + 1;
			} else if(l1i>0){
				l1i = t[l1i];
			} else{
				t[ti] = 0;
				ti = ti + 1;
			}
		}
	}
		
		
	
	
	

}