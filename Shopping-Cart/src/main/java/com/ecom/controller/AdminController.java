package com.ecom.controller;

import com.ecom.entity.Category;
import com.ecom.entity.Product;
import com.ecom.entity.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;


    private final ProductService productService;

    @Autowired
    public AdminController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @Autowired
    private UserService userService;

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
    public String index(){
        return "admin/index";
    }

    @GetMapping("/addProduct")
    public String addProduct(Model model){
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories",categories);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model m){
        m.addAttribute("categories",categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        String imageName = (file != null && !file.isEmpty()) ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existCategory = categoryService.existCategory(category.getName());
        if(existCategory){
            session.setAttribute("errorMsg", "Category Name Already Exists");
        } else {
            Category savedCategory = categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(savedCategory)){
                session.setAttribute("errorMsg", "Not Saved! Internal Server Error");
            } else {

                File savedFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("successMsg", "Saved Successfully!!");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id,HttpSession session){
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if (deleteCategory){
              session.setAttribute("successMsg","category delete success");
        }
        else{
            session.setAttribute("errorMsg","something wrong on server");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model){
        model.addAttribute("category",categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,
                                 HttpSession session) throws IOException {
        Category oldcategory=categoryService.getCategoryById(category.getId());
        String imageName=file.isEmpty() ? oldcategory.getImageName():file.getOriginalFilename();

        if(!ObjectUtils.isEmpty(category)){
            oldcategory.setName(category.getName());
            oldcategory.setIsActive(category.getIsActive());
            oldcategory.setImageName(imageName);
        }

        Category updateCategory = categoryService.saveCategory(oldcategory);
        if(!ObjectUtils.isEmpty(updateCategory)){

            if (!file.isEmpty()){
                File savedFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

            }

           session.setAttribute("successMsg","Category Updated Successfully!!");
        }
        else{
            session.setAttribute("errorMsg","Something Wrong :(");
        }

        return "redirect:/admin/loadEditCategory/"+category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image,
                               HttpSession session) throws IOException {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImageName(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product saveProduct = productService.saveProduct(product);

        if(!ObjectUtils.isEmpty(saveProduct)){
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                    + image.getOriginalFilename());
          /*  System.out.println(path);*/
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("successMsg", "Product Saved Successfully");
        }
        else{
            session.setAttribute("errorMsg","Something Wrong :(");
        }
        return "redirect:/admin/addProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model model){
       model.addAttribute("products",productService.getAllProducts());
       return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id,HttpSession session){
        Boolean  deleteProduct = productService.deleteProduct(id);
        if (deleteProduct){
            session.setAttribute("successMsg","Product Deleted Successfully");
        }
        else {
            session.setAttribute("errorMsg","Something Wrong");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id,Model model){
         model.addAttribute("product", productService.getProductById(id));
         model.addAttribute("categories",categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,HttpSession session,
                                @RequestParam("file") MultipartFile image,Model model){

        if(product.getDiscount()<0 || product.getDiscount()>100){
            session.setAttribute("errorMsg","Invalid Discount");
        }
        else{
            Product updateProduct = productService.updateProduct(product, image);
            if(!ObjectUtils.isEmpty(updateProduct)){
                session.setAttribute("successMsg","Product Updated Successfully");
            }
            else {
                session.setAttribute("errorMsg","Something Wrong");
            }
        }
        return "redirect:/admin/editProduct/"+product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model model){
        List<UserDtls> users = userService.getAllUsers("ROLE_USER");
        model.addAttribute("users",users);
        return "/admin/users";
    }

    @GetMapping("updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,HttpSession session){
       Boolean f= userService.updateAccountStatus(id,status);
       if (f){
           session.setAttribute("successMsg","Account Status Updated");
       }
       else{
           session.setAttribute("errorMsg","Something Wrong On Server");
       }
        return "redirect:/admin/users";
    }

}
