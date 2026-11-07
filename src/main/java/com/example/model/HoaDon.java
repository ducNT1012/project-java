
package com.example.model;

import java.time.LocalDate;

public class HoaDon {

    private String maHD;
    private LocalDate ngayLap;
    private String maKH;

    public HoaDon() {
    }

    public HoaDon(String maHD, LocalDate ngayLap, String maKH) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.maKH = maKH;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }
}
