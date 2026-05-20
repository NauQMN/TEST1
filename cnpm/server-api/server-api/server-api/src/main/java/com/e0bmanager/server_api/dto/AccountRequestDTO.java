package com.e0bmanager.server_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountRequestDTO {
    private Integer accountId;
    private String username;
    private String hoTen;
    private String chucVu;
    private String sdt;
    private LocalDate ngaySinh;
}