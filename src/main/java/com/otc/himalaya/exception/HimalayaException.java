package com.otc.himalaya.exception;

import com.otc.himalaya.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class HimalayaException extends RuntimeException {

    private static final long serialVersionUID = 500515802716689108L;
    private LocalDateTime errTime;
    private String errMsg;
    private String errCode;

    private HimalayaException() {
        super();
        this.errTime = DateUtils.getNowLocal();
    }

    public static HimalayaException occur() {
        return new HimalayaException();
    }

    public static HimalayaException occur(String errCode, String errMsg) {
        return occur().setErrCode(errCode)
                      .setErrMsg(errMsg);
    }

    public static HimalayaException occur(HiOtcErrorEnum error) {
        return occur(error.getErrCode(), error.getErrMsg());
    }

    @Getter
    public enum HiOtcErrorEnum {
        COMMON_ERROR("9900", "Server Error."),

        //login
        WRONG_EMAIL_OR_PASSWORD("8787", "Wrong email address or password"),

        UNAUTHORIZED("99999", "unauthorized."),
        TOKEN_EXPIRED_OR_NOT_VALID_YET("99998", "token expired or not valid yet."),
        TOKEN_ERROR("99997", "token error."),
        TOKEN_ERROR_TYPE("99996", "token error."),
        USER_NOT_FOUND("99990", "user not found."),
        USER_HAD_BEEN_REGISTRY("99990", "this account has been registered.");

        private String errMsg;
        private String errCode;

        HiOtcErrorEnum(String errCode, String errMsg) {
            this.errCode = errCode;
            this.errMsg = errMsg;
        }
    }

}
