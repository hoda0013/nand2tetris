package com.example.interviewPractice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyMain {

    public static void main(String args[]) {
//        printDuplicateCharacters();
//        isAnagram("cat", "tac");
//        getFirstNonRepeatedCharacter("aabbcdde");
//        reverseStringIteration("abcdef");
//        System.out.println(reverseStringRecursion("abcdef"));
//        System.out.println("contains only digits: " + String.valueOf(containsOnlyDigits("1234a")));
//        printAllDuplicateCharsAndCount("aardvark");
//        countNumberVowelsAndConsonants("abcdefghi");
//        reverseWordsInSentence("I am a sentence that goes this way.");
//        isPalindrome("madamimadams");
//        System.out.println(recursiveFibonacci(8));
    }

    public static void quickSort(int [] array) {
        int pivotIndex = array.length / 2;
        int pivotValue = array[pivotIndex];

        //swap
        int temp = array[pivotIndex];
        array[pivotIndex] = array[array.length - 1];
        array[array.length - 1] = temp;

        int biggerValue;
        for (int indexFromLeft = 0; indexFromLeft < array.length; indexFromLeft++) {
            if (array[indexFromLeft] > pivotValue) {
                biggerValue = array[indexFromLeft];
                break;
            }
        }

        int smallerValue;
        for (int indexFromRight = array.length - 2; indexFromRight >= 0; indexFromRight--) {
            if (array[indexFromRight] < pivotValue) {
                smallerValue = array[indexFromRight];
            }
        }
    }


    public static void swap() {

    }


    public static int recursiveFibonacci(int index) {
        // returns {index} term of the fibonacci sequence, e.g. 0, 1, 1, 2, 3, 5, 8, 13, 21, etc...
        // if you chose 6 as the number, this would return 8 b/c that's the 6th number of the series

        if (index == 0) {
            return 0;
        }

        if (index == 1) {
            return 1;
        }

        return recursiveFibonacci(index - 2) + recursiveFibonacci(index - 1);
    }

    public static void isPalindrome(String input) {
        String rev = new StringBuilder(input).reverse().toString();

        if (rev.equals(input)) {
            System.out.println("yes");
        } else {
            System.out.println("no");
        }

    }


    public static void reverseWordsInSentence(String input) {
        List<String> words = new ArrayList<>();

        boolean isProcessingWord = false;
        StringBuilder holder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == ' ' || c == '\n' || c == '\t' || (i == input.length() - 1)) {
                //White space, ignore
                if (isProcessingWord) {
                    isProcessingWord = false;
                    words.add(holder.toString());
                    holder = new StringBuilder();
                }
            } else {
                //not whitespace, add to array
                isProcessingWord = true;
               holder.append(c);
            }
        }

        for (int i = words.size() - 1; i >= 0; i--) {
            System.out.println(words.get(i));
        }
    }

    public static void findAllStringPermutations(String input) {
        //FAIL
    }

    public static void countNumberVowelsAndConsonants(String input) {
        List<String> vowelsList = new ArrayList<>();
        vowelsList.add("a");
        vowelsList.add("e");
        vowelsList.add("i");
        vowelsList.add("o");
        vowelsList.add("u");

        int vowelsCount = 0;
        int consonantsCount = 0;

        for (char c : input.toCharArray()) {
            String charVal = String.valueOf(c);
            if (vowelsList.contains(charVal)) {
                vowelsCount++;
            } else {
                consonantsCount++;
            }
        }

        System.out.println("num vowels = " + vowelsCount);
        System.out.println("num consonants = " + consonantsCount);
    }

    public static void printAllDuplicateCharsAndCount(String input) {
        Map<String, Integer> map = new HashMap<>();

        for (char c : input.toCharArray()) {
            String charVal = String.valueOf(c);
            map.put(charVal, map.containsKey(charVal) ? map.get(charVal) + 1 : 1);
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println("Character " + entry.getKey() + " appears " + entry.getValue() + " times.");
            }
        }
    }

    public static boolean containsOnlyDigits(String input) {
        String output = input.replaceAll("[0-9]+", "");
        System.out.println(output);
        return output.length() == 0;
    }

    public static String reverseStringRecursion(String input) {
        if (input == null) {
            throw new RuntimeException("input cannot be null");
        }

        if (input.length() == 1) {
            return input;
        } else if (input.length() == 2) {
            char first = input.charAt(0);
            char last = input.charAt(1);
            return String.valueOf(last) + String.valueOf(first);
        }

        char first = input.charAt(0);
        char last = input.charAt(input.length() - 1);
        System.out.println("first: " + first + " last: " + last);
        String sliced;
        if (input.length() == 3) {
            sliced = String.valueOf(input.charAt(1));
        } else {
            sliced = input.substring(1, input.length() - 1);
        }
        System.out.println("sliced: " + sliced);
        return last + reverseStringRecursion(sliced) + first;
    }

    public static void printDuplicateCharacters() {
        String inputString = "Aardvark";
        char [] array = inputString.toCharArray();
        Arrays.sort(array);

        char matchedChar = array[0];
        boolean isMatched = false;

        for (int i = 1; i < array.length; i++) {
            if (array[i-1] == array[i]) {
                matchedChar = array[i];
                isMatched = true;
            } else if (isMatched) {
                System.out.print(matchedChar);
                isMatched = false;
            }
        }
    }

    public static void isAnagram(String input1, String input2) {
        char [] i1 = input1.toCharArray();
        char [] i2 = input2.toCharArray();

        Arrays.sort(i1);
        Arrays.sort(i2);

        System.out.println(i1);
        System.out.println(i2);
        if (Arrays.equals(i1, i2)) {
            System.out.println("isAnagram");
        } else {
            System.out.println("is not anagram");
        }
    }

    public static void getFirstNonRepeatedCharacter(String input) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        for (char c : input.toCharArray()) {
            map.put(String.valueOf(c), (map.get(String.valueOf(c)) == null ? 0 : map.get(String.valueOf(c))) + 1);
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                System.out.println(entry.getKey());
                break;
            }
        }
    }

    public static void reverseStringIteration(String input) {

        char first;
        char last;

        int tailIndex = input.length() - 1;
        char [] reversed = new char[input.length()];

        for (int i = 0; i < input.length(); i++) {
            if (i < tailIndex) {
                first = input.charAt(i);
                last = input.charAt(tailIndex);
                reversed[i] = last;
                reversed[tailIndex] = first;
                tailIndex--;
            } else if (i == tailIndex) {
                reversed[i] = input.charAt(i);
                System.out.println(reversed);
                break;
            } else {
                System.out.println(reversed);
                break;
            }
        }
    }
}

