package com.hungum.product.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document(collection = "Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    @Id
    private String id;

    private String name;

    private List<String> possibleFacets;

    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPossibleFacets() {
        return possibleFacets;
    }
    public void setPossibleFacets(List<String> possibleFacets) {
        this.possibleFacets = possibleFacets;
    }

    public Category(String name) {
        this.name = name;
    }
}
