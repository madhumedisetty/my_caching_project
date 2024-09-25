package com.datastructure.array;
import java.util.Scanner;
public class InsertionSort {
    public static void insertionSort(int[] arr){

        for(int i=1;i<arr.length;i++){
            int key=arr[i];
            int j=i-1;
            while(arr[j]>key&&j>=0){
                arr[j+1]=arr[j];
                j-=1;
            }
            arr[j+1]=key;
        }
        
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
        insertionSort(arr);
        System.out.println("The sorted array is");
        for(int i=0;i<n;i++){
            System.out.println(arr[i]);
        }
        sc.close();
    }
    
}
