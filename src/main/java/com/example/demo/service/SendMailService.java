package com.example.demo.service;

import com.example.demo.controller.SendMailController;
import com.example.demo.dto.MailRequest;
import com.example.demo.dto.PECMailRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class SendMailService {

    private JavaMailSender javaMailSender;

    private static final Logger logger = LogManager.getLogger(SendMailService.class);

    // todo: mail in HTML, SimpleMailMessage gestisce solo le mail in testo semplice
    public void sendMail(MailRequest mailRequest){
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            if(mailRequest.getFrom() != null && !mailRequest.getFrom().isEmpty()){
                mailMessage.setFrom(mailRequest.getFrom());
            }
            mailMessage.setTo(mailRequest.getTo().toArray(new String[0]));
            if(mailRequest.getCc() != null && !mailRequest.getCc().isEmpty()){
                mailMessage.setCc(mailRequest.getCc().toArray(new String[0]));
            }
            if(mailRequest.getBcc() != null && !mailRequest.getBcc().isEmpty()){
                mailMessage.setBcc(mailRequest.getCc().toArray(new String[0]));
            }
            if(mailRequest.getReplyTo() != null && !mailRequest.getReplyTo().isEmpty()){
                String replyToString = String.join(",", mailRequest.getReplyTo());
                mailMessage.setReplyTo(replyToString);
            }
            if(mailRequest.getSubject() != null && !mailRequest.getSubject().isEmpty()){
                mailMessage.setSubject(mailRequest.getSubject());
            }
            if(mailRequest.getSentDate() != null){
                mailMessage.setSentDate(mailRequest.getSentDate());
            }
            if(mailRequest.getBodyText() != null && !mailRequest.getBodyText().isEmpty()){
                mailMessage.setText(mailRequest.getBodyText());
            }
            javaMailSender.send(mailMessage);
            logger.info("Email inviata correttamente");
        } catch (Exception ex){
            logger.error("Errore nell'invio della mail: " + ex.getMessage(), ex);
            throw new RuntimeException("Errore nell'invio della mail" + ex.getMessage(), ex);
        }
    }

    public void sendPec(PECMailRequest pecMailRequest) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(pecMailRequest.getTo());
            mailMessage.setSubject(pecMailRequest.getSubject());
            mailMessage.setText(pecMailRequest.getBody());
            javaMailSender.send(mailMessage);
            logger.info("Email inviata correttamente");
        } catch (Exception ex){
            logger.error("Errore nell'invio della mail: " + ex.getMessage(), ex);
            throw new RuntimeException("Errore nell'invio della mail" + ex.getMessage(), ex);
        }
    }

}
