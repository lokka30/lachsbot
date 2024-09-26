# `lachsbot`

JVM Discord Bot used to log all the things, and offer utility commands.

Designed for personal use, though you're most welcome to use lachsbot within the bounds of its [License][LICENSE].

## Features

### Logging

The bot logs a handful of events, hopefully more over time. Messages received, updated, and deleted, members banned or unbanned from guilds, and members joining/leaving guilds are some example events that are logged in detail by lachsbot.

There are cases where Discord's API does not provide very useful information in logs, especially for the message deleted event, where it only provides us with the ID of the message that was deleted. By storing received and updated messages in MongoDB, lachsbot will try to piece together what the last known content of the deleted message was, making it far easier for moderators to check for rulebreakers abusing message deletion. 

### Slash Commands

Some slash commands are provided, such as:

1. `/purge` (Moderators Only)

    This will clear the 10 most recent messages sent in the channel the command is ran.

2. `/shutdown` (Admins Only)

    This softly shuts down the bot, just as a killswitch in case it goes rogue. ;)

### CLI

lachsbot features a basic CLI system allowing for administrative control over the bot. The following CLI commands are provided:

1. `exit`

    Softly shuts down the bot.

2. `help`

   View a list of available commands within the CLI. The information it provides is 
   automatically populated, and paginated.

3. `reload`

    Soft-reload functionality, mostly just to reload the configuration files.

4. `status`

    Check the status of the bot, such as its JVM uptime.

## Tech Stack

Thanks to the following projects for making this bot possible.

- **JDA** (Java Discord API)

  Fantastic library to interact with the Discord API

- **MongoDB**

  Quick and easy storage solution for the logs

- **Kotlin**

  A more enjoyable programming experience for the JVM

- **Docker**
  
  Building and deploying the app on any machine

- **Configurate**

  Seamless configuration library

- **Maven**

  Robust build system

- **IntelliJ IDEA**

  Intelligent IDE for Kotlin/Java

## Usage

These instructions are for Docker. If you don't want to use DOcker, you can alternatively build and run it locally using JDK 8, Maven, and MongoDB.

**Docker commands below may require root privileges (`sudo`).**

Steps:

1. Download this project (`git clone` or just Download ZIP on GitHub).
2. Adjust your working directory (`cd`).
3. Run `docker compose up -d`.
4. Optionally, stop the bot using `docker compose down`, and make any necessary configuration changes.

## Branches

- `master`

    This branch usually contains the same code that was used to compile the latest
    published Release binary.

- `dev/<version>`

    (Where `<version>` denotes a version being developed.)

    Development branches for specific versions. Can eventually be merged into `master`.

- `feat/<feature>`

    (Where `<feature>` denotes a feature being developed.)
    
    Major features can get their own branch to eventually be merged into `dev/...`.

## Support

Absolutely no support is provided for this project.

You are most welcome to create issues on the GitHub repository to report issues.

Please feel free to [email me](mailto:lachy@lachy.space) if you need to contact me over a private channel.

## Copyright Notice

Copyright (C) 2024  Lachlan Adamson et al.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

***

[License]: LICENSE.md
