package com.arteeck;

public class Forwarder {
    private int vkChatId;
    private long tgChatId;
    private String userName;

    public Forwarder(int vkChatId, long tgChatId, String userName) {
        this.vkChatId = vkChatId;
        this.tgChatId = tgChatId;
        this.userName = userName;
    }

    public int getVkChatId() {
        return vkChatId;
    }

    public String getUserName() {
        return userName;
    }

    public long getTgChatId() {
        return tgChatId;
    }

    @Override
    public int hashCode() {
        return (int)tgChatId + vkChatId ^ userName.hashCode();
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Forwarder))
            return false;
        Forwarder otherForwarder = (Forwarder)obj;
        return tgChatId == otherForwarder.tgChatId &&
                vkChatId == otherForwarder.vkChatId &&
                userName.equals(otherForwarder.userName);
    }

    public String createForwardMessage(String tgChatTitle, String userName, String message) {
        return String.format("(%s) %s: %s", tgChatTitle, userName, message);
    }

    public String toStringValue() {
        return String.format("%d, %d, '%s'", tgChatId, vkChatId, userName);
    }
}
