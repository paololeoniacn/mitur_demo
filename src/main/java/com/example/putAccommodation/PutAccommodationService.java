package com.example.putAccommodation;

import com.example.demo.SlugifyService;
import com.example.uploads3aem.S3Request;
import com.example.uploads3aem.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
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

    private static final String PATH_ACCOMMODATION = "/content/dam/tdh-infocamere/it/accommodations/";


    public PutAccommodationService(S3Service s3Service, ObjectMapper objectMapper, SlugifyService slugifyService){
        this.s3Service = s3Service;
        this.objectMapper = objectMapper;
        this.slugifyService = slugifyService;
    }

    public void putAccommodation(PutAccommodationRequest putAccommodationRequest, boolean isUpdate){
        String normalizedName = slugifyService.normalize(putAccommodationRequest.getName());
        String normalizedRegion = slugifyService.normalize(putAccommodationRequest.getRegion());
        String normalizedCity = slugifyService.normalize(putAccommodationRequest.getCity());

        List<String> imagesURL = putAccommodationRequest.getPhotos();
        List<String> uploadedImagePaths = new ArrayList<>();

        for (int i = 0; i < imagesURL.size(); i++) { // Ciclo la lista di immagini e carico su S3 ogni immagine dopo aver creato il path
            String indexPhoto = String.valueOf(i);
            logger.info("indexPhoto: " + indexPhoto);
            String finalPath = Utils.pathBuilder(imagesURL.get(i),normalizedName, normalizedRegion, normalizedCity, PATH_ACCOMMODATION, indexPhoto);
            try{
                logger.info("Caricamento immagine {} al path {} in corso", indexPhoto, finalPath);
                byte[] imageBytes = Utils.downloadImage(imagesURL.get(i));
                s3Service.uploadImage(imageBytes, finalPath);
                uploadedImagePaths.add(finalPath);
                logger.info("Immagine {} caricata correttamente al path {}", indexPhoto, finalPath);
                //logger.info("[MOCK] Simulazione upload immagine al path: {}", finalPath);
            } catch(Exception ex){
                logger.info("Caricamento immagine {} al path {} non riuscito: {}", indexPhoto, finalPath, ex.getMessage());
            }
        }
        RenderAccommodationAEM renderAccommodationAEM = renderAccommodation(putAccommodationRequest, uploadedImagePaths); // creazione del JSON da salvare su s3
        String finalPathJson = pathBuilderJson(isUpdate, normalizedName, normalizedCity, normalizedRegion, PATH_ACCOMMODATION);  // metodo per settare il path (SetPathS3)
        String jsonString = renderJsonToString(renderAccommodationAEM);
        S3Request s3Request = new S3Request(finalPathJson, jsonString);
        s3Service.process(s3Request);
        logger.info("JSON caricato correttamente al path {}", finalPathJson);
        //logger.info("[MOCK] Simulazione salvataggio JSON: path={} content={}", finalPathJson, jsonString);
    }

    // metodo che crea il Json RenderAccommodationAEM da salvare su s3 mappandolo dalla request
    public RenderAccommodationAEM renderAccommodation (PutAccommodationRequest putAccommodationRequest, List<String> uploadedImagePaths) {
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
        renderAccommodationAEM.setType(putAccommodationRequest.getAccomodationType().getValue());
        if(putAccommodationRequest.getRating() != null && !putAccommodationRequest.getRating().isEmpty()){
            renderAccommodationAEM.setStarRating(putAccommodationRequest.getRating());
        }
        renderAccommodationAEM.setListOfServices(putAccommodationRequest.getListOfService().stream().map(ListOfService::getValue).toList());
        renderAccommodationAEM.setRoomListOfServices(putAccommodationRequest.getRoomService().stream().map(RoomService::getValue).toList());
        renderAccommodationAEM.setPaymentMethods(putAccommodationRequest.getPaymentMethods().stream().map(PaymentMethod::getValue).toList());
        renderAccommodationAEM.setCheckIn(putAccommodationRequest.getCheckIn());
        renderAccommodationAEM.setCheckOut(putAccommodationRequest.getCheckOut());
        if(putAccommodationRequest.getHotelChain() != null && !putAccommodationRequest.getHotelChain().isEmpty()){
            renderAccommodationAEM.setHotelChain(putAccommodationRequest.getHotelChain());
        }
        renderAccommodationAEM.setCin(putAccommodationRequest.getCin());
        return renderAccommodationAEM;
    }

    public String renderJsonToString(RenderAccommodationAEM renderAccommodationAEM){
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(renderAccommodationAEM);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore nella creazione della stringa JSON: " + e.getMessage());
        }
        return jsonString;
    }

    public String pathBuilderJson(Boolean isUpdate, String name, String city, String region, String initialPathAccommodation){
        String finalName = name.length() > 80 ? name.substring(0, 80) : name;
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
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

}
