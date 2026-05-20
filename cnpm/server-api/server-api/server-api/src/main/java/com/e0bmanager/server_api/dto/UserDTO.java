package com.e0bmanager.server_api.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String fullname;
    private String sdt;
    private String cccd;
    private String phone;
    private String email;
    private String avatar;
    private String password;
}