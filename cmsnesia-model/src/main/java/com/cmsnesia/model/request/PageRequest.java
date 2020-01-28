package com.cmsnesia.model.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest implements Serializable {

  private int page;
  private int size;

  public String asQueryParam() {
    return "page=" + page + "&size=" + size;
  }
}
