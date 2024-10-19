package com.ecom.controller;

import com.ecom.entity.Category;
import com.ecom.entity.Product;
import com.ecom.entity.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p,Model model){
        if (p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user",userDtls);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categories",allActiveCategory);

    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/signin")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category){
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);
        model.addAttribute("categories",categories);
        model.addAttribute("products",products);
        model.addAttribute("paramValue",category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id,Model model){
        Product productById = productService.getProductById(id);
        model.addAttribute("product",productById);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file,
                           HttpSession session) throws IOException {
        String imgName=file.isEmpty() ? "default.jpg": file.getOriginalFilename();
        user.setProfileImage(imgName);
        UserDtls saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)){
            if(!file.isEmpty()){
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());
                /*  System.out.println(path);*/
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
             }
            session.setAttribute("successMsg","Welcome OnBoard :)");
        }
            else {
                session.setAttribute("errorMsg","Something Wrong :(");
            }
     return "redirect:/register";
    }

    // Forgot Password Controller
    @GetMapping("/forgot-password")
    public String showForgotPassword(){
         return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDtls userByEmail = userService.getUserByEmail(email);
        if (ObjectUtils.isEmpty(userByEmail)){
            session.setAttribute("errorMsg","Invalid Mail Id");
        }
        else {

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email,resetToken);

            // Generating URL to reset the password
            String url=CommonUtil.generateURL(request)+"/reset-password?token="+resetToken;

            Boolean sendMail= commonUtil.sendMail(url,email);
            if (sendMail){
            session.setAttribute("successMsg","Password Reset Link sent on email");
            }
            else {
                session.setAttribute("errorMsg","Something wrong on server");
            }
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token,HttpSession session, Model model){
        UserDtls userByToken = userService.getUserByToken(token);

        if (ObjectUtils.isEmpty(userByToken)){
            model.addAttribute("msg","Your link is Invalid or expired");
            return "message";
        }
        model.addAttribute("token",token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,@RequestParam String password, HttpSession session,Model model){
        UserDtls userByToken = userService.getUserByToken(token);
        if (ObjectUtils.isEmpty(userByToken)){
            model.addAttribute("msg","Your link is Invalid or expired");
            return "message";
        }
        else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            session.setAttribute("successMsg","Password Changed Successfully");
            model.addAttribute("msg","Password Changed Successfully");
            return "login";
        }

    }

}
