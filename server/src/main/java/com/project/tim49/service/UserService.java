package com.project.tim49.service;

import com.project.tim49.model.User;
import com.project.tim49.model.UserRequest;

import java.util.List;

public interface UserService {
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAll ();
    User save(UserRequest userRequest);
}