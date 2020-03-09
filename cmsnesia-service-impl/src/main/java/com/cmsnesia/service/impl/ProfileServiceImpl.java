package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.ProfileAssembler;
import com.cmsnesia.domain.Profile;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.ProfileDto;
import com.cmsnesia.model.api.Result;
import com.cmsnesia.model.api.StatusCode;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.ProfileService;
import com.cmsnesia.service.repository.ProfileRepo;
import com.cmsnesia.service.util.Sessions;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

  private final ProfileAssembler profileAssembler;
  private final ProfileRepo profileRepo;

  @Override
  public Mono<Result<ProfileDto>> add(AuthDto session, ProfileDto dto) {
    return profileRepo
        .exists(session)
        .flatMap(
            exists -> {
              if (exists) {
                return Mono.just(Result.build(StatusCode.DUPLICATE_DATA_EXCEPTION));
              }
              Profile profile = profileAssembler.fromDto(dto);
              profile.setId(UUID.randomUUID().toString());
              profile.setCreatedBy(session.getId());
              profile.setCreatedAt(new Date());
              profile.setApplications(Sessions.applications(session));
              return profileRepo
                  .save(profile)
                  .map(
                      saved ->
                          Result.build(
                              profileAssembler.fromEntity(saved), StatusCode.SAVE_SUCCESS));
            });
  }

  @Override
  public Mono<Result<ProfileDto>> edit(AuthDto session, ProfileDto dto) {
    return profileRepo
        .exists(session)
        .flatMap(
            exists -> {
              if (!exists) {
                return Mono.just(Result.build(StatusCode.DATA_FOUND));
              }
              return profileRepo
                  .findById(dto.getId())
                  .flatMap(
                      profile -> {
                        Profile save = profileAssembler.fromDto(dto);
                        save.audit(profile);
                        save.setModifiedBy(session.getId());
                        save.setModifiedAt(new Date());
                        return profileRepo
                            .save(save)
                            .map(
                                saved ->
                                    Result.build(
                                        profileAssembler.fromEntity(saved),
                                        StatusCode.SAVE_SUCCESS));
                      });
            });
  }

  @Override
  public Mono<Result<ProfileDto>> delete(AuthDto session, ProfileDto dto) {
    throw new UnsupportedOperationException("Couldn't delete profile! Operation not supported.");
  }

  @Override
  public Mono<Page<ProfileDto>> find(AuthDto session, ProfileDto dto, Pageable pageable) {
    throw new UnsupportedOperationException("Couldn't find profile! Operation not supported.");
  }

  @Override
  public Mono<Result<ProfileDto>> find(AuthDto session, IdRequest idRequest) {
    return profileRepo
        .find(session, idRequest)
        .map(profile -> Result.build(profileAssembler.fromEntity(profile), StatusCode.DATA_FOUND));
  }
}
