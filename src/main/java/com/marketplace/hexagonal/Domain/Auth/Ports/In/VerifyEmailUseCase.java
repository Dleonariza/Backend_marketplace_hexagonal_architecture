package com.marketplace.hexagonal.Domain.Auth.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;

public interface VerifyEmailUseCase {
    UserModel verifyEmail(String email, String code);
}
