package com.cmsnesia.model.response;

import com.cmsnesia.model.request.PageRequest;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> implements Serializable {

  private List<T> content;
  private PageRequest page;
  private Long total;
}
