package com.cmsnesia.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto extends BaseDto {

  private String id;
  private String title;
  private String description;
  private Set<AddressDto> addresses;
  private Set<MediaDto> medias;
}
