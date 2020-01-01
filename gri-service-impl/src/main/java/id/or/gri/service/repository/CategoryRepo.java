package id.or.gri.service.repository;

import id.or.gri.domain.Category;
import id.or.gri.service.repository.custom.CategoryRepoCustom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepo extends ReactiveMongoRepository<Category, String>, CategoryRepoCustom {
}
