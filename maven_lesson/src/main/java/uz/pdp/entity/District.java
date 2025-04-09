package uz.pdp.entity;

import lombok.Builder;
import lombok.ToString;

import java.util.UUID;



@ToString(callSuper = true)
public class District extends Region{
    private String name;

    @Builder(builderMethodName = "districtBuilder", buildMethodName = "districtBuild")
    public District(UUID id, String regionName, Integer countOfDistricts, String name) {
        super(id, regionName, countOfDistricts);
        this.name = name;
    }
}
