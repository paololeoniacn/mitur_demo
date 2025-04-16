package com.example.demo.service;

import com.example.demo.Slugifier;
import com.example.demo.dto.InputMessageToCRM;
import com.example.demo.dto.PutAccommodationRequest;
import com.example.demo.dto.RenderAccommodationAEM;
import com.example.demo.mapper.RenderAccomodationMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PutAccommodationService {

    private final S3Service s3Service;

    private final ObjectMapper objectMapper;

    private final RenderAccomodationMapper mapper;

    private static final Logger logger = LogManager.getLogger(PutAccommodationService.class);

    public PutAccommodationService(S3Service s3Service, ObjectMapper objectMapper, RenderAccomodationMapper mapper){
        this.s3Service = s3Service;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
    }

    public void putAccommodation(PutAccommodationRequest putAccommodationRequest){

        // normalizzazione name, city, region -> TODO: metodo normalize da spostare dal Controller?
        String normalizedName = Slugifier.slugify(putAccommodationRequest.getName());
        String normalizedRegion = Slugifier.slugify(putAccommodationRequest.getRegion());
        String normalizedCity = Slugifier.slugify(putAccommodationRequest.getCity());

        List<String> imagesURL = putAccommodationRequest.getPhotos();   // Ciclo la lista di immagini e carico su S3 ogni immagine dopo aver creato il path
        for(String imageURL : imagesURL){
            String finalPath = s3Service.pathBuilder(imageURL,normalizedName, normalizedRegion, normalizedCity, "initialPath");
            try{
                s3Service.uploadImageFromUrl(imageURL, "bucketName", finalPath);
            } catch(IOException ex){
                logger.info("Caricamento immagine su S3 non riuscito: " + ex.getMessage());
            }
        }
        RenderAccommodationAEM renderAccommodationAEM = renderJson(putAccommodationRequest); // richiamo il metodo per la creazione del JSON da salvare su s3
        String finalPathJson = s3Service.pathBuilderJson(true, normalizedName, normalizedCity, normalizedRegion, "initialPathAccomodation" );  // metodo per settare il path (SetPathS3)

        // TODO: richiamare metodo per caricare su S3 il JSON
    }

    public void postAccommodation(PutAccommodationRequest putAccommodationRequest){
        String normalizedName = Slugifier.slugify(putAccommodationRequest.getName());
        String normalizedRegion = Slugifier.slugify(putAccommodationRequest.getRegion());
        String normalizedCity = Slugifier.slugify(putAccommodationRequest.getCity());

        List<String> imagesURL = putAccommodationRequest.getPhotos();   // Ciclo la lista di immagini e carico su S3 ogni immagine dopo aver creato il path
        for(String imageURL : imagesURL){
            String finalPath = s3Service.pathBuilder(imageURL,normalizedName, normalizedRegion, normalizedCity, "initialPath");
            try{
                s3Service.uploadImageFromUrl(imageURL, "bucketName", finalPath);
            } catch(IOException ex){
                logger.info("Caricamento immagine su S3 non riuscito: " + ex.getMessage());
            }
        }

        RenderAccommodationAEM renderAccommodationAEM = renderJson(putAccommodationRequest); // richiamo il metodo per la creazione del JSON da salvare su s3
        String finalPathJson = s3Service.pathBuilderJson(false, normalizedName, normalizedCity, normalizedRegion, "initialPathAccomodation" );  // metodo per settare il path (SetPathS3)

        // TODO: richiamare metodo per caricare su S3 il JSON
    }

    // metodo per convertire l'oggetto in XML e validarlo con i file xsd
    public void validateWithXsd(PutAccommodationRequest putAccommodationRequest){
        try{
            // Validazione con XSD
            JAXBContext jaxbContext = JAXBContext.newInstance(PutAccommodationRequest.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(putAccommodationRequest, writer);
            String xmlString = writer.toString();
            // Caricamento schema XSD
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File("src/main/resources/xsd/putAccommodation.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlString)));
        }catch(Exception e){
            throw new ValidationException("Errore durante la validazione XML: " + e.getMessage());
        }
    }

    // metodo che crea il Json RenderAccommodationAEM da salvare su s3 mappandolo dalla request
    public RenderAccommodationAEM renderJson(PutAccommodationRequest putAccommodationRequest){
        return mapper.renderJson(putAccommodationRequest);
    }

    public InputMessageToCRM renderJsonToCRM(PutAccommodationRequest putAccommodationRequest, String trackingId, Boolean isUpdate){
        String currentTimeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        try {
            String jsonRequest = objectMapper.writeValueAsString(putAccommodationRequest); // Trasformazione in stringa per invio messaggio al CRM
            InputMessageToCRM inputMessageToCRM = new InputMessageToCRM();
            inputMessageToCRM.setType("Accomodation");
            if(isUpdate){
                inputMessageToCRM.setMethod("PUT");
            } else {
                inputMessageToCRM.setMethod("POST");
            }
            inputMessageToCRM.setIdentifier(putAccommodationRequest.getIdentifier());
            inputMessageToCRM.setCurrentTimeStamp(currentTimeStamp);
            inputMessageToCRM.setJsonRequest(jsonRequest);
            inputMessageToCRM.setTrackingId(trackingId);
            return inputMessageToCRM;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Render JSON non riuscito: " + e.getMessage());
        }
    }
}
