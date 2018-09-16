package com.umarbhutta.xlightcompanion.okHttp.model;

import java.util.Date;

public class MessageResult {
    public MessageResult() {
    }

    public MessageResult(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String message;
    public String time;
}
