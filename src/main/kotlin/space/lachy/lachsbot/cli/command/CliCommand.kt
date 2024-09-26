package space.lachy.lachsbot.cli.command

interface CliCommand {
    val aliases: List<String>
    val name: String
    val description: String
    val usage: String

    fun execute(id: String, args: List<String>)

}