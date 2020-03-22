package com.cmsnesia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto implements Serializable {

  private String id;

  private String name;

  private String description;

  private String startedAt;

  private String endedAt;

  private Set<String> types;

  private Set<MediaDto> medias;
}
