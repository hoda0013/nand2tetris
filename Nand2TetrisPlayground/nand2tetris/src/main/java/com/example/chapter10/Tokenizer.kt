package com.example.chapter10

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.regex.Pattern


class Tokenizer {

    private lateinit var outputFile: File
    private lateinit var bufferedWriter: BufferedWriter
    private lateinit var bufferedReader: BufferedReader
    private lateinit var inputFile: File

    private val whiteSpaceRegex = Pattern.compile("[\\s]+")

    fun initialize(fileName: String){
        inputFile = File(fileName)
        outputFile = File(fileName.plus("T.xml"))
        bufferedWriter = BufferedWriter(FileWriter(outputFile))
        bufferedReader = BufferedReader(FileReader(inputFile))
    }

    private val tokensSplitOnWhitespace = ArrayList<String>()

    fun tokenize() {
        // go through program line by line
        // for each line, break into tokens based on whitespace
        // categorize each token and print it to file
        var line = bufferedReader.readLine()
        var isInComment = false
        while(line != null) {
            // Split at all whitespace
            val split = line.split(whiteSpaceRegex)

            // May contain comments, strip those out

            for(i in split.indices) {
                val token = split[i]

                if (token == "//") {
                    isInComment = false
                    break
                } else if (token == "/*" || token == "/**") {
                    isInComment = true
                    // discard this token and every other token until we see a */
                } else if (token == "*/") {
                    isInComment = false
                } else if (!isInComment) {
                    if (token.isNotEmpty()) {
                        tokensSplitOnWhitespace.add(token)
                    }
                }
            }

            line = bufferedReader.readLine()
        }


        val tokens = ArrayList<String>()

        tokensSplitOnWhitespace.forEach {
            val splitted = it.split(
                    Regex("((?<=[;\\(\\)\\{\\}\\[\\]\\.\\,\\+\\-\\*\\/&\\|\\<\\>=~])|(?=[;\\(\\)\\{\\}\\[\\]\\.\\,\\+\\-\\*\\/&\\|\\<\\>=~]))")
            )
            splitted.filter { it.isNotEmpty() }.forEach { tokens.add(it) }
            System.out.println(splitted.toString())
        }

        tokens.forEach {
            bufferedWriter.append("$it\n")
        }

        bufferedWriter.close()
    }
}