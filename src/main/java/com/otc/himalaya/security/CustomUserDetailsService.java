package com.otc.himalaya.security;


import com.otc.himalaya.entity.User;
import com.otc.himalaya.exception.HimalayaException;
import com.otc.himalaya.service.UserService;
import com.otc.himalaya.service.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws HimalayaException, UsernameNotFoundException {

        User user = userService.findByEmail(email);
        UserVo userRolesView = userService.buildUserVo(user);

        return UserPrincipal.create(userRolesView);
    }

    public UserDetails loadUserById(String id) {

        User user = userService.findById(id);
        UserVo userRolesView = userService.buildUserVo(user);

        return UserPrincipal.create(userRolesView);
    }
}