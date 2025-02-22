package com.example;

import java.util.Arrays;

public class StringAnagram {
    public static boolean isAnagram(String s1, String s2) {
        // Remove whitespace and convert to lower case
        String str1 = s1.replaceAll("\\s", "").toLowerCase();
        String str2 = s2.replaceAll("\\s", "").toLowerCase();

        // Check if lengths are different
        if (str1.length() != str2.length()) {
            return false;
        } else {
            // Convert strings to character arrays
            char[] c1 = str1.toCharArray();
            char[] c2 = str2.toCharArray();

            // Sort both character arrays
            Arrays.sort(c1);
            Arrays.sort(c2);

            // Compare the sorted arrays
            return Arrays.equals(c1, c2);
        }
    }

    public static void main(String[] args) {
        System.out.println(isAnagram("Rama", "Amar")); // Output: true
    }
}
