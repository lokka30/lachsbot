package space.lachy.lachsbot.cli.command

import space.lachy.lachsbot.LachsBot

object ReloadCliCommand : CliCommand {

    override val aliases: List<String> = listOf("reload", "rl")
    override val name: String = "Soft Reload"
    override val description: String = "Soft-reloads the bots, especially useful for small config changes."
    override val usage: String = aliases[0]

    override fun execute(id: String, args: List<String>) {
        LachsBot.reload()
    }
}