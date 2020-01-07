package id.or.gri.service.repository;

import id.or.gri.domain.PostDraft;
import id.or.gri.service.repository.custom.PostDraftRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PostDraftRepo extends ReactiveMongoRepository<PostDraft, String>, PostDraftRepoCustom {
}
