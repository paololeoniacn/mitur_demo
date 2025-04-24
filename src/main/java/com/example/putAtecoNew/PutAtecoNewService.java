package com.example.putAtecoNew;

import com.example.demo.SlugifyService;
import com.example.putAccommodation.Utils;
import com.example.uploads3aem.S3Request;
import com.example.uploads3aem.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class PutAtecoNewService {

    private final S3Service s3Service;

    private final ObjectMapper objectMapper;

    private final SlugifyService slugifyService;

    private static final Logger logger = LogManager.getLogger(PutAtecoNewService.class);

    public PutAtecoNewService(S3Service s3Service, SlugifyService slugifyService, ObjectMapper objectMapper){
        this.s3Service = s3Service;
        this.slugifyService = slugifyService;
        this.objectMapper = objectMapper;
    }

    public void putAtecoNew(PutAtecoNewRequest putAtecoNewRequest, boolean isUpdate){
        String normalizedName = slugifyService.normalize(putAtecoNewRequest.getName());
        String normalizedRegion = slugifyService.normalize(putAtecoNewRequest.getRegion());
        String normalizedCity = slugifyService.normalize(putAtecoNewRequest.getCity());

        List<String> imagesURL = putAtecoNewRequest.getPhotos();
        List<String> uploadedImagePaths = new ArrayList<>();

        for(int i = 0; i < imagesURL.size(); i++){
            String initialPath = getInitialPath(putAtecoNewRequest);
            String indexPhoto = String.valueOf(i);
            String finalPath = Utils.pathBuilder(imagesURL.get(i), normalizedName, normalizedRegion, normalizedCity, initialPath, indexPhoto);
            try{
                logger.info("Caricamento immagine {} al path {} in corso", indexPhoto, finalPath);
                byte[] imageBytes = Utils.downloadImage(imagesURL.get(i));
                s3Service.uploadImage(imageBytes, finalPath);
                uploadedImagePaths.add(finalPath);
                logger.info("Immagine {} caricata correttamente al path {}", indexPhoto, finalPath);
                // logger.info("[MOCK] Simulazione upload immagine al path: {}", finalPath);
            } catch(Exception ex){
                logger.info("Caricamento immagine {} al path {} non riuscito: {}", indexPhoto, finalPath, ex.getMessage());
            }
        }
        RenderAtecoNewAEM renderAtecoNewAEM = renderAtecoNew(putAtecoNewRequest, uploadedImagePaths);
        String initialPathJson = getInitialPath(putAtecoNewRequest);
        String finalPathJson = pathBuilderJson(isUpdate, normalizedName, normalizedCity, normalizedRegion, initialPathJson);
        String jsonString = renderJsonToString(renderAtecoNewAEM);
        S3Request s3Request = new S3Request(finalPathJson, jsonString);
        s3Service.process(s3Request);
        logger.info("JSON caricato correttamente al path {}", finalPathJson);
        // logger.info("[MOCK] Simulazione salvataggio JSON: path={} content={}", finalPathJson, jsonString);
    }

    public RenderAtecoNewAEM renderAtecoNew(PutAtecoNewRequest putAtecoNewRequest, List<String> uploadedImagePaths) {
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
        renderPeriods(putAtecoNewRequest, renderAtecoNewAEM);
        renderAtecoNewAEM.setDescription(putAtecoNewRequest.getDescription());
        renderAtecoNewAEM.setImages(uploadedImagePaths);
        return renderAtecoNewAEM;
    }

    public RenderAtecoNewAEM renderPeriods(PutAtecoNewRequest putAtecoNewRequest, RenderAtecoNewAEM renderAtecoNewAEM){
        Map<String, List<HoursType>> hoursMap = new HashMap<>();
        OpeningHoursType openingHours = putAtecoNewRequest.getOpeningHoursType();

        if (openingHours != null) {
            hoursMap.put("1", openingHours.getMon());
            hoursMap.put("2", openingHours.getTue());
            hoursMap.put("3", openingHours.getWed());
            hoursMap.put("4", openingHours.getThu());
            hoursMap.put("5", openingHours.getFri());
            hoursMap.put("6", openingHours.getSat());
            hoursMap.put("7", openingHours.getSun());
        }

        for (Map.Entry<String, List<HoursType>> entry : hoursMap.entrySet()) {
            String day = entry.getKey();
            List<HoursType> hoursList = entry.getValue();

            PeriodsType periods = new PeriodsType();
            List<PeriodType> open = new ArrayList<>();
            List<PeriodType> close = new ArrayList<>();

            if (hoursList != null) {
                for (HoursType h : hoursList) {
                    PeriodType openPeriod = new PeriodType();
                    openPeriod.setTime(h.getStart());
                    openPeriod.setDay(day);
                    open.add(openPeriod);

                    PeriodType closePeriod = new PeriodType();
                    closePeriod.setTime(h.getEnd());
                    closePeriod.setDay(day);
                    close.add(closePeriod);
                }
            }

            periods.setOpen(open);
            periods.setClose(close);

            switch (day) {
                case "1": renderAtecoNewAEM.setPeriodsMon(List.of(periods)); break;
                case "2": renderAtecoNewAEM.setPeriodsTue(List.of(periods)); break;
                case "3": renderAtecoNewAEM.setPeriodsWed(List.of(periods)); break;
                case "4": renderAtecoNewAEM.setPeriodsThu(List.of(periods)); break;
                case "5": renderAtecoNewAEM.setPeriodsFri(List.of(periods)); break;
                case "6": renderAtecoNewAEM.setPeriodsSat(List.of(periods)); break;
                case "7": renderAtecoNewAEM.setPeriodsSun(List.of(periods)); break;
            }
        }
        return renderAtecoNewAEM;
    }

    public String renderJsonToString(RenderAtecoNewAEM renderAtecoNewAEM){
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(renderAtecoNewAEM);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore nella creazione della stringa JSON: " + e.getMessage());
        }
        return jsonString;
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
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
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

}
