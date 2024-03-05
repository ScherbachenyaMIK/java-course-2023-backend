package edu.java.bot.command;

import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CommandList {
    @Autowired
    private List<Command> commandList;
}
