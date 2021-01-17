package com.example.chapter10

import java.io.File

fun main(args: Array<String>) {
    val tokenizer = Tokenizer()
    val parser = Parser()

    val fileOrDirectory = "/Users/bigern/Projects/nand2tetris/projects/10/Square/"
    val file = File(fileOrDirectory)

    if (file.exists()) {
        if (file.isFile) {
            tokenizer.initialize(fileOrDirectory)
            val tokens = tokenizer.tokenize()
        } else if (file.isDirectory) {
            // get all .jack files in directory and process them
            file.list()
                    ?.filter { it.endsWith(".jack") }
                    ?.forEach {
                        tokenizer.initialize("$fileOrDirectory$it")
                        val tokens = tokenizer.tokenize()
                        parser.setTokens(tokens)
                    }
        } else {
            throw Exception("$fileOrDirectory is neither a file nor directory")
        }
    } else {
        throw Exception("problem finding file or directory: $fileOrDirectory")
    }
}
