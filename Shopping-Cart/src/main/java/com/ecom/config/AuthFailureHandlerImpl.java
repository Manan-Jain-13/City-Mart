package com.ecom.config;

import com.ecom.entity.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        String email = request.getParameter("username");
        UserDtls userDtls = userRepository.findByEmail(email);

        if (userDtls.getIsEnable()){
            if(userDtls.getAccountNonLocked()){
                 if(userDtls.getFailedAttempt()<AppConstant.ATTEMPT_TIME){
                   userService.increaseFailedAttempt(userDtls);
                 }
                 else{
                     userService.userAccountLock(userDtls);
                     exception=new LockedException("You made 3 wrong attempts. Try after two minutes.");
                 }
            }
            else{
                if (userService.unlockAccountTimeExpired(userDtls)){
                    exception=new LockedException("Your Account has been unlocked. Please try log-in again");
                }
                exception=new LockedException("Your Account has been locked. Please try after some time");
            }
        }
        else {
            exception=new LockedException("Please use correct credentials");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request,response,exception);
    }
}
