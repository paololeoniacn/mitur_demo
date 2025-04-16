package com.example.demo.mapper;

import com.example.demo.dto.PutAccommodationRequest;
import com.example.demo.dto.RenderAccommodationAEM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RenderAccomodationMapper {

    @Mapping(target = "status", constant = "OPERATIONAL")
    @Mapping(target = "officialName", source = "name")
    @Mapping(target = "insegna", source = "name")
    @Mapping(target = "language", constant = "it")
    @Mapping(target = "category", constant = "lodging")
    @Mapping(target = "primaryTag", constant = "primary-tag/accommodations")
    @Mapping(target = "destinationType", constant = "destination-type/accommodations")
    RenderAccommodationAEM renderJson(PutAccommodationRequest putAccommodationRequest);

}
