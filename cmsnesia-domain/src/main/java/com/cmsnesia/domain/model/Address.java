package com.cmsnesia.domain.model;

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
public class Address implements Serializable {

  private String country;
  private String city;
  private String street;
  private Set<Email> emails;
  private Set<Phone> phones;
}
