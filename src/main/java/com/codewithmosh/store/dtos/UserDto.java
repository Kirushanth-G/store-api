package com.codewithmosh.store.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {
    @JsonIgnore
    private Long id;
    @JsonProperty("full_name")
    private String name;
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;
}
