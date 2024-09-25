package com.datastructure.array;

import java.util.Scanner;

public class ContainerWithMostWater {

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the size of the array");
        int n=sc.nextInt();
        int[] arr=new int[n];
        System.out.println("Enter the elements of the array");
        for(int i=0;i<n;i++){
            arr[i]=sc.nextInt();
        }
        int maxArea=0;
        int left=0, right=n-1;
        while(left<right){
            int area=Math.min(arr[left],arr[right])*(right-left);
            maxArea=Math.max(maxArea, area);
            if(arr[left]<arr[right]){
                left++;
            }
            else{
                right--;
            }
        }
        System.out.println("Max Area is "+maxArea);
        sc.close();
    }
}
