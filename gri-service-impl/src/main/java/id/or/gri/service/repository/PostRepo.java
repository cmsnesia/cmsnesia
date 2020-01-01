package id.or.gri.service.repository;

import id.or.gri.domain.Post;
import id.or.gri.service.repository.custom.PostRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PostRepo extends ReactiveMongoRepository<Post, String>, PostRepoCustom {
}
