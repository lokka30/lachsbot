package space.lachy.lachsbot.cli.command

import space.lachy.lachsbot.LachsBot.logger
import space.lachy.lachsbot.cli.Cli
import kotlin.math.ceil

object HelpCliCommand : CliCommand {
    private const val COMMANDS_PER_PAGE: Int = 4

    override val aliases: List<String> = listOf("help", "man", "manual", "h", "m", "commands", "cmds")
    override val name: String = "Help Manual"
    override val description: String = "View a list of available commands."
    override val usage: String = "${aliases[0]} [page#]"

    override fun execute(id: String, args: List<String>) {
        val pages: List<String> = pages(id)

        if(args.isEmpty()) {
            logger.info(pages[0])
        } else if(args.size == 1) {
            val i: Int? = args[0].toIntOrNull()

            if(i == null) {
                logger.error("Invalid usage: page number argument is not a valid integer, got '${args[0]}'. Usage: ${usage}")
                return
            }

            if(i < 1) {
                logger.error("Invalid usage: page number argument must be 1 or greater, got '${args[0]}'. Usage: ${usage}")
                return
            }

            if(i > args.size) {
                logger.error("Invalid usage: page number argument must be between 1 and ${args.size}, got '${args[0]}'. Usage: ${usage}")
                return
            }

            logger.info(pages[i - 1])
        } else {
            logger.error("Invalid usage: too many arguments, expected qty 0 or 1, got ${args.size}. Usage: ${usage}")
            return
        }
    }

    private fun pages(cmdId: String): List<String> {
        val pages: MutableList<String> = mutableListOf()
        val commandsInCurrentPage: MutableList<CliCommand> = mutableListOf()
        var pageNumCurrent: Int = 1
        val pageNumMax: Int = ceil(Cli.commands.size * 1.0f / COMMANDS_PER_PAGE * 1.0f).toInt()

        for (command in Cli.commands) {
            if(commandsInCurrentPage.size > COMMANDS_PER_PAGE) {
                pages.add(buildPage(commandsInCurrentPage, pageNumCurrent, pageNumMax, cmdId))
                commandsInCurrentPage.clear()
                pageNumCurrent++
            }

            commandsInCurrentPage.add(command)
        }

        if(pageNumCurrent != pageNumMax) {
            throw IllegalStateException("pageNumCurrent should match pageNumMax, but got ${pageNumCurrent} and ${pageNumMax}, respectively")
        }

        pages.add(buildPage(commandsInCurrentPage, pageNumCurrent, pageNumMax, cmdId))

        return pages.toList()
    }

    private fun buildPage(
        cmds: Collection<CliCommand>,
        pageNumCurrent: Int,
        pageNumMax: Int,
        cmdId: String
    ): String {
        val sb: StringBuilder = StringBuilder()
        val appendSeparator = {
            sb.append("\n+")
                .append("-".repeat(22))
                .append('+')
                .append(" Page ")
                .append(pageNumCurrent)
                .append(" of ")
                .append(pageNumMax)
                .append(" +")
                .append("-".repeat(22))
                .append("+\n")
        }

        appendSeparator()

        for(cmd in cmds) {
            sb.append('\n')
            sb.append(" -> ")
            sb.append(cmd.name)
            sb.append(" (")
            sb.append(cmd.aliases[0])
            sb.append(")\n    :- Aliases: ")
            sb.append(cmd.aliases.joinToString(", "))
            sb.append("\n    :- Description: ")
            sb.append(cmd.description)
            sb.append("\n    '- Usage: ")
            sb.append(cmd.usage)
            sb.append('\n')
        }

        if(pageNumCurrent == pageNumMax) {
            sb.append("\nYou have reached the last page of the help menu.\n")
        } else {
            sb.append("\nTo view the next page, run '${cmdId} ${pageNumCurrent + 1}'")
        }

        appendSeparator()

        return sb.toString()
    }
}