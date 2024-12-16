package com.ecom.controller;

import com.ecom.entity.*;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import jakarta.persistence.GeneratedValue;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p, Model model){
        if (p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user",userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            model.addAttribute("countCart",countCart);

        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categories",allActiveCategory);

    }

    @GetMapping("/")
    public String home(){
      return "user/home";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session){
        Cart saveCart = cartService.saveCart(pid, uid);
        if (ObjectUtils.isEmpty(saveCart)){
            session.setAttribute("errorMsg","Product add to cart failed");
        }
        else {
            session.setAttribute("successMsg","Product successfully added");
        }
        return "redirect:/product/"+pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p,Model model){

        UserDtls user=getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts",carts);
        if(carts.size()>0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
       return userDtls;
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy,@RequestParam Integer cid){
        cartService.updateQuantity(sy,cid);
        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String orderPage(Principal p,Model model){
        UserDtls user=getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts",carts);
        if(carts.size()>0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice()+250+100;
            model.addAttribute("orderPrice", orderPrice);
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }

        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request,Principal p) throws Exception{
       // System.out.println(request);
        UserDtls user = getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(),request);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess(){
        return "/user/success";
    }

    @GetMapping("/user-orders")
    public String myOrders(Model model,Principal p){
        UserDtls loggedInUser = getLoggedInUserDetails(p);
        List<ProductOrder> orders = orderService.getOrderByUser(loggedInUser.getId());
        model.addAttribute("orders",orders);
        return "/user/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st,HttpSession session){
        OrderStatus[] values = OrderStatus.values();
        String status=null;

        for(OrderStatus orderStatus:values){
            if(orderStatus.getId().equals(st)){
                status=orderStatus.getName();
            }
        }
        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
        try {
            commonUtil.sendMailForProductOrder(updateOrder,status);
        } catch (Exception e) {
           e.printStackTrace();
        }
        if(!ObjectUtils.isEmpty(updateOrder)){
            session.setAttribute("successMsg","Updated Successfully");
        }
        else{
            session.setAttribute("errorMsg","Something went wrong :(");
        }
        return "redirect:/user/user-orders";
    }

    @GetMapping("/profile")
    public String profile(){
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session){
        UserDtls updateUserProfile = userService.updateUserProfile(user, img);
        if(ObjectUtils.isEmpty(updateUserProfile)){
            session.setAttribute("errorMsg","No changes made");
        }
        else {
            session.setAttribute("successMsg","Changes Done Successfully");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,@RequestParam String currentPassword,Principal p,
                                 HttpSession session){
        UserDtls loggedInUserDetails = getLoggedInUserDetails(p);
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
        if(matches){
            String encodePassword = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encodePassword);
            UserDtls updatedUser = userService.updateUser(loggedInUserDetails);
            if(ObjectUtils.isEmpty(updatedUser)){
               session.setAttribute("errorMsg","Something Wrong On Server");
            }
            else {
               session.setAttribute("successMsg","Updated Successfully");
            }
        }
        else{
            session.setAttribute("errorMsg","Password doesn't match");
        }
        return "redirect:/user/profile";
    }

}
