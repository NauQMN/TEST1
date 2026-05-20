package com.e0bmanager.server_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor // Dùng Lombok hoặc tự generate Getter/Setter
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private String iconPath;

    // Một danh mục có nhiều sản phẩm
    @OneToMany(mappedBy = "danhMuc", cascade = CascadeType.ALL)
    @JsonIgnore // Tránh vòng lặp vô tận khi biến thành JSON
    private List<SanPham> danhMucSanPham;
}