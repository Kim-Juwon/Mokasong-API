package com.mokasong.common.util;

import com.mokasong.common.state.MessageSendPurpose;
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
public class MessageSender {
    @Value("${coolsms.api-key}")
    private String API_KEY;
    @Value("${coolsms.api-secret}")
    private String API_SECRET;
    @Value("${coolsms.from}")
    private String FROM;

    public SingleMessageSentResponse sendMessageToOne(MessageSendPurpose purpose, String to, String verificationCode) throws Exception {
        String text = this.getSuitedText(purpose);
        text = String.format(text, verificationCode);

        Message message = new Message();
        message.setFrom(this.FROM);
        message.setTo(to);
        message.setText(text);

        return NurigoApp.INSTANCE
                .initialize(API_KEY, API_SECRET, "https://api.coolsms.co.kr")
                .sendOne(new SingleMessageSendingRequest(message));
    }

    private String getSuitedText(MessageSendPurpose purpose) {
        String message;

        switch (purpose) {
            case VERIFY_CELLPHONE_NUMBER:
                message = "[Mokasong](회원가입 휴대전화 인증) 인증번호는 [%s]입니다.";
                break;
            case FIND_EMAIL:
                message = "[Mokasong](이메일 찾기 인증) 인증번호는 [%s]입니다.";
                break;
            case FIND_PASSWORD:
                message = "[Mokasong](비밀번호 찾기 인증) 인증번호는 [%s]입니다.";
                break;
            default:
                throw new IllegalArgumentException("MessageSendPurpose Enum에 정의된 값만 넣어주세요.");
        }

        return message;
    }
}
