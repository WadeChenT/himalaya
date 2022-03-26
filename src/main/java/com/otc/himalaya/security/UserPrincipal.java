package com.otc.himalaya.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otc.himalaya.entity.User;
import com.otc.himalaya.service.vo.UserVo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserPrincipal implements UserDetails {

    @JsonIgnore
    private static final long serialVersionUID = 1016768551875939792L;

    private User user;

    @EqualsAndHashCode.Include
    private String id;

    @EqualsAndHashCode.Include
    private String email;

    @EqualsAndHashCode.Include
    private String username; //from UserDetails

    @JsonIgnore
    private String password; //from UserDetails

    private Collection<? extends GrantedAuthority> authorities; //from UserDetails

    private UserPrincipal() {}

    public static UserPrincipal create(UserVo userVo) {
        User user = userVo.getUser();
        /*
        List<GrantedAuthority> authorities =
                adminUserRolesVo.getPermissionList()
                                .stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                                .collect(Collectors.toList());
        */

        // for dev 全開的權限
//         boolean isDeveloper = userRolesView.getRolesList()
//                                            .stream()
//                                            .anyMatch(role -> StringUtils.equals(role.getName(), "SYSTEM_DEVELOPER"));
//         if (isDeveloper) authorities.add(new SimpleGrantedAuthority("SYSTEM_DEVELOPING"));
        // for dev 全開的權限

        return new UserPrincipal().setId(user.getId())
                                  .setUsername(user.getName())
                                  .setPassword(user.getPassword())
                                  .setEmail(user.getEmail())
                                  .setUser(user)
                                  // .setAuthorities(authorities)
                ;
    }


    public String getName() {
        return this.user.getName();
    }

    public String getEmail() {
        return this.user.getEmail();
    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return this.user.getActivate().equals(1);
//    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

//    @Override
//    public boolean isEnabled() {
//        return this.user.getActivate().equals(1);
//    }

}
