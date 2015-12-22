package de.cineaste.android.persistence;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.NearbyMessage;

public class NearbyMessageHandler {

    private static NearbyMessageHandler instance;

    private final List<NearbyMessage> messages;

    public static NearbyMessageHandler getInstance() {

        return instance == null ? instance = new NearbyMessageHandler() : instance;
    }

    private NearbyMessageHandler() {
        messages = new ArrayList<>();
    }

    public void addMessage(NearbyMessage message) {
        messages.add(message);
    }

    public void addMessages(List<NearbyMessage> message) {
        messages.addAll(message);
    }

    public void removeMessage(NearbyMessage message) {
        messages.remove(message);
    }

    public List<NearbyMessage> getMessages() {
        return messages;
    }

    public int getSize() {
        return messages.size();
    }

    public void clearMessages() {
        messages.clear();
    }
}
