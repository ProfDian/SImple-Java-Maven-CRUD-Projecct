package com.example.model;

public class Akademik {
    private Long id;
    private String nim;
    private double ipk;
    private int semester;
    private String jurusan;
    private String fakultas;
    
    // Getter dan Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNim() {
        return nim;
    }
    
    public void setNim(String nim) {
        this.nim = nim;
    }
    
    public double getIpk() {
        return ipk;
    }
    
    public void setIpk(double ipk) {
        this.ipk = ipk;
    }
    
    public int getSemester() {
        return semester;
    }
    
    public void setSemester(int semester) {
        this.semester = semester;
    }
    
    public String getJurusan() {
        return jurusan;
    }
    
    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }
    
    public String getFakultas() {
        return fakultas;
    }
    
    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }
}