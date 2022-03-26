package com.otc.himalaya.service;

import com.otc.himalaya.entity.User;
import com.otc.himalaya.service.vo.UserVo;

import javax.mail.MessagingException;
import java.io.IOException;

public interface UserService {
    User createNewUsers(User newUser) throws MessagingException, IOException;

    void deleteByEntity(User deleteUser) throws MessagingException, IOException;

    User saveOrUpdate(User entity);

    User findByEmail(String email);

    User findById(String id);

    User findByEmailVerifiedToken(String token);

    UserVo buildUserVo(User user);
}
