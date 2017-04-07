package com.f.gupiao.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fei on 2017/4/6.
 */
@Entity
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "seq_stock")
    @SequenceGenerator(name = "seq_stock",sequenceName = "seq_stock")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String code;

    @Column
    private String sDate;
    @Column
    private double price;

    @Column
    private Integer fall;

    public Integer getFall() {
        return fall;
    }

    public void setFall(Integer fall) {
        this.fall = fall;
    }

    public Stock() {
    }

    public Stock(String name, String code, double price, String sDate) {
        this.name = name;
        this.code = code;
        this.sDate = sDate;
        this.price = price;
    }

    public Stock(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public Long getId() {

        return id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
