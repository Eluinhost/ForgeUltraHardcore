package gg.uhc.ultrahardcore;

import gg.uhc.ultrahardcore.commands.CommandButcher;
import gg.uhc.ultrahardcore.commands.CommandFeed;
import gg.uhc.ultrahardcore.commands.CommandHeal;
import gg.uhc.ultrahardcore.commands.CommandFreeze;
import net.minecraft.command.ICommand;

public enum Command {
    FEED(new CommandFeed()),
    HEAL(new CommandHeal()),
    FREEZE(new CommandFreeze()),
    BUTCHER(new CommandButcher())
    ;

    public final ICommand command;

    Command(ICommand command) {
        this.command = command;
    }
}
