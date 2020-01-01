package id.or.gri.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
