package com.example;

import java.util.*;

@SuppressWarnings("unused")
public class RecursiveBinarySearch {

    // Public method to initiate the recursive binary search
    public static int binarySearch(int[] nums, int target) {
        // Start the recursive search with the entire range of the array
        return binarySearchHelper(nums, target, 0, nums.length - 1);
    }

    // Private helper method for recursive binary search
    private static int binarySearchHelper(int[] nums, int target, int low, int high) {
        // Base case: if the search range is invalid, target is not found
        if (low > high) {
            return -1; // Target not found
        }

        // Calculate the middle index
        int mid = low + (high - low) / 2;

        // Check if the target is found at mid
        if (nums[mid] == target) {
            return mid; // Target found, return its index
        }
        // If target is greater, search in the right half
        else if (target > nums[mid]) {
            return binarySearchHelper(nums, target, mid + 1, high); // Search in right half
        }
        // If target is less, search in the left half
        else {
            return binarySearchHelper(nums, target, low, mid - 1); // Search in left half
        }
    }

    public static void main(String[] args) {
        // Sample sorted array for searching
        int[] a = {3, 4, 6, 7, 9, 12, 16, 17};
        int target = 6; // The target value to search for

        // Perform binary search and store the index of the target
        int ind = binarySearch(a, target);

        // Output the result of the search
        if (ind == -1) {
            System.out.println("The target is not present."); // Target not found
        } else {
            System.out.println("The target is at index: " + ind); // Output the index where target was found
        }
    }
}
