package com.ecom.service.impl;

import com.ecom.entity.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDtls saveUser(UserDtls user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        UserDtls saveUser = userRepository.save(user);
        return saveUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);

    }

    @Override
    public List<UserDtls> getAllUsers(String role) {
        return userRepository.findByRole(role);

    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        Optional<UserDtls> findByUser = userRepository.findById(id);
        if (findByUser.isPresent()){
            UserDtls userDtls = findByUser.get();
            userDtls.setIsEnable(status);
            userRepository.save(userDtls);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(UserDtls user) {
        int attempt=user.getFailedAttempt()+1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(UserDtls user) {
         user.setAccountNonLocked(false);
         user.setLockTime(new Date());
         userRepository.save(user);
    }

    @Override
    public Boolean unlockAccountTimeExpired(UserDtls user) {
       long lockTime=user.getLockTime().getTime();
       long unlockTime=lockTime+ AppConstant.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();

        if (unlockTime<currentTime){
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls findByEmail = userRepository.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        userRepository.save(findByEmail);
    }

    @Override
    public UserDtls getUserByToken(String token) {
        return userRepository.findByResetToken(token);

    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        return userRepository.save(user);

    }
}
