package com.mokasong.util;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsSes {
    @Autowired
    private AmazonSimpleEmailServiceAsync amazonSimpleEmailServiceAsync;
    @Value("${aws.ses.from}")
    private String FROM;

    public SendEmailResult sendEmail(String to, String subject, String textBody) {
        SendEmailRequest request = new SendEmailRequest()
            .withDestination(
                    new Destination().withToAddresses(to))
            .withMessage(new Message()
                    .withBody(new Body()
                            .withText(new Content()
                                    .withCharset("UTF-8").withData(textBody)))
                    .withSubject(new Content()
                            .withCharset("UTF-8").withData(subject)))
            .withSource(FROM);

        return amazonSimpleEmailServiceAsync.sendEmail(request);
    }
}
