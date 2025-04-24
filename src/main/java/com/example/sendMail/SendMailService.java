package com.example.sendMail;

import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ValidationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

@Service
public class SendMailService {

    private final JavaMailSender javaMailSender;

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

    public void validateWithXsdMail(MailRequest mailRequest){
        try{
            // Validazione con XSD
            JAXBContext jaxbContext = JAXBContext.newInstance(MailRequest.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(mailRequest, writer);
            String xmlString = writer.toString();
            // Caricamento schema XSD
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File mainXsd = new File("src/main/resources/xsd/sendMail.xsd");
            Schema schema = schemaFactory.newSchema(new Source[] {
                    new StreamSource(mainXsd)
            });
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlString)));
            logger.info("Validazione riuscita");
        }catch(Exception e){
            throw new ValidationException("Errore durante la validazione XML: " + e.getMessage());
        }
    }

    public void validateWithXsdPECMail(PECMailRequest pecMailRequest){
        try{
            // Validazione con XSD
            JAXBContext jaxbContext = JAXBContext.newInstance(PECMailRequest.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(pecMailRequest, writer);
            String xmlString = writer.toString();
            // Caricamento schema XSD
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File mainXsd = new File("src/main/resources/xsd/sendMailPEC.xsd");
            Schema schema = schemaFactory.newSchema(new Source[] {
                    new StreamSource(mainXsd)
            });
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlString)));
            logger.info("Validazione riuscita");
        }catch(Exception e){
            throw new ValidationException("Errore durante la validazione XML: " + e.getMessage());
        }
    }

}
