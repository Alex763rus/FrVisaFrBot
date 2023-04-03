package com.example.my.frvisafrbot.model.jpa;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Objects;

@Data
public class User {

    private Long chatId;
    private String firstName;

    private String lastName;

    private String userName;

    private Timestamp registeredAt;

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getChatId().equals(user.getChatId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChatId());
    }
}
