package edu.java.bot.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HelpCommandTest {
    HelpCommand helpCommand = new HelpCommand();

    @Test
    void command() {
        assertEquals("/help", helpCommand.command());
    }

    @Test
    void description() {
        assertEquals("""
            The /help command has been entered.
            This command prints information about all currently supported bot commands.

            List of available commands:
            """, helpCommand.description());
    }
}
