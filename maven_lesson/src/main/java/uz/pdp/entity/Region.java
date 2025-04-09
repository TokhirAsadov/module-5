package uz.pdp.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.ToString;

import java.util.UUID;

@ToString
//@Builder(
//        builderMethodName = "quruvchi",
//        buildMethodName = "qurish"
//)
@Builder
public class Region {
    private UUID id;
    private String regionName;
    private Integer countOfDistricts;
}
