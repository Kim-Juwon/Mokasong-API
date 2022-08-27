package com.mokasong.common.util;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *  https://github.com/coolsms/coolsms-java-examples 참고
 */

@Component
public class Coolsms {
    @Value("${coolsms.api-key}")
    private String API_KEY;
    @Value("${coolsms.api-secret}")
    private String API_SECRET;
    @Value("${coolsms.from}")
    private String FROM;

    public SingleMessageSentResponse sendMessageToOne(String to, String text) throws Exception {
        Message message = new Message();
        message.setFrom(FROM);
        message.setTo(to);
        message.setText(text);

        return NurigoApp.INSTANCE
                .initialize(API_KEY, API_SECRET, "https://api.coolsms.co.kr")
                .sendOne(new SingleMessageSendingRequest(message));
    }
}
