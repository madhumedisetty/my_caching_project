package com.datastructure.array;

import java.util.*;

public class MaxSubArray {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the size of the array");
        int n=sc.nextInt();
        int[] arr=new int[n];
        System.out.println("Enter the elements of the array");
        for(int i=0;i<n;i++){
            arr[i]=sc.nextInt();
        }
        int sum=0;
        int maxSum=Integer.MIN_VALUE;
        int left=0, right=0, temp=0;
        for(int i=0;i<n;i++){
            sum+=arr[i];

            maxSum=Math.max(maxSum, sum);
            left=temp;
            right=i;
            if(sum<0){
                sum=0;
                temp=i+1;
            }

        }
        System.out.println("Max Sum of Sub Array is "+maxSum);
        System.out.println("Starting index is "+left+" and End index is "+right);
        sc.close();
    }
    
}
