package edu.java.util;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jpa.model.Chat;

@SuppressWarnings("HideUtilityClassConstructor")
public class ChatEntityDTOConverter {
    public static ChatDTO convert(Chat chat) {
        return new ChatDTO(chat.getId(), chat.getChatId());
    }

    public static Chat convert(ChatDTO chatDTO) {
        Chat chat = new Chat();
        chat.setId(chatDTO.id());
        chat.setChatId(chatDTO.chatId());
        return chat;
    }
}
