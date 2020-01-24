package id.or.gri.domain;

import id.or.gri.domain.model.Media;
import id.or.gri.domain.model.Tag;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "post_draft")
@EqualsAndHashCode(callSuper = true)
public class PostDraft extends Audit {

    @Id
    private String id;

    @NotBlank(message = "Post title must be not blank")
    @Size(max = 100, message = "Maximum size of post title is 100")
    private String title;

    @NotBlank(message = "Post content must be not blank")
    private String content;

    @NotNull
    private Set<Media> medias;

    @NotEmpty
    private Set<Tag> tags;

    @NotEmpty
    private Set<Category> categories;

}