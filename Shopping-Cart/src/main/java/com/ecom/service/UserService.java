package com.ecom.service;

import com.ecom.entity.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    public UserDtls saveUser(UserDtls user);

    public UserDtls getUserByEmail(String email);

    public List<UserDtls> getAllUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);

    public void increaseFailedAttempt(UserDtls user);

    public void userAccountLock(UserDtls user);

    public Boolean unlockAccountTimeExpired(UserDtls user);

    public void resetAttempt(int userId);

    void updateUserResetToken(String email, String resetToken);

    public UserDtls getUserByToken(String token);

    public UserDtls updateUser(UserDtls user);

    public UserDtls updateUserProfile(UserDtls user, MultipartFile img);

}
