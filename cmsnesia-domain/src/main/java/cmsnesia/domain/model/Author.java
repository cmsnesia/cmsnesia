package cmsnesia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {

    @NotBlank(message = "Author name must be not blank")
    @Size(max = 100, message = "Length of author name must be less then or equal to 100")
    private String name;

    @NotNull
    private Date modifiedAt;

}
