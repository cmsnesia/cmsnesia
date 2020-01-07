package id.or.gri.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token implements Serializable {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    
}
