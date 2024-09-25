package com.datastructure.array;

import java.util.Scanner;

public class TrappingRainwater {
    public int trap(int[] height) {
        int n=height.length;
        int left[]=new int[n];
        int right[]=new int[n];
        left[0]=height[0];
        right[n-1]=height[n-1];
        for(int i=1;i<n;i++){
            left[i]=Math.max(height[i], left[i-1]);
        }
        for(int j=n-2;j>=0;j--){
            right[j]=Math.max(height[j], right[j+1]);
        }
        int sum=0;
        for(int i=0;i<n;i++){
            sum+=(Math.min(left[i],right[i])-height[i]);
        }
        return sum;
    }
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the size of the array");
        int n=sc.nextInt();
        int[] arr=new int[n];
        System.out.println("Enter the elements of the array");
        for(int i=0;i<n;i++){
            arr[i]=sc.nextInt();
        }
        TrappingRainwater obj=new TrappingRainwater();
        System.out.println("The amount of water trapped is "+obj.trap(arr));
        sc.close();
    }
}
