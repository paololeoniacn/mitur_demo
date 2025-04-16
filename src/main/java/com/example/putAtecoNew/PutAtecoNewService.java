package com.example.putAtecoNew;

import com.example.demo.Slugifier;
import com.example.uploadImage.S3Service;
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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PutAtecoNewService {

    private final S3Service s3Service;

    private static final Logger logger = LogManager.getLogger(PutAtecoNewService.class);

    public PutAtecoNewService(S3Service s3Service){
        this.s3Service = s3Service;
    }

    public void postAtecoNew(PutAtecoNewRequest putAtecoNewRequest){
        // normalizzazione name, city, region -> TODO: metodo normalize da spostare dal Controller?
        String normalizedName = Slugifier.slugify(putAtecoNewRequest.getName());
        String normalizedRegion = Slugifier.slugify(putAtecoNewRequest.getRegion());
        String normalizedCity = Slugifier.slugify(putAtecoNewRequest.getCity());

        List<String> imagesURL = putAtecoNewRequest.getPhotos();
        List<String> uploadedImagePaths = new ArrayList<>();
        for(String imageURL : imagesURL){
            String initialPath = getInitialPath(putAtecoNewRequest);
            String finalPath = s3Service.pathBuilder(imageURL, normalizedName, normalizedRegion, normalizedCity, initialPath);
            try{
                s3Service.uploadImageFromUrl(imageURL, "bucketName", finalPath);
                uploadedImagePaths.add(finalPath);
            } catch(IOException ex){
                logger.info("Caricamento immagine su S3 non riuscito: " + ex.getMessage());
            }
        }
        RenderAtecoNewAEM renderAtecoNewAEM = renderJson(putAtecoNewRequest, uploadedImagePaths); // TODO mappare i parametri che mancano
        String initialPathJson = getInitialPath(putAtecoNewRequest);
        String finalPathJson = pathBuilderJson(false, normalizedName, normalizedCity, normalizedRegion, initialPathJson);
        // TODO: richiamare metodo per caricare su S3 il JSON
    }

    public void putAtecoNew(PutAtecoNewRequest putAtecoNewRequest){
        // normalizzazione name, city, region -> TODO: metodo normalize da spostare dal Controller?
        String normalizedName = Slugifier.slugify(putAtecoNewRequest.getName());
        String normalizedRegion = Slugifier.slugify(putAtecoNewRequest.getRegion());
        String normalizedCity = Slugifier.slugify(putAtecoNewRequest.getCity());

        List<String> imagesURL = putAtecoNewRequest.getPhotos();
        List<String> uploadedImagePaths = new ArrayList<>(); // Ciclo la lista di immagini e carico su S3 ogni immagine dopo aver creato il path
        for(String imageURL : imagesURL){
            String initialPath = getInitialPath(putAtecoNewRequest);
            String finalPath = s3Service.pathBuilder(imageURL,normalizedName, normalizedRegion, normalizedCity, "initialPath");
            try{
                s3Service.uploadImageFromUrl(imageURL, "bucketName", finalPath);
                uploadedImagePaths.add(finalPath);
            } catch(IOException ex){
                logger.info("Caricamento immagine su S3 non riuscito: " + ex.getMessage());
            }
        }
        RenderAtecoNewAEM renderAtecoNewAEM = renderJson(putAtecoNewRequest, uploadedImagePaths);
        // TODO: creare metodo per settare path per il JSON (isUpdate true)
        String initialPathJson = getInitialPath(putAtecoNewRequest);
        String finalPathJson = pathBuilderJson(true, normalizedName, normalizedCity, normalizedRegion, initialPathJson);
        // TODO: richiamare metodo per caricare su S3 il JSON
    }

    public void validateWithXsd(PutAtecoNewRequest putAtecoNewRequest){
        try{
            // Validazione con XSD
            JAXBContext jaxbContext = JAXBContext.newInstance(PutAtecoNewRequest.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(putAtecoNewRequest, writer);
            String xmlString = writer.toString();
            // Caricamento schema XSD
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File mainXsd = new File("src/main/resources/xsd/putAtecoNew.xsd");
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

    public RenderAtecoNewAEM renderJson (PutAtecoNewRequest putAtecoNewRequest, List<String> uploadedImagePaths) {
        RenderAtecoNewAEM renderAtecoNewAEM = new RenderAtecoNewAEM();
        renderAtecoNewAEM.setIdentifier(putAtecoNewRequest.getIdentifier());
        renderAtecoNewAEM.setFiscalCod(putAtecoNewRequest.getFiscalCod());
        if(putAtecoNewRequest.getVatCod() != null && !putAtecoNewRequest.getVatCod().isEmpty()){
            renderAtecoNewAEM.setPIva(putAtecoNewRequest.getVatCod());
        }
        renderAtecoNewAEM.setStatus("OPERATIONAL");
        renderAtecoNewAEM.setInsegna(putAtecoNewRequest.getName());
        renderAtecoNewAEM.setOfficialName(putAtecoNewRequest.getName());
        renderAtecoNewAEM.setLanguage("it");
        if(putAtecoNewRequest.getFullAddress() != null && !putAtecoNewRequest.getCountry().isEmpty()){
            renderAtecoNewAEM.setFullAddress(putAtecoNewRequest.getFullAddress());
        }
        if(putAtecoNewRequest.getCity() != null && !putAtecoNewRequest.getCity().isEmpty()){
            renderAtecoNewAEM.setCity(putAtecoNewRequest.getCity());
        }
        if(putAtecoNewRequest.getPostalCode() != null && !putAtecoNewRequest.getPostalCode().isEmpty()){
            renderAtecoNewAEM.setPostalCode(putAtecoNewRequest.getPostalCode());
        }
        if(putAtecoNewRequest.getProvince() != null && !putAtecoNewRequest.getProvince().isEmpty()){
            renderAtecoNewAEM.setProvince(putAtecoNewRequest.getProvince());
        }
        if(putAtecoNewRequest.getRegion() != null && !putAtecoNewRequest.getRegion().isEmpty()){
            renderAtecoNewAEM.setRegion(putAtecoNewRequest.getRegion());
        }
        if(putAtecoNewRequest.getCountry() != null && !putAtecoNewRequest.getCountry().isEmpty()){
            renderAtecoNewAEM.setCountry(putAtecoNewRequest.getCountry());
        }
        if(putAtecoNewRequest.getStreetName() != null && !putAtecoNewRequest.getStreetName().isEmpty()){
            renderAtecoNewAEM.setStreetName(putAtecoNewRequest.getStreetName());
        }
        if(putAtecoNewRequest.getStreetNumber() != null && !putAtecoNewRequest.getStreetNumber().isEmpty()){
            renderAtecoNewAEM.setStreetNumber(putAtecoNewRequest.getStreetNumber());
        }
        if(putAtecoNewRequest.getAlternativeAddress() != null && !putAtecoNewRequest.getAlternativeAddress().isEmpty()){
            renderAtecoNewAEM.setAlternativeAddress(putAtecoNewRequest.getAlternativeAddress());
        }
        renderAtecoNewAEM.setPrimaryAtecoCode(putAtecoNewRequest.getPrimaryAtecoCod());
        renderAtecoNewAEM.setCategory(getCategory(putAtecoNewRequest));
        renderAtecoNewAEM.setPrimaryTag(getPrimaryTag(putAtecoNewRequest));
        renderAtecoNewAEM.setDestinationType(getDestination(putAtecoNewRequest));
        renderAtecoNewAEM.setPoiTelephoneNumber(putAtecoNewRequest.getPhoneNumber());
        if(putAtecoNewRequest.getWebSite() != null && !putAtecoNewRequest.getWebSite().isEmpty()){
            renderAtecoNewAEM.setPoiWebsiteUrl(putAtecoNewRequest.getWebSite());
        }
        if(putAtecoNewRequest.getMailAddress() != null && !putAtecoNewRequest.getMailAddress().isEmpty()){
            renderAtecoNewAEM.setPoiEmail(putAtecoNewRequest.getMailAddress());
        }
        if(putAtecoNewRequest.getGoogleWebAddress() != null && !putAtecoNewRequest.getGoogleWebAddress().isEmpty()){
            renderAtecoNewAEM.setWebsiteGoogle(putAtecoNewRequest.getGoogleWebAddress());
        }
        if(putAtecoNewRequest.getIsoCertification() != null && !putAtecoNewRequest.getIsoCertification().isEmpty()){
            renderAtecoNewAEM.setIsoCert(putAtecoNewRequest.getIsoCertification());
        }
        // TODO: periods
        renderAtecoNewAEM.setDescription(putAtecoNewRequest.getDescription());
        renderAtecoNewAEM.setImages(uploadedImagePaths);
        return renderAtecoNewAEM;
    }

    public String getInitialPath(PutAtecoNewRequest putAtecoNewRequest){
        String date = "2025-04-01T00:00:00+02:00";
        OffsetDateTime startDateAtecoNew = OffsetDateTime.parse(date);
        OffsetDateTime localDateTime = OffsetDateTime.now();
        String atecoCod = putAtecoNewRequest.getPrimaryAtecoCod();
        boolean isBefore = localDateTime.isBefore(startDateAtecoNew);

        if((isBefore && AtecoCodeGroups.BEFORE_COD_MEETINGS.contains(atecoCod)) || (!isBefore && AtecoCodeGroups.AFTER_COD_MEETINGS.contains(atecoCod))){
            return AtecoPaths.PATH_MEETINGS;
        } else if((isBefore && AtecoCodeGroups.BEFORE_COD_BEACH.contains(atecoCod)) || (!isBefore && AtecoCodeGroups.AFTER_COD_BEACH.contains(atecoCod))){
            return AtecoPaths.PATH_BEACH;
        } else if((isBefore && AtecoCodeGroups.BEFORE_COD_ENTER.contains(atecoCod)) || (!isBefore && AtecoCodeGroups.AFTER_COD_ENTER.contains(atecoCod))){
            return AtecoPaths.PATH_ENTERTAINMENT;
        } else if((isBefore && AtecoCodeGroups.BEFORE_COD_INN.contains(atecoCod)) || (!isBefore && AtecoCodeGroups.AFTER_COD_INN.contains(atecoCod))){
            return AtecoPaths.PATH_INNOVATIONS;
        } else if(AtecoCodeGroups.COD_AIR_TRANS.contains(atecoCod)){
            return AtecoPaths.PATH_AIR_TRANSPORT;
        } else if(AtecoCodeGroups.COD_LAND_TRANS.contains(atecoCod)){
            return AtecoPaths.PATH_LAND_TRANSPORT;
        } else if(AtecoCodeGroups.COD_WATER_TRANS.contains(atecoCod)){
            return AtecoPaths.PATH_WATER_TRANSPORT;
        } else if((isBefore && AtecoCodeGroups.BEFORE_COD_THERM.contains(atecoCod)) || (!isBefore && AtecoCodeGroups.AFTER_COD_THERM.equals(atecoCod))){
            return AtecoPaths.PATH_THERMAL;
        }
        return AtecoPaths.PATH_FOOD;
    };

    public String pathBuilderJson(Boolean isUpdate, String name, String city, String region, String initialPathJson){
        String finalName = name.length() > 80 ? name.substring(0, 80) : name;
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        String action = isUpdate ? "destination_update_" : "destination_put_";

        Map<String, String> pathSuffixMap = Map.of(
                AtecoPaths.PATH_MEETINGS, "_meetings-and-exhibition_",
                AtecoPaths.PATH_BEACH, "_beach-facilities_",
                AtecoPaths.PATH_ENTERTAINMENT, "_entertainment_",
                AtecoPaths.PATH_INNOVATIONS, "_innovations_",
                AtecoPaths.PATH_AIR_TRANSPORT, "_air-transport_",
                AtecoPaths.PATH_LAND_TRANSPORT, "_land-transport_",
                AtecoPaths.PATH_WATER_TRANSPORT, "_water-transport_",
                AtecoPaths.PATH_THERMAL, "_thermal-baths_",
                AtecoPaths.PATH_FOOD, "_food-and-wine_"
        );

        String pathPart = pathSuffixMap.getOrDefault(initialPathJson, "_food-and-wine_");

        String finalPath = initialPathJson
                    + region.toLowerCase() + "/"
                    + city.toLowerCase() + "/"
                    + name.toLowerCase() + "/"
                    + action + finalName
                    + pathPart
                    + timeStamp;
        return finalPath;
    }

    public String getCategory(PutAtecoNewRequest putAtecoNewRequest){
        String initialPath = getInitialPath(putAtecoNewRequest);
        Map<String, String> categoryMap = Map.of(
                AtecoPaths.PATH_MEETINGS, "meetings-and-exhibition",
                AtecoPaths.PATH_BEACH, "beach-facilities",
                AtecoPaths.PATH_ENTERTAINMENT, "entertainment",
                AtecoPaths.PATH_INNOVATIONS, "innovations",
                AtecoPaths.PATH_AIR_TRANSPORT, "air-transport",
                AtecoPaths.PATH_LAND_TRANSPORT, "land-transport",
                AtecoPaths.PATH_WATER_TRANSPORT, "water-transport",
                AtecoPaths.PATH_THERMAL, "thermal-baths",
                AtecoPaths.PATH_FOOD, "food-and-wine"
        );
        return categoryMap.getOrDefault(initialPath, "_food-and-wine_");
    }

    public String getPrimaryTag(PutAtecoNewRequest putAtecoNewRequest){
        String initialPath = getInitialPath(putAtecoNewRequest);
        Map<String, String> primaryTagMap = Map.of(
                AtecoPaths.PATH_MEETINGS, "primary-tag/meetings-and-exhibition",
                AtecoPaths.PATH_BEACH, "primary-tag/beach-facilities",
                AtecoPaths.PATH_ENTERTAINMENT, "primary-tag/entertainment",
                AtecoPaths.PATH_INNOVATIONS, "primary-tag/innovations",
                AtecoPaths.PATH_AIR_TRANSPORT, "primary-tag/air-transport",
                AtecoPaths.PATH_LAND_TRANSPORT, "primary-tag/land-transport",
                AtecoPaths.PATH_WATER_TRANSPORT, "primary-tag/water-transport",
                AtecoPaths.PATH_THERMAL, "primary-tag/thermal-baths",
                AtecoPaths.PATH_FOOD, "primary-tag/food-and-wine"
        );
        return primaryTagMap.getOrDefault(initialPath, "_food-and-wine_");
    }

    public String getDestination(PutAtecoNewRequest putAtecoNewRequest){
        String initialPath = getInitialPath(putAtecoNewRequest);
        Map<String, String> destinationMap = Map.of(
                AtecoPaths.PATH_MEETINGS, "destination-type/meetings-and-exhibition",
                AtecoPaths.PATH_BEACH, "destination-type/beach-facilities",
                AtecoPaths.PATH_ENTERTAINMENT, "destination-type/entertainment",
                AtecoPaths.PATH_INNOVATIONS, "destination-type/innovations",
                AtecoPaths.PATH_AIR_TRANSPORT, "destination-type/air-transport",
                AtecoPaths.PATH_LAND_TRANSPORT, "destination-type/land-transport",
                AtecoPaths.PATH_WATER_TRANSPORT, "destination-type/water-transport",
                AtecoPaths.PATH_THERMAL, "destination-type/thermal-baths",
                AtecoPaths.PATH_FOOD, "destination-type/food-and-wine"
        );
        return destinationMap.getOrDefault(initialPath, "_food-and-wine_");
    }

    public void mapPeriods(PutAtecoNewRequest putAtecoNewRequest, RenderAtecoNewAEM renderAtecoNewAEM){
        putAtecoNewRequest.getOpeningHoursType().getMon();
    }
}
