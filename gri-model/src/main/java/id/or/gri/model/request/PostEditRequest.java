package id.or.gri.model.request;

import id.or.gri.model.MediaDto;
import id.or.gri.model.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEditRequest {

    private String id;
    private String title;
    private String content;

    private Set<MediaDto> medias;
    private Set<TagDto> tags;
    private Set<IdRequest> categories;

}