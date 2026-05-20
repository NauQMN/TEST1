package com.e0bmanager.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private Integer id;
    private String username;
    private String password;
    private String fullname;
    private String role;
    private Integer status;
    private String nhanVienId;
    private String email;

}