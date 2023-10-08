package com.implementation.idp.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author saurabhpuri on 05/10/23
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private long id;
    private String name;
    private String email;
}
