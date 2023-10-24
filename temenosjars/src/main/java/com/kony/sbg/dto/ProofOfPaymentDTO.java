package com.kony.sbg.dto;

public class ProofOfPaymentDTO {
    private String propType;
    private String propValue;
    private String name;


    public ProofOfPaymentDTO(String propType, String propValue, String name) {
        this.propType = propType;
        this.propValue = propValue;
        this.name = name;
    }



    public String getPropType() {
        return this.propType;
    }

    public void setPropType(String propType) {
        this.propType = propType;
    }

    public String getPropValue() {
        return this.propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProofOfPaymentDTO{" +
                "popType='" + propType + '\'' +
                ", popValue='" + propValue + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
