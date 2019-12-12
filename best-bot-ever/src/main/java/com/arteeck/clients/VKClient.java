package com.arteeck.clients;

import com.arteeck.commands.*;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;

public class VKClient extends AbstractClient {
    private VkApiClient vk;
    private GroupActor group;

    public VkApiClient getVk() {
        return vk;
    }

    public GroupActor getGroup() {
        return group;
    }


    public VKClient() {
        commands.put("/echo", EchoCommand::new);
        commands.put("/help", HelpCommand::new);
        commands.put("/copyfromtg", CopyFromTgCommand::new);
        commands.put("/authors", AuthorsCommand::new);
        commands.put("/remove", RemoveCommand::new);
        commands.put("/state", StateCommand::new);
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        int groupId = 186946532;
        String accessToken = "bda0a5f98bb0f7096b22ee4e2a28246405489da" +
                "f14ce8477f0794de854614d4ba26f0532456bf8766446e";
        group = new GroupActor(groupId, accessToken);
    }

    private static int getRandomID() {
        byte[] bytes = new byte[]{1, 2, 3, 4};
        int integer = (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
        return (int) (Math.random() * 100000);
    }

    @Override
    public void SendMessage(String text, int chatId) {
        try {
            vk.messages()
                    .send(group)
                    .peerId(chatId)
                    .randomId(VKClient.getRandomID())
                    .message(text)
                    .execute();
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
        }
    }
}

