package com.example;

import java.util.*;

// TrieNode class represents a single node in the Trie
class TrieNode<T> {
    // Map to store child nodes, where the key is the character and value is the child TrieNode
    Map<Character, TrieNode<T>> children;
    
    // Value associated with this node (null if this node doesn't represent the end of a word)
    T value;
    
    // Flag to indicate if this node represents the end of a word
    boolean isEndOfWord;

    // Constructor to initialize a new TrieNode
    public TrieNode() {
        this.children = new HashMap<>();
        this.value = null;
        this.isEndOfWord = false;
    }
}

// Trie class implements the main functionality of the Trie data structure
class Trie<T> {
    // Root node of the Trie
    private TrieNode<T> root;

    // Constructor to initialize an empty Trie
    public Trie() {
       this.root = new TrieNode<>();
    }

    // Method to insert a word and its associated value into the Trie
    public void insert(String word, T value) {
        TrieNode<T> current = root;
        
        // Iterate through each character in the word
        for (char c : word.toCharArray()) {
            // If the current character doesn't exist as a child, create a new node
            current = current.children.computeIfAbsent(c, k -> new TrieNode<>());
        }
        
        // Mark the last node as the end of a word and set its value
        current.isEndOfWord = true;
        current.value = value;
    }

    // Method to search for a word in the Trie
    public T search(String word) {
        TrieNode<T> node = findNode(word);
        // Return the value if the word exists and is marked as end of word, otherwise null
        return (node != null && node.isEndOfWord) ? node.value : null;
    }

    // Method to check if any word in the Trie starts with the given prefix
    public boolean startsWith(String prefix) {
        // If we can find a node for this prefix, it exists in the Trie
        return findNode(prefix) != null;
    }

    // Helper method to find a node corresponding to a given word or prefix
    private TrieNode<T> findNode(String str) {
        TrieNode<T> current = root;
        
        // Traverse the Trie following the characters in the string
        for (char c : str.toCharArray()) {
            current = current.children.get(c);
            // If at any point we can't find a child node, the word/prefix doesn't exist
            if (current == null) return null;
        }
        
        // Return the final node we reached
        return current;
    }

    // Method to get all words in the Trie with a given prefix
    public List<String> getWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode<T> prefixNode = findNode(prefix);
        
        // If the prefix exists in the Trie, collect all words starting from its last node
        if (prefixNode != null) {
            collectWords(prefixNode, prefix, result);
        }
        
        return result;
    }

    // Recursive helper method to collect all words from a given node
    private void collectWords(TrieNode<T> node, String currentPrefix, List<String> result) {
        // If this node is marked as end of word, add the current prefix to results
        if (node.isEndOfWord) {
            result.add(currentPrefix);
        }
        
        // Recursively explore all child nodes
        for (Map.Entry<Character, TrieNode<T>> entry : node.children.entrySet()) {
            collectWords(entry.getValue(), currentPrefix + entry.getKey(), result);
        }
    }
}

// Main class to demonstrate the usage of the Trie
public class AdvancedDataStructures {
    public static void main(String[] args) {
        Trie<Integer> trie = new Trie<>();

        // Insert some words with associated values
        trie.insert("apple", 1);
        trie.insert("app", 2);
        trie.insert("application", 3);
        trie.insert("banana", 4);

        // Demonstrate search functionality
        System.out.println("Value for 'apple': " + trie.search("apple"));  // Output: 1
        System.out.println("Value for 'app': " + trie.search("app"));      // Output: 2
        System.out.println("Value for 'appl': " + trie.search("appl"));    // Output: null

        // Demonstrate prefix checking
        System.out.println("Starts with 'app': " + trie.startsWith("app"));  // Output: true
        System.out.println("Starts with 'ban': " + trie.startsWith("ban"));  // Output: true
        System.out.println("Starts with 'car': " + trie.startsWith("car"));  // Output: false

        // Demonstrate getting words with a prefix
        System.out.println("Words with prefix 'app': " + trie.getWordsWithPrefix("app"));
        // Output: [app, apple, application]
    }
}