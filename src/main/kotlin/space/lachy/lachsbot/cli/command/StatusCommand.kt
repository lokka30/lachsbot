package space.lachy.lachsbot.cli.command

import space.lachy.lachsbot.LachsBot.logger
import space.lachy.lachsbot.util.JvmUtil
import space.lachy.lachsbot.util.TimeUtil

object StatusCommand : CliCommand {
    override val aliases: List<String> = listOf("status")
    override val name: String = "Check Status"
    override val description: String = "Check the general status of the bot."
    override val usage: String = aliases[0]

    override fun execute(id: String, args: List<String>) {
        val sb: StringBuilder = StringBuilder()

        sb.append("\nStatus Report\n")
        sb.append("=============\n")
        sb.append("\n")
        sb.append("Version: \${project.version}")
        sb.append("Uptime: ${TimeUtil.formatMillisToDuration(JvmUtil.getUptimeMs())}\n")

        logger.info(sb.toString())
    }
}