package com.mokasong.common.util;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsSes {
    private final AmazonSimpleEmailServiceAsync amazonSimpleEmailServiceAsync;

    public AwsSes(AmazonSimpleEmailServiceAsync amazonSimpleEmailServiceAsync) {
        this.amazonSimpleEmailServiceAsync = amazonSimpleEmailServiceAsync;
    }

    @Value("${aws.ses.from}")
    private String FROM;

    public SendEmailResult sendEmail(String to, String subject, String htmlBody) {
        SendEmailRequest request = new SendEmailRequest()
            .withDestination(
                    new Destination().withToAddresses(to))
            .withMessage(new Message()
                    .withBody(new Body()
                            .withHtml(new Content()
                                    .withCharset("UTF-8").withData(htmlBody)))
                    .withSubject(new Content()
                            .withCharset("UTF-8").withData(subject)))
            .withSource(FROM);

        return amazonSimpleEmailServiceAsync.sendEmail(request);
    }
}
