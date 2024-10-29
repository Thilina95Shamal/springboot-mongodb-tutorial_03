package com.example.proj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "address")
// This means that in the json output it includes only the not-null values ( properties which has a values only)
// Eg: If `address` is null, it won't be included in the JSON output.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

    private String address1;
    private String address2;
    private String city;
}
