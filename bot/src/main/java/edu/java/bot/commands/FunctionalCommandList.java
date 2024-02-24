package edu.java.bot.commands;

import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FunctionalCommandList {
    @Autowired
    private List<FunctionalCommand> functionalCommandList;
}
