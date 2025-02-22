package com.example;
public class Solution {

    // Function to return the longest palindromic substring
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 2) {
            return s; // If string is null or has length < 2, return the string itself
        }

        int start = 0, maxLength = 1;

        for (int i = 0; i < s.length(); i++) {
            // Check for odd-length palindromes (expand around a single character)
            int len1 = expandAroundCenter(s, i, i);

            // Check for even-length palindromes (expand around two adjacent characters)
            int len2 = expandAroundCenter(s, i, i + 1);

            // Choose the longer palindrome between len1 and len2
            int len = Math.max(len1, len2);

            // If a longer palindrome is found, update the start and maxLength
            if (len > maxLength) {
                start = i - (len - 1) / 2;  // Calculate the start index of the palindrome
                maxLength = len;            // Update the maximum length
            }
        }

        // Return the longest palindromic substring
        return s.substring(start, start + maxLength);
    }

    // Helper function to expand around the center and return the length of the palindrome
    private int expandAroundCenter(String s, int left, int right) {
        // Expand as long as the characters at left and right are equal and within bounds
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;   // Move left pointer to the left
            right++;  // Move right pointer to the right
        }
        // Return the length of the palindrome
        return right - left - 1;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        String input = "babad";
        System.out.println("Longest Palindromic Substring: " + sol.longestPalindrome(input));

        input = "cbbd";
        System.out.println("Longest Palindromic Substring: " + sol.longestPalindrome(input));
    }
}
