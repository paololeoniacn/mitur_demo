package com.example.demo.service;

import com.example.demo.dto.MailRequest;
import com.example.demo.dto.PECMailRequest;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {

    private JavaMailSender javaMailSender;

    private static final Logger logger = LogManager.getLogger(SendMailService.class);

    @Autowired
    public SendMailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(MailRequest mailRequest){
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =  new MimeMessageHelper(mimeMessage, true, "UTF-8");
            if(mailRequest.getFrom() != null && !mailRequest.getFrom().isEmpty()){
                helper.setFrom(mailRequest.getFrom());
            } else{
                helper.setFrom("noreply@italia.it");
            }
            helper.setTo(mailRequest.getTo().toArray(new String[0]));
            if(mailRequest.getCc() != null && !mailRequest.getCc().isEmpty()){
                helper.setCc(mailRequest.getCc().toArray(new String[0]));
            }
            if(mailRequest.getBcc() != null && !mailRequest.getBcc().isEmpty()){
                helper.setBcc(mailRequest.getBcc().toArray(new String[0]));
            }

            if (mailRequest.getReplyTo() != null && !mailRequest.getReplyTo().isEmpty()) {
                helper.setReplyTo(mailRequest.getReplyTo());
            }

            if(mailRequest.getSubject() != null && !mailRequest.getSubject().isEmpty()){
                helper.setSubject(mailRequest.getSubject());
            }
            if(mailRequest.getSentDate() != null){
                helper.setSentDate(mailRequest.getSentDate());
            }
            if(mailRequest.getBodyText() != null && !mailRequest.getBodyText().isEmpty()){
                helper.setText(mailRequest.getBodyText());
            }
            javaMailSender.send(mimeMessage);
            logger.info("Email inviata correttamente");
        } catch (Exception ex){
            logger.error("Errore nell'invio della mail: " + ex.getMessage(), ex);
            throw new RuntimeException("Errore nell'invio della mail: " + ex.getMessage(), ex);
        }
    }

    public void sendPEC(PECMailRequest pecMailRequest) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(pecMailRequest.getTo());
            helper.setSubject(pecMailRequest.getSubject());
            helper.setText(pecMailRequest.getBody());

            javaMailSender.send(mimeMessage);
            logger.info("PEC inviata correttamente");
        } catch (Exception ex) {
            logger.error("Errore nell'invio della PEC: " + ex.getMessage(), ex);
            throw new RuntimeException("Errore nell'invio della PEC: " + ex.getMessage(), ex);
        }
    }

}
