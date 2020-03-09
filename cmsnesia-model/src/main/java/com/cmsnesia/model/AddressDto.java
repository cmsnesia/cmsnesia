package com.cmsnesia.model;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto implements Serializable {

  private String country;
  private String city;
  private String street;
  private Set<EmailDto> emails;
  private Set<PhoneDto> phones;
}
