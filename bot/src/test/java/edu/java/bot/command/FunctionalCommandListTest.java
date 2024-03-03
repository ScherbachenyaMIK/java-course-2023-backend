package edu.java.bot.command;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FunctionalCommandListTest {
    @MockBean
    private List<FunctionalCommand> mockFunctionalCommandList;

    @Test
    void getFunctionalCommandList() throws NoSuchFieldException, IllegalAccessException {
        FunctionalCommandList commandList = new FunctionalCommandList();

        Field field = FunctionalCommandList.class.getDeclaredField("functionalCommandList");
        field.setAccessible(true);
        field.set(commandList, mockFunctionalCommandList);

        assertNotNull(commandList.getFunctionalCommandList());
        assertEquals(mockFunctionalCommandList, commandList.getFunctionalCommandList());
    }
}
