package com.cmsnesia.model.request;

import com.cmsnesia.model.MediaDto;
import com.cmsnesia.model.TagDto;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEditRequest {

  private String id;
  private String title;
  private String content;
  private String link;

  private Set<MediaDto> medias;
  private Set<TagDto> tags;
  private Set<IdRequest> categories;
}
