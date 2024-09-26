package space.lachy.lachsbot.cli.command

import space.lachy.lachsbot.LachsBot
import space.lachy.lachsbot.LachsBot.logger
import kotlin.system.exitProcess

object ExitCliCommand : CliCommand {

    override val aliases: List<String> = listOf("exit", "quit", "e", "q", "stop", "shutdown")
    override val name: String = "Exit CLI"
    override val description: String = "Exits the CLI."
    override val usage: String = aliases[0]

    override fun execute(id: String, args: List<String>) {
        logger.info("Exiting CLI...")
        LachsBot.stop()

        // the try/catch wrapping over this function in the listener can prevent shutdown, so this is a backup
        exitProcess(1)
    }


}