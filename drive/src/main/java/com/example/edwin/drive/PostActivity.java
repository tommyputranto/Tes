package com.example.edwin.drive;

import android.app.Activity;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by edwinch on 8/13/15.
 */
@ParseClassName("Request")
public class PostActivity extends ParseObject {

    //Lokasi Asal
    public String getLokasiAsal(){
        return getString("LokasiAsal");
    }
    public void setLokasiAsal(String name){
        put("LokasiAsal", name);
    }

    //Lokasi Asal Description
    public String getLokasiAsalDesc(){
        return getString("LokasiAsalDesc");
    }

    //Lokasi Tujuan
    public String getLokasiTujuan(){
        return getString("LokasiTujuan");
    }

    //Lokasi Tujuan Description
    public String getLokasiTujuanDesc(){
        return getString("LokasiTujuanDesc");
    }

    //Jumlah Penumpang
    public Integer getJumlahPenumpang(){
        return getInt("JumlahPenumpang");
    }

    //Tanggal dan Waktu
    public Date getPostDate(){
        return getDate("Date");
    }

    //Status

}
