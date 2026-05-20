package com.e0bmanager.server_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Thêm Setter nếu cần
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
