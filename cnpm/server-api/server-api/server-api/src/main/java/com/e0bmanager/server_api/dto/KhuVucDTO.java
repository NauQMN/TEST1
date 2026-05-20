package com.e0bmanager.server_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhuVucDTO {
    private int id;
    private String ten_khu_vuc;
}