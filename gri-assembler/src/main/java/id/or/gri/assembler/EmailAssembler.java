package id.or.gri.assembler;

import id.or.gri.domain.model.Email;
import id.or.gri.model.EmailDto;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EmailAssembler implements Assembler<Email, EmailDto> {

    @Nonnull
    @Override
    public Email fromDto(@Nonnull EmailDto dto) {
        return Email.builder()
                .address(dto.getAddress())
                .types(dto.getTypes())
                .status(dto.getStatus())
                .build();
    }

    @Nonnull
    @Override
    public Set<Email> fromDto(@Nonnull Collection<EmailDto> list) {
        return list.stream().map(this::fromDto).collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public EmailDto fromEntity(@Nonnull Email entity) {
        return EmailDto.builder()
                .address(entity.getAddress())
                .types(entity.getTypes())
                .status(entity.getStatus())
                .build();
    }

    @Nonnull
    @Override
    public Set<EmailDto> fromEntity(@Nonnull Collection<Email> entity) {
        return entity.stream().map(this::fromEntity).collect(Collectors.toSet());
    }
}