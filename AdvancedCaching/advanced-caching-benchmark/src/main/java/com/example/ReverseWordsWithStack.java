package com.example;

import java.util.Stack;

public class ReverseWordsWithStack {
    public static String reverseWords(String input) {
        // Create a stack to store the words
        Stack<String> stack = new Stack<>();
        
        // Split the string into words by spaces
        String[] words = input.split("\\s+");
        
        // Push each word onto the stack
        for (String word : words) {
            stack.push(word);
        }
        
        // Create a StringBuilder to store the reversed words
        StringBuilder reversed = new StringBuilder();
        
        // Pop words from the stack and append them to the StringBuilder
        while (!stack.isEmpty()) {
            reversed.append(stack.pop());
            if (!stack.isEmpty()) { // Add a space between words
                reversed.append(" ");
            }
        }
        
        return reversed.toString();
    }

    public static void main(String[] args) {
        String input = "this is an amazing like this";
        String result = reverseWords(input);
        System.out.println("Reversed: " + result);
    }
}
