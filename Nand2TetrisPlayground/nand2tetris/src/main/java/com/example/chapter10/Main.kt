package com.example.chapter10

import java.io.File

fun main(args: Array<String>) {

    val fileOrDirectory = "/Users/bigern/Projects/nand2tetris/projects/10/Square"
    val file = File(fileOrDirectory)

    if (file.exists()) {
        if (file.isFile) {
            val path = file.parent
            val sourceFilename = file.name
            val outputDir = File("$path/output")
            val didCreateDir = outputDir.mkdir()
            System.out.println("didCreateDir = $didCreateDir")

            val tokens = Tokenizer(outputDir.path).let {
                it.initialize(fileOrDirectory)
                it.tokenize()
            }

            Parser(outputDir.path, sourceFilename).apply {
                setTokens(tokens)
                parse()
            }
        } else if (file.isDirectory) {
            // get all .jack files in directory and process them
            val inputDirPath = file.path
            val outputDir = File("$inputDirPath/output")
            val didCreateDir = outputDir.mkdir()
            System.out.println("didCreateDir = $didCreateDir")

            file.list()
                    ?.filter { it.endsWith(".jack") }
                    ?.forEach { sourceFile ->
                        val tokens = Tokenizer(outputDir.path).let {
                            it.initialize("$fileOrDirectory/$sourceFile")
                            it.tokenize()
                        }
                        Parser(outputDir.path, sourceFile).apply {
                            setTokens(tokens)
                            parse()
                        }
                    }
        } else {
            throw Exception("$fileOrDirectory is neither a file nor directory")
        }
    } else {
        throw Exception("problem finding file or directory: $fileOrDirectory")
    }
}
