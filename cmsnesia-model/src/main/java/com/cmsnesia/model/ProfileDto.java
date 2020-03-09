package com.cmsnesia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
