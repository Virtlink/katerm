package net.pelsmaeker.katerm.generator

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

object CLI : NoOpCliktCommand(
    name = "katerm-gen",
)

object GenerateCommand : CliktCommand(
    name = "generate",
) {
    override fun run() {
        // Implementation for generating code
        println("Generating code...")
    }
}

fun main(args: Array<String>) {
    CLI.subcommands(
       GenerateCommand,
    ).main(args)
}