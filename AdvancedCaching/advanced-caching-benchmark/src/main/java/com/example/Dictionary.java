package com.example;

import java.util.function.Consumer;

public class Dictionary<K, V> {
    private Entry<K, V>[] table;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    @SuppressWarnings("unchecked")
    public Dictionary(int capacity) {
        table = new Entry[capacity];
        size = 0;
    }

    public void put(K key, V value) {
        if (key == null) { // Check if the key is null to prevent null pointer exceptions
            throw new IllegalArgumentException("Null keys are not allowed"); // Throw an exception if the key is null
        }
        if ((double) size / table.length >= LOAD_FACTOR_THRESHOLD) { // Check if the load factor has exceeded the threshold
            resize(); // Resize the table if the load factor has exceeded the threshold
        }
        int index = hash1(key); // Calculate the initial index using the first hash function
        int step = hash2(key); // Calculate the step size using the second hash function
        int i = 0; // Initialize the iteration counter
        while (table[index] != null && !table[index].getKey().equals(key)) { // Loop until an empty slot is found or the key is found
            index = (index + step * i) % table.length; // Calculate the next index using the step size and iteration counter
            i++; // Increment the iteration counter
        }
        table[index] = new Entry<>(key, value); // Insert the new entry into the table
        size++; // Increment the size of the table
    }

    public V get(K key) {
        if (key == null) { // Check if the key is null to prevent null pointer exceptions
            throw new IllegalArgumentException("Null keys are not allowed"); // Throw an exception if the key is null
        }
        int index = hash1(key);
        int step = hash2(key);
        int i = 0;
        while (table[index] != null) {
            if (table[index].getKey().equals(key)) { // Fixed method call to getKey()
                return table[index].getValue();
            }
            index = (index + step * i) % table.length;
            i++;
        }
        return null; // Return null if the key is not found
    }

    public void remove(K key) {
        if (key == null) { // Check if the key is null to prevent null pointer exceptions
            throw new IllegalArgumentException("Null keys are not allowed"); // Throw an exception if the key is null
        }
        int index = hash1(key);
        int step = hash2(key);
        int i = 0;

        while (table[index] != null) {
            if (table[index].getKey().equals(key)) {
                table[index] = null; // Remove the entry by setting it to null
                size--; // Decrease the size
                rehash(index); // Rehash the entries after removal to avoid broken chains
                return; // Exit after removing
            }
            index = (index + step * i) % table.length;
            i++;
        }
        System.out.println("Key not found: " + key); // Key not found
    }

    public void forEach(Consumer<Entry<K, V>> action) {
        for (Entry<K, V> entry : table) {
            if (entry != null) {
                action.accept(entry); // Apply the consumer action to each non-null entry
            }
        }
    }

    private void rehash(int removedIndex) {
        int index = (removedIndex + 1) % table.length;
        while (table[index] != null) {
            Entry<K, V> entryToRehash = table[index];
            table[index] = null; // Clear the entry
            size--; // Decrease the size
            put(entryToRehash.getKey(), entryToRehash.getValue()); // Reinsert the entry
            index = (index + 1) % table.length; // Move to the next index
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table; // Use array for oldTable
        table = new Entry[oldTable.length * 2]; // Double the size of the table
        size = 0; // Reset size
        for (Entry<K, V> entry : oldTable) {
            if (entry != null) {
                put(entry.getKey(), entry.getValue()); // Rehash entries
            }
        }
    }

    private int hash1(K key) {
        return Math.abs(key.hashCode()) % table.length; // First hash function
    }

    private int hash2(K key) {
        return 1 + (Math.abs(key.hashCode()) % (table.length - 1)); // Second hash function
    }

    private static class Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key; // Getter for key
        }

        public V getValue() {
            return value; // Getter for value
        }
    }

    public static void main(String[] args) {
        // Test the Dictionary class
        Dictionary<String, Integer> dict = new Dictionary<>(5);
        dict.put("Apple", 1);
        dict.put("Banana", 2);
        dict.put("Orange", 3);

        // Get values from the dictionary
        System.out.println("Apple: " + dict.get("Apple")); // Output: Apple: 1
        System.out.println("Banana: " + dict.get("Banana")); // Output: Banana: 2
        System.out.println("Orange: " + dict.get("Orange")); // Output: Orange: 3

        // Test removing a key
        dict.remove("Banana");
        System.out.println("After removing Banana:");
        System.out.println("Banana: " + dict.get("Banana")); // Output: Banana: null

        // Test with a non-existing key
        System.out.println("Grape: " + dict.get("Grape")); // Output: Grape: null

        // Test null key exception
        try {
            dict.put(null, 4); // This should throw an exception
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // Output: Null keys are not allowed
        }

        // Use forEach method to print all entries
        System.out.println("All entries in the dictionary:");
        dict.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
