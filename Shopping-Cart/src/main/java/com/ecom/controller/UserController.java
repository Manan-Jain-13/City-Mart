package com.ecom.controller;

import com.ecom.entity.Category;
import com.ecom.entity.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model model){
        if (p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user",userDtls);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categories",allActiveCategory);

    }

    @GetMapping("/")
    public String home(){
      return "user/home";
    }


}
