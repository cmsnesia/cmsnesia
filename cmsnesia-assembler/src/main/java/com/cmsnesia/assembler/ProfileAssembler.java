package com.cmsnesia.assembler;

import com.cmsnesia.domain.Profile;
import com.cmsnesia.model.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProfileAssembler extends Assembler<Profile, ProfileDto> {

    private final AuthorAssembler authorAssembler;
    private final MediaAssembler mediaAssembler;

    @Nonnull
    @Override
    public Profile fromDto(@Nonnull ProfileDto dto) {
        return Profile.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .medias(mediaAssembler.fromDto(dto.getMedias()))
                .build();
    }

    @Nonnull
    @Override
    public List<Profile> fromDto(@Nonnull Collection<ProfileDto> dtos) {
        return dtos.stream().map(this::fromDto).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public ProfileDto fromEntity(@Nonnull Profile entity) {
        return ProfileDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .medias(mediaAssembler.fromEntity(entity.getMedias()))
                .build();
    }

    @Nonnull
    @Override
    public List<ProfileDto> fromEntity(@Nonnull Collection<Profile> entities) {
        return entities.stream().map(this::fromEntity).collect(Collectors.toList());
    }
}
