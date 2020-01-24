package id.or.gri.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "tag")
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class Tag extends Audit {

    @Id
    private String id;

    @NotBlank(message = "Tag name must be not blank")
    @Size(max = 50)
    @Indexed(unique = true)
    private String name;

}