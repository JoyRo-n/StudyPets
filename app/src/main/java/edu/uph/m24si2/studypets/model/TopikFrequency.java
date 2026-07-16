package edu.uph.m24si2.studypets.model;

// Model khusus untuk hasil query GROUP BY topik
// Room butuh class yang fieldnya persis sama dengan kolom yang di-return query
public class TopikFrequency {
    public String topik  = "";
    public int    jumlah = 0;
}
