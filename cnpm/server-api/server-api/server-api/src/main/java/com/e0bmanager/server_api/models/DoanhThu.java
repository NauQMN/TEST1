package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "doanh_thu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoanhThu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private LocalDate ngay;

    private Double tongDoanhThu;
    private Integer soLuongDon;

    public DoanhThu(LocalDate ngay, Double tongDoanhThu, Integer soLuongDon) {
        this.ngay = ngay;
        this.tongDoanhThu = tongDoanhThu;
        this.soLuongDon = soLuongDon;
    }

}