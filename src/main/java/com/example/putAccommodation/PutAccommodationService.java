package com.example.putAccommodation;

import com.example.demo.SlugifyService;
import com.example.uploads3aem.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class PutAccommodationService {

    private final S3Service s3Service;

    private final ObjectMapper objectMapper;

    private final SlugifyService slugifyService;

    private static final Logger logger = LogManager.getLogger(PutAccommodationService.class);

    public PutAccommodationService(S3Service s3Service, ObjectMapper objectMapper, SlugifyService slugifyService){
        this.s3Service = s3Service;
        this.objectMapper = objectMapper;
        this.slugifyService = slugifyService;
    }

    public void putAccommodation(PutAccommodationRequest putAccommodationRequest){

        String normalizedName = slugifyService.normalize(putAccommodationRequest.getName());
        String normalizedRegion = slugifyService.normalize(putAccommodationRequest.getRegion());
        String normalizedCity = slugifyService.normalize(putAccommodationRequest.getCity());

        List<String> imagesURL = putAccommodationRequest.getPhotos();
        List<String> uploadedImagePaths = new ArrayList<>();
        // Ciclo la lista di immagini e carico su S3 ogni immagine dopo aver creato il path
        for (int i = 0; i < imagesURL.size(); i++) {
            String indexPhoto = imagesURL.get(i);
            String finalPath = Utils.pathBuilder(imagesURL.get(i),normalizedName, normalizedRegion, normalizedCity, "/content/dam/tdh-infocamere/it/accommodations/", indexPhoto);
            try{
                s3Service.uploadImageFromUrl(imagesURL.get(i), finalPath);
                uploadedImagePaths.add(finalPath);
                logger.info("Immagine {} caricata correttamente", indexPhoto);
            } catch(IOException ex){
                logger.info("Caricamento immagine su S3 non riuscito: " + ex.getMessage());
            }
        }
        RenderAccommodationAEM renderAccommodationAEM = renderJson(putAccommodationRequest, uploadedImagePaths); // richiamo il metodo per la creazione del JSON da salvare su s3
        String finalPathJson = pathBuilderJson(true, normalizedName, normalizedCity, normalizedRegion, "/content/dam/tdh-infocamere/it/accommodations/" );  // metodo per settare il path (SetPathS3)
        // TODO: richiamare metodo per caricare su S3 il JSON
    }

    public void postAccommodation(PutAccommodationRequest putAccommodationRequest){
        String normalizedName = slugifyService.normalize(putAccommodationRequest.getName());
        String normalizedRegion = slugifyService.normalize(putAccommodationRequest.getRegion());
        String normalizedCity = slugifyService.normalize(putAccommodationRequest.getCity());

        List<String> imagesURL = putAccommodationRequest.getPhotos();
        List<String> uploadedImagePaths = new ArrayList<>();// Ciclo la lista di immagini e carico su S3 ogni immagine dopo aver creato il path
        for (int i = 0; i < imagesURL.size(); i++) {
            String indexPhoto = imagesURL.get(i);
            String finalPath = Utils.pathBuilder(imagesURL.get(i),normalizedName, normalizedRegion, normalizedCity, "/content/dam/tdh-infocamere/it/accommodations/", indexPhoto);
            try{
                s3Service.uploadImageFromUrl(imagesURL.get(i), finalPath);
                uploadedImagePaths.add(finalPath);
                logger.info("Immagine {} caricata correttamente", indexPhoto);
            } catch(IOException ex){
                logger.info("Caricamento immagine su S3 non riuscito: " + ex.getMessage());
            }
        }

        RenderAccommodationAEM renderAccommodationAEM = renderJson(putAccommodationRequest, uploadedImagePaths); // richiamo il metodo per la creazione del JSON da salvare su s3
        String finalPathJson = pathBuilderJson(false, normalizedName, normalizedCity, normalizedRegion, "/content/dam/tdh-infocamere/it/accommodations/");  // metodo per settare il path (SetPathS3)
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
            File mainXsd = new File("src/main/resources/xsd/putAccommodation.xsd");
            File importedXsd = new File("src/main/resources/xsd/putRequest.xsd");
            Schema schema = schemaFactory.newSchema(new Source[] {
                    new StreamSource(mainXsd),
                    new StreamSource(importedXsd)
            });
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlString)));
            logger.info("Validazione riuscita");
        }catch(Exception e){
            throw new ValidationException("Errore durante la validazione XML: " + e.getMessage());
        }
    }

    // metodo che crea il Json RenderAccommodationAEM da salvare su s3 mappandolo dalla request
    public RenderAccommodationAEM renderJson (PutAccommodationRequest putAccommodationRequest, List<String> uploadedImagePaths) {
        RenderAccommodationAEM renderAccommodationAEM = new RenderAccommodationAEM();
        renderAccommodationAEM.setIdentifier(putAccommodationRequest.getIdentifier());
        renderAccommodationAEM.setFiscalCod(putAccommodationRequest.getFiscalCod());
        if(putAccommodationRequest.getVatCod() != null && !putAccommodationRequest.getVatCod().isEmpty()){
            renderAccommodationAEM.setPIva(putAccommodationRequest.getVatCod());
        }
        renderAccommodationAEM.setStatus("OPERATIONAL");
        renderAccommodationAEM.setInsegna(putAccommodationRequest.getName());
        renderAccommodationAEM.setOfficialName(putAccommodationRequest.getName());
        renderAccommodationAEM.setLanguage("it");
        if(putAccommodationRequest.getCountry() != null && !putAccommodationRequest.getCountry().isEmpty()){
            renderAccommodationAEM.setCountry(putAccommodationRequest.getCountry());
        }
        if(putAccommodationRequest.getProvince() != null && !putAccommodationRequest.getProvince().isEmpty()){
            renderAccommodationAEM.setProvince(putAccommodationRequest.getProvince());
        }
        renderAccommodationAEM.setCity(putAccommodationRequest.getCity());
        if(putAccommodationRequest.getPostalCode() != null && !putAccommodationRequest.getPostalCode().isEmpty()){
            renderAccommodationAEM.setPostalCode(putAccommodationRequest.getPostalCode());
        }
        if(putAccommodationRequest.getStreetName() != null && !putAccommodationRequest.getStreetName().isEmpty()){
            renderAccommodationAEM.setStreetName(putAccommodationRequest.getStreetName());
        }
        if(putAccommodationRequest.getStreetNumber() != null && !putAccommodationRequest.getStreetNumber().isEmpty()){
            renderAccommodationAEM.setStreetNumber(putAccommodationRequest.getStreetNumber());
        }
        renderAccommodationAEM.setFullAddress(putAccommodationRequest.getFullAddress());
        renderAccommodationAEM.setRegion(putAccommodationRequest.getRegion());
        renderAccommodationAEM.setCategory("lodging");
        renderAccommodationAEM.setPrimaryTag("primary-tag/accommodations");
        renderAccommodationAEM.setDestinationType("destination-type/accommodations");
        renderAccommodationAEM.setPoiTelephoneNumber(putAccommodationRequest.getPhoneNumber());
        if(putAccommodationRequest.getWebSite() != null && !putAccommodationRequest.getWebSite().isEmpty()){
            renderAccommodationAEM.setPoiWebsiteUrl(putAccommodationRequest.getWebSite());
        }
        if(putAccommodationRequest.getMailAddress() != null && !putAccommodationRequest.getMailAddress().isEmpty()){
            renderAccommodationAEM.setPoiEmail(putAccommodationRequest.getMailAddress());
        }
        if(putAccommodationRequest.getGoogleWebAddress() != null && !putAccommodationRequest.getGoogleWebAddress().isEmpty()){
            renderAccommodationAEM.setWebsiteGoogle(putAccommodationRequest.getGoogleWebAddress());
        }
        renderAccommodationAEM.setDescription(putAccommodationRequest.getDescription());
        if(putAccommodationRequest.getIsoCertification() != null && !putAccommodationRequest.getIsoCertification().isEmpty()){
            renderAccommodationAEM.setIsoCert(putAccommodationRequest.getIsoCertification());
        }
        renderAccommodationAEM.setImages(uploadedImagePaths);
        renderAccommodationAEM.setType(putAccommodationRequest.getAccomodationType());
        if(putAccommodationRequest.getRating() != null && !putAccommodationRequest.getRating().isEmpty()){
            renderAccommodationAEM.setStarRating(putAccommodationRequest.getRating());
        }
        renderAccommodationAEM.setListOfServices(putAccommodationRequest.getListOfService());
        renderAccommodationAEM.setRoomListOfServices(putAccommodationRequest.getRoomService());
        renderAccommodationAEM.setPaymentMethods(putAccommodationRequest.getPaymentMethods());
        renderAccommodationAEM.setCheckIn(putAccommodationRequest.getCheckIn());
        renderAccommodationAEM.setCheckOut(putAccommodationRequest.getCheckOut());
        if(putAccommodationRequest.getHotelChain() != null && !putAccommodationRequest.getHotelChain().isEmpty()){
            renderAccommodationAEM.setHotelChain(putAccommodationRequest.getHotelChain());
        }
        renderAccommodationAEM.setCin(putAccommodationRequest.getCin());
        return renderAccommodationAEM;
    }

    public String pathBuilderJson(Boolean isUpdate, String name, String city, String region, String initialPathAccommodation){
        String finalName = name.length() > 80 ? name.substring(0, 80) : name;
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        String action = isUpdate ? "destination_update_" : "destination_put_";
        String finalPath = initialPathAccommodation
                    + region.toLowerCase() + "/"
                    + city.toLowerCase() + "/"
                    + name.toLowerCase() + "/"
                    + action + finalName
                    + "_accommodation_"
                    + timeStamp;
            return finalPath;
    }

    // metodo per creare Json da mandare a CRM con la coda
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
