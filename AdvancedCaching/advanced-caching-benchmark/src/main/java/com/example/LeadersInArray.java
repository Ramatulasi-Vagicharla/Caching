package com.example;

import java.util.ArrayList;
import java.util.List;

public class LeadersInArray {
    public static void main(String[] args) {
        int[] arr1 = {4, 7, 1, 0};
        int[] arr2 = {10, 22, 12, 3, 0, 6};

        System.out.println("Leaders in arr1: " + findLeaders(arr1));
        System.out.println("Leaders in arr2: " + findLeaders(arr2));
    }

    // Function to find leaders in the array
    public static List<Integer> findLeaders(int[] arr) {
        List<Integer> leaders = new ArrayList<>();
        int maxFromRight = arr[arr.length - 1]; // Start with the rightmost element
        leaders.add(maxFromRight); // The rightmost element is always a leader

        // Traverse the array from the second last element to the start
        for (int i = arr.length - 2; i >= 0; i--) {
            if (arr[i] > maxFromRight) { // Check if the current element is a leader
                leaders.add(arr[i]); // Add the leader to the list
                maxFromRight = arr[i]; // Update the maximum
            }
        }

        // Since we traversed from right to left, we need to reverse the list to maintain the original order
        List<Integer> result = new ArrayList<>();
        for (int i = leaders.size() - 1; i >= 0; i--) {
            result.add(leaders.get(i));
        }

        return result; // Return the list of leaders
    }
}

