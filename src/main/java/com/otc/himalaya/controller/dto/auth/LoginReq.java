package com.otc.himalaya.controller.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otc.himalaya.utils.ValidationPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Schema(description = "login object")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginReq {

    @Schema(description = "login email", required = true)
    @Email
    @NotBlank(message = "email " + ValidationPattern.FIELD_NOT_BLANK)
    private String email;

    @Schema(description = "login password", required = true)
    @NotBlank(message = "password " + ValidationPattern.FIELD_NOT_BLANK)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
