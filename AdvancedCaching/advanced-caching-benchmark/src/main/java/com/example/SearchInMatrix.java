package com.example;

public class SearchInMatrix {

    // Function to search for a specific element in an unordered matrix
    public static boolean searchElement(int[][] matrix, int target) {
        // Iterate through each row of the matrix
        for (int i = 0; i < matrix.length; i++) {
            // Iterate through each column in the row
            for (int j = 0; j < matrix[i].length; j++) {
                // If the current element matches the target, return true
                if (matrix[i][j] == target) {
                    System.out.println("Element " + target + " found at position: (" + i + ", " + j + ")");
                    return true;
                }
            }
        }
        // If we reach here, the element was not found
        return false;
    }

    public static void main(String[] args) {
        // Example of an unordered matrix
        int[][] matrix = {
            {5, 3, 8},
            {1, 6, 9},
            {2, 7, 4}
        };

        // Target element to search for
        int target = 6;

        // Call the search function
        boolean found = searchElement(matrix, target);

        // Print result
        if (!found) {
            System.out.println("Element " + target + " not found.");
        }
    }
}
