package com.tongxiangdb.dto;

public class AssetDTO {
    private Long id;
    private String name;
    private Double value;

    // Constructors
    public AssetDTO() {}

    public AssetDTO(Long id, String name, Double value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
