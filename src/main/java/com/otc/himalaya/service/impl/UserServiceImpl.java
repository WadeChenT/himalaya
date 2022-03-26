package com.otc.himalaya.service.impl;

import com.otc.himalaya.entity.User;
import com.otc.himalaya.exception.HimalayaException;
import com.otc.himalaya.repo.UserRepository;
import com.otc.himalaya.service.UserService;
import com.otc.himalaya.service.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository usersRepository;
    private final JavaMailSender mailSender;


    @Override
    public User createNewUsers(User newUser) throws MessagingException, IOException {
        newUser = saveOrUpdate(newUser);

//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
//                                                          MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//                                                          StandardCharsets.UTF_8.name());
//        message.setTo(newUser.getEmail());
//        message.setSubject("Account Registration");
//        String content = Files.readString(ResourceUtils.getFile("classpath:templates/new_account_registration_trans.html").toPath());
//        content = StringUtils.replace(content,
//                                      "{{$name}}",
//                                      newUser.getName());
//        switch (CompanyService.CompanyRoleType.of(company.getCompanyRoleId())) {
//            case Lab:
//                String labUrl = StringUtils.replace(ConfigConstant.labRegistryUrl + "{token}",
//                                                    "{token}",
//                                                    newUser.getEmailVerifiedToken());
//                content = StringUtils.replace(content,
//                                              "{{$verifiedUrl}}",
//                                              labUrl);
//                break;
//            case ProductProvider:
//                String ppUrl = StringUtils.replace(ConfigConstant.ppRegistryUrl + "{token}",
//                                                   "{token}",
//                                                   newUser.getEmailVerifiedToken());
//                content = StringUtils.replace(content,
//                                              "{{$verifiedUrl}}",
//                                              ppUrl);
//                break;
//
//            case Admin:
//            default:
//                throw HimalayaException.occur(HimalayaException.HiOtcErrorEnum.COMMON_ERROR);
//        }
//        message.setText(content, true);
//        mailSender.send(mimeMessage);

        return newUser;
    }

    @Override
    public void deleteByEntity(User deleteUser) throws MessagingException, IOException {
        usersRepository.delete(deleteUser);
    }

    @Override
    public User saveOrUpdate(User entity) {
        return usersRepository.save(entity);
    }

    @Override
    public User findByEmail(String email) {
        return usersRepository.findByEmail(email)
                              .orElseThrow(() -> HimalayaException.occur(HimalayaException.HiOtcErrorEnum.USER_NOT_FOUND));
    }

    @Override
    public User findById(String id) {
        return usersRepository.findById(id)
                              .orElseThrow(() -> HimalayaException.occur(HimalayaException.HiOtcErrorEnum.USER_NOT_FOUND));
    }

    @Override
    public User findByEmailVerifiedToken(String token) {
        return usersRepository.findByTmpToken(token)
                              .orElseThrow(() -> HimalayaException.occur(HimalayaException.HiOtcErrorEnum.USER_HAD_BEEN_REGISTRY));
    }

    @Override
    public UserVo buildUserVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setUser(user);
        return userVo;
    }
}
