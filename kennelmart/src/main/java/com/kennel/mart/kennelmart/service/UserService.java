package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.UpdateProfileRequest;
import com.kennel.mart.kennelmart.dto.ChangePasswordRequest;
import com.kennel.mart.kennelmart.entity.User;

public interface UserService {
    User updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    User updateProfileImage(String email, String imageUrl);
}