package com.cmsnesia.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "categoryGroup")
@EqualsAndHashCode(callSuper = true)
public class CategoryGroup extends Audit {

    private String id;
    private String name;
    private Set<String> categoryIds;

}
