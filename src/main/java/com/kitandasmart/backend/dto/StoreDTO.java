package com.kitandasmart.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)  // This will ignore any extra fields like "id"
public class StoreDTO {
    private String store_name;
    private String store_location;
    private List<ProductDTO> products;

    // Getters and Setters
    public String getStore_name() { return store_name; }
    public void setStore_name(String store_name) { this.store_name = store_name; }

    public String getStore_location() { return store_location; }
    public void setStore_location(String store_location) { this.store_location = store_location; }

    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }
}
