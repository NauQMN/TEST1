package com.e0bmanager.server_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "khu_vuc")
@Getter @Setter
public class KhuVuc {
    @Id
    private Integer id;

    @Column(name = "ten_khu_vuc")
    private String tenKhuVuc;
}
