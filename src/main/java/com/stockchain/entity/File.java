package com.stockchain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer idUser;
    private Long size;
    private String name;


}
