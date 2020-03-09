package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Address;
import com.cmsnesia.domain.model.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "profile")
@EqualsAndHashCode(callSuper = true)
public class Profile extends Audit {

    @Id
    private String id;

    @NotBlank(message = "Profile title must be not blank")
    private String title;

    @NotBlank(message = "Profile description must be not blank")
    private String description;

    @NotNull
    private Set<Address> addresses;

    @NotNull
    private Set<Media> medias;
}