package com.gexu.keycloak.bizkeycloakmodel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Calendar;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema
public class User {

  private String id;

  private String username;

  @NotBlank
  private String name;

  private String picture;

  private String phoneNumber;

  private Group group;

  private Set<Role> role;

  private Boolean enabled;

  private String commonIp;

  private String lastLoginIp;

  private Long lastLoginTime;

  private Long createdAt;

  @Schema(description = "申请重置密码时间")
  private Calendar resetAt;
}
