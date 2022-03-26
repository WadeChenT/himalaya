package com.otc.himalaya.service.vo;

import com.otc.himalaya.entity.User;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserVo {
    private User user;
    // private List<Roles> rolesList;
    // private List<Permission> permissionList;
}
