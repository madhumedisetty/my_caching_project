package com.datastructure.array;
import java.util.*;

public class TwoSumBasic {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the target");
        int target=sc.nextInt();
        System.out.println("Enter the size of the array");
        int n=sc.nextInt();
        HashMap<Integer, Integer> hs=new HashMap<>();
        for(int i=0;i<n;i++){
            System.out.println("Enter the element " + i+": ");
            int x=sc.nextInt();
            int comp=target-x;
            if(hs.containsKey(comp)){
                System.out.println("Elements are: " + hs.get(comp) + ", " + i);
                break;
            }
            else{
                hs.put(x,i);
            } 
        }
        if(hs.size()==n){
            System.out.println("No such pair exists");
        }
        sc.close();
    }
    
    
}
