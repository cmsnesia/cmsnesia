package com.cmsnesia.model.response;

import com.cmsnesia.model.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> implements Serializable {

    private List<T> content;
    private PageRequest page;
    private Long total;

}
