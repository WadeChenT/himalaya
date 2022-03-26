package com.otc.himalaya.controller;

import com.otc.himalaya.entity.Trade;
import com.otc.himalaya.entity.User;
import com.otc.himalaya.repo.TradeRepository;
import com.otc.himalaya.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class RootController {

    private final TradeRepository userRepository;

    @GetMapping("/user")
    public List<Trade> getAllPerson() {
        return userRepository.findAll();
    }

}
