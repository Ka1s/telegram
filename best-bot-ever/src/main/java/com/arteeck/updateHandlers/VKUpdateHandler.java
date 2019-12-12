package com.arteeck.updateHandlers;

import com.arteeck.Application;
import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.messages.Message;

public class VKUpdateHandler extends CallbackApiLongPoll {
    public VKUpdateHandler(VkApiClient client, GroupActor actor) {
        super(client, actor);
    }

    @Override
    public void messageNew(Integer groupId, Message message) {
        Application.vkClient.HandleUpdate(message.getText(), message.getPeerId());
    }
}
