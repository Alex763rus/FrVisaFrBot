package com.example.my.frvisafrbot.enums;

public enum Emoji {

    ARROW_UP (":arrow_up:"),
    ARROW_DOWN (":arrow_down:"),
    SHOPPING_TROLLEY (":shopping_trolley:"),
    LOCK (":lock:"),
    UNLOCK (":unlock:"),
    QUESTION (":question:"),
    MOYAI(":moyai:"),
    MONEY_BAG(":moneybag:"),
    WARNING(":warning:"),
    ROBOT_FACE(":robot_face:");

    private String code;

    Emoji(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
