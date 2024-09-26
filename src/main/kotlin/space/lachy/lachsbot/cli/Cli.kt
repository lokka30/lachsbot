package space.lachy.lachsbot.cli

import okhttp3.internal.toImmutableList
import space.lachy.lachsbot.LachsBot.logger
import space.lachy.lachsbot.cli.command.CliCommand
import space.lachy.lachsbot.cli.command.ExitCliCommand
import space.lachy.lachsbot.cli.command.HelpCliCommand
import space.lachy.lachsbot.cli.command.ReloadCliCommand
import space.lachy.lachsbot.cli.command.StatusCommand
import space.lachy.lachsbot.util.ThrowableUtil

object Cli {

    val commands: Set<CliCommand> = setOf(
        ExitCliCommand,
        HelpCliCommand,
        ReloadCliCommand,
        StatusCommand
    )

    fun start() {
        logger.info("You are now in CLI mode, use 'h' for help.")
        while (true) {
            //print(">")

            val command: String? = readlnOrNull()

            if(command == null) {
                logger.info("Reached EOF; exiting CLI mode.")
                break
            }

            val cmdSplit: List<String> = command.split(' ')

            val cmdId: String = (cmdSplit.getOrNull(0) ?: "").lowercase()

            if(cmdId.isEmpty()) {
                logger.info("Ignoring empty command.")
                continue
            }

            val cmdArgs: List<String> = cmdSplit.subList(1, cmdSplit.size).toImmutableList()

            val cmd: CliCommand? = commands.firstOrNull { it.aliases.contains(cmdId) }

            if(cmd == null) {
                logger.error("Unknown command '${cmdId}'. For help, run 'help'.")
                continue
            }

            try {
                cmd.execute(cmdId, cmdArgs)
            } catch(ex: Exception) {
                ThrowableUtil.logThrowable(ex, "Encountered exception whilst executing CLI command")
            }
        }

    }

}