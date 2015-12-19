package de.cineaste.persistence;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.entity.NearbyMessage;


/**
 * Created by marcelgross on 18.11.15.
 */
public class NearbyMessageHandler {

    private static NearbyMessageHandler instance;

    private List<NearbyMessage> messages;

    public static NearbyMessageHandler getInstance() {

        return instance == null ? instance = new NearbyMessageHandler() : instance;
    }

    public NearbyMessageHandler() {
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
