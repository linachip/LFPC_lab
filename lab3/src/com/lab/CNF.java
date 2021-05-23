package com.lab;

public class CNF extends Main {

   static String path = "var25.txt";

    public CNF(String grammarFile) {
        super(grammarFile);
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            path = args[0];
            System.out.println("Grammar file: " + path);
        }
    }
}