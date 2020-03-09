package com.cmsnesia.model.api;

import java.io.Serializable;
import lombok.*;

@Getter
@ToString
public class StatusCode implements Serializable {

  private static String STATUS_SUCCESS = "SUCCESS";
  private static String STATUS_ERROR = "ERROR";
  private static String STATUS_WARN = "WARN";

  public static StatusCode DATA_FOUND = new StatusCode(1, STATUS_SUCCESS, "Data ditemukan.");
  public static StatusCode SYSTEM_EXCEPTION = new StatusCode(2, STATUS_ERROR, "Kesalahan sistem.");
  public static StatusCode DATABASE_EXCEPTION =
      new StatusCode(3, STATUS_ERROR, "Kesalahan di database.");
  public static StatusCode DUPLICATE_DATA_EXCEPTION =
      new StatusCode(4, STATUS_ERROR, "Data sudah ada.");
  public static StatusCode DATA_NOT_FOUND = new StatusCode(5, STATUS_WARN, "Data tidak ditemukan.");
  public static StatusCode SAVE_SUCCESS =
      new StatusCode(6, STATUS_SUCCESS, "Data berhasil disimpan.");
  public static StatusCode SAVE_FAILED = new StatusCode(7, STATUS_ERROR, "Data gagal disimpan.");
  public static StatusCode DELETE_SUCCESS =
      new StatusCode(8, STATUS_SUCCESS, "Data berhasil dihapus.");
  public static StatusCode DELETE_FAILED = new StatusCode(9, STATUS_ERROR, "Data gagal dihapus.");
  public static StatusCode LOGIN_FAILED =
      new StatusCode(10, STATUS_WARN, "Login gagal! Pastikas username dan password anda benar.");
  public static StatusCode LOGIN_SUCCESS = new StatusCode(11, STATUS_SUCCESS, "Login berhasil.");
  public static StatusCode GENERAL_EXCEPTION =
      new StatusCode(
          12,
          STATUS_ERROR,
          "Mungkin terjadi masalah pada jaringan anda, periksa kembali jaringan anda.");
  public static StatusCode INVALID_TOKEN =
      new StatusCode(13, STATUS_ERROR, "Token anda tidak valid.");
  public static StatusCode LOGOUT_SUCCESS = new StatusCode(14, STATUS_SUCCESS, "Logout berhasil.");

  private final int code;
  private final String status;
  private final String message;

  public StatusCode() {
    this.code = GENERAL_EXCEPTION.getCode();
    this.status = GENERAL_EXCEPTION.getStatus();
    this.message = GENERAL_EXCEPTION.getMessage();
  }

  public StatusCode(int code, String status, String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }
}
