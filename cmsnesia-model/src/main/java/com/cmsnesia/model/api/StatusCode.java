package com.cmsnesia.model.api;

import java.io.Serializable;

public enum StatusCode implements Serializable {
  DATA_FOUND(1, "Data ditemukan."),
  SYSTEM_EXCEPTION(2, "Kesalahan sistem."),
  DATABASE_EXCEPTION(3, "Kesalahan di database."),
  DUPLICATE_DATA_EXCEPTION(4, "Data sudah ada."),
  DATA_NOT_FOUND(5, "Data tidak ditemukan."),
  SAVE_SUCCESS(6, "Data berhasil disimpan."),
  SAVE_FAILED(7, "Data gagal disimpan."),
  DELETE_SUCCESS(8, "Data berhasil dihapus."),
  DELETE_FAILED(9, "Data gagal dihapus."),
  LOGIN_FAILED(10, "Login gagal! Pastikas username dan password anda benar."),
  LOGIN_SUCCESS(11, "Login berhasil."),
  GENERAL_EXCEPTION(
      12, "Mungkin terjadi masalah pada jaringan anda, periksa kembali jaringan anda."),
  INVALID_TOKEN(13, "Token anda tidak valid."),
  LOGOUT_SUCCESS(14, "Logout berhasil.");

  private final int statusCode;
  private final String message;

  StatusCode() {
    this.statusCode = -1;
    this.message = "";
  }

  StatusCode(final int statusCode, final String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("StatusCode{");
    sb.append("statusCode=").append(statusCode);
    sb.append(", message='").append(message).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
