package com.cmsnesia.model.api;

import java.io.Serializable;

public class Result<T extends Serializable> implements Serializable {

  private T data;
  private StatusCode statusCode;

  public Result() {
    this.data = null;
    this.statusCode = StatusCode.GENERAL_EXCEPTION;
  }

  public Result(T data) {
    this.data = data;
    this.statusCode = StatusCode.GENERAL_EXCEPTION;
  }

  public Result(T data, StatusCode statusCode) {
    this.data = data;
    this.statusCode = statusCode;
  }

  public static <T extends Serializable> Result<T> build(T data, StatusCode statusCode) {
    return new Result<>(data, statusCode);
  }

  public static <T extends Serializable> Result<T> build(StatusCode statusCode) {
    Result<T> result = new Result<>();
    result.setStatusCode(statusCode);
    return result;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Result{");
    sb.append("data=").append(data);
    sb.append(", statusCode=").append(statusCode);
    sb.append('}');
    return sb.toString();
  }
}
