package com.datastructure.array;

import java.util.*;

public class Recursion {
    public static void printString(String str, int n){
        if(n==0){
            return;
        }
        System.out.println(str);
        printString(str, n-1);
    }   

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the string");
        String str=sc.nextLine();
        System.out.println("Enter the number of times to print the string");
        int n=sc.nextInt();
        printString(str, n);
        sc.close();
    }
}
