package id.or.gri.domain.model;

import id.or.gri.domain.model.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Media implements Serializable {

    private String name;
    private String url;
    private MediaType type;
}
