package org.exercise.handlers.interfaces;

import org.exercise.model.Message;

public interface MessageHandler {
    String handleRequest(Message m);
    void printResponse(Message m);
}
