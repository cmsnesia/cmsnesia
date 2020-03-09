package com.cmsnesia.service.repository.custom;

import com.cmsnesia.domain.Profile;
import com.cmsnesia.domain.model.Media;
import com.cmsnesia.domain.model.enums.MediaType;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.IdRequest;
import com.cmsnesia.service.util.Sessions;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProfileRepoCustomImpl implements ProfileRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Boolean> exists(AuthDto session) {
    Query query = new Query();
    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));
    return reactiveMongoTemplate.exists(query, Profile.class);
  }

  public Mono<Profile> find(AuthDto session, IdRequest idRequest) {
    Query query = new Query();
    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));
    if (idRequest != null && StringUtils.hasText(idRequest.getId())) {
      query.addCriteria(Criteria.where("id").is(idRequest.getId()));
    }
    return reactiveMongoTemplate
        .findOne(query, Profile.class)
        .defaultIfEmpty(
            Profile.builder()
                .title("Profile")
                .description("<p></p>")
                .medias(
                    Arrays.asList(
                            Media.builder()
                                .name("Profile.jpg")
                                .type(MediaType.THUMBNAIL)
                                .url(
                                    "https://raw.githubusercontent.com/cmsnesia/assets/master/about-img.jpg")
                                .build())
                        .stream()
                        .collect(Collectors.toSet()))
                .build());
  }
}
