package com.app.ecom.dto;

import com.app.ecom.model.Address;
import com.app.ecom.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProdcutResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;
    private Address address;
}
