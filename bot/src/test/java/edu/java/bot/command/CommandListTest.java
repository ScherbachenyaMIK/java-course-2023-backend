package edu.java.bot.command;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CommandListTest {
    @MockBean
    private List<Command> mockCommandList;

    @Test
    void getCommandList() throws NoSuchFieldException, IllegalAccessException {
        CommandList commandList = new CommandList();

        Field field = CommandList.class.getDeclaredField("commandList");
        field.setAccessible(true);
        field.set(commandList, mockCommandList);

        assertNotNull(commandList.getCommandList());
        assertEquals(mockCommandList, commandList.getCommandList());
    }
}
