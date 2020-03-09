package com.cmsnesia.assembler;

import com.cmsnesia.domain.model.Address;
import com.cmsnesia.model.AddressDto;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AddressAssembler extends Assembler<Address, AddressDto> {

  private final EmailAssembler emailAssembler;
  private final PhoneAssembler phoneAssembler;

  @Nonnull
  @Override
  public Address fromDto(@Nonnull AddressDto dto) {
    return Address.builder()
        .country(dto.getCountry())
        .city(dto.getCity())
        .street(dto.getStreet())
        .emails(emailAssembler.fromDto(dto.getEmails()))
        .phones(phoneAssembler.fromDto(dto.getPhones()))
        .build();
  }

  @Nonnull
  @Override
  public Set<Address> fromDto(@Nonnull Collection<AddressDto> dtos) {
    return dtos == null
        ? new HashSet<>()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public AddressDto fromEntity(@Nonnull Address entity) {
    return AddressDto.builder()
        .country(entity.getCountry())
        .city(entity.getCity())
        .street(entity.getStreet())
        .emails(emailAssembler.fromEntity(entity.getEmails()))
        .phones(phoneAssembler.fromEntity(entity.getPhones()))
        .build();
  }

  @Nonnull
  @Override
  public Set<AddressDto> fromEntity(@Nonnull Collection<Address> entities) {
    return entities == null
        ? new HashSet<>()
        : entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
