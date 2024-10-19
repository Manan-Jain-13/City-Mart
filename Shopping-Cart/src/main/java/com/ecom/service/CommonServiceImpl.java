package com.ecom.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

@Service
public class CommonServiceImpl implements CommonService{

    @Override
    public void removeSessionMsg() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute("successMsg");
        session.removeAttribute("errorMsg");
    }
}
