//Longest common substring

import java.util.*;

public class LCS{

 //List<FingerPrint> List<FingerPrint> -> int[]
 //Given: two lists of fingerprints
 //Returns: an array that gives indexes of l1 and l2 where the match
 //         is found; index 0 of array has offset of l1; index 1 of
 //         array has offset of l2; array holds -1 if no match
 //         is found
public static int[] lcs(List<FingerPrint> l1, List<FingerPrint> l2){
 //table[i,j] holds the longest suffix for prefixes of l1 and l2
 // that end at indexes i and j respectively
 int[][] table = new int[l1.size()][l2.size()];

 //holds the longest substring match
 int matchLength = 0;

 //offsets/index of longest match, initally -1 for no match
 //offset for l1
 int index1 = -1;
 //offset for l2
 int index2 = -1;
	
 for(int i=0; i<l1.size(); i++){
  for(int j=0; j<l2.size(); j++){
   if(l1.get(i).similarTo(l2.get(j))){
    if(i==0||j==0){
     table[i][j] = 1;
    }else{
     table[i][j] = table[i-1][j-1] + 1;
    }
    //update indexes if a longer match is found
    if(matchLength<table[i][j]){
     matchLength = table[i][j];
     index1 = i - matchLength + 1;
     index2 = j - matchLength + 1;
    } 
   } else {
    table[i][j] = 0;
   }
  }
 }
		
 int[] answer = new int[2];
 answer[0] = index1;
 answer[1] = index2;
 return answer;
}
	
}