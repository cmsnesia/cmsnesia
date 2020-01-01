package id.or.gri.assembler;

import id.or.gri.domain.model.Author;
import id.or.gri.model.AuthorDto;
import id.or.gri.model.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorAssembler implements Assembler<Author, AuthorDto> {

    @Nonnull
    @Override
    public Author fromDto(@Nonnull AuthorDto dto) {
        try {
            return Author.builder()
                    .name(dto.getName())
                    .modifiedAt(DateTimeUtils.toDate(dto.getModifiedAt()))
                    .build();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public Set<Author> fromDto(@Nonnull Collection<AuthorDto> dtos) {
        return dtos.stream().map(this::fromDto).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public AuthorDto fromEntity(@Nonnull Author entity) {
        return AuthorDto.builder()
                .name(entity.getName())
                .modifiedAt(DateTimeUtils.toString(entity.getModifiedAt()))
                .build();
    }

    @Nonnull
    @Override
    public Set<AuthorDto> fromEntity(@Nonnull Collection<Author> entities) {
        return entities.stream().map(this::fromEntity).collect(Collectors.toSet());
    }
}
