package com.cmsnesia.domain;

import com.cmsnesia.domain.model.Application;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
public abstract class Audit implements Serializable {

  @NotNull @CreatedDate private Date createdAt;

  @LastModifiedDate private Date modifiedAt;

  private Date deletedAt;

  @NotNull @CreatedBy private String createdBy;

  @LastModifiedBy private String modifiedBy;

  private String deletedBy;

  private Set<String> status;

  private Set<Application> applications;

  public void audit(Audit audit) {
    if (Objects.nonNull(audit.getCreatedAt())) {
      this.setCreatedAt(audit.getCreatedAt());
    }
    if (Objects.nonNull(audit.getModifiedAt())) {
      this.setModifiedAt(audit.getModifiedAt());
    }
    if (Objects.nonNull(audit.getDeletedAt())) {
      this.setDeletedAt(audit.getDeletedAt());
    }
    if (!StringUtils.isEmpty(audit.getCreatedBy())) {
      this.setCreatedBy(audit.getCreatedBy());
    }
    if (!StringUtils.isEmpty(audit.getModifiedBy())) {
      this.setModifiedBy(audit.getModifiedBy());
    }
    if (!StringUtils.isEmpty(audit.getDeletedBy())) {
      this.setDeletedBy(audit.getDeletedBy());
    }
    if (!StringUtils.isEmpty(audit.getStatus())) {
      this.setStatus(audit.getStatus());
    }
    if (!CollectionUtils.isEmpty(audit.getApplications())) {
      this.setApplications(audit.getApplications());
    }
  }
}
