package id.or.gri.service.repository;

import id.or.gri.domain.Tag;
import id.or.gri.service.repository.custom.TagRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TagRepo extends ReactiveMongoRepository<Tag, String>, TagRepoCustom {
}
