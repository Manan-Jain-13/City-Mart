package com.ecom.service.impl;

import com.ecom.entity.Product;
import com.ecom.repository.ProdcutRepository;
import com.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProdcutRepository prodcutRepository;

    @Override
    public Product saveProduct(Product product) {
      return prodcutRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
       return prodcutRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product = prodcutRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(product)){
            prodcutRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        Product product = prodcutRepository.findById(id).orElse(null);
        return product;
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {
        Product dbProduct=getProductById(product.getId());

        String imageName= image.isEmpty() ? dbProduct.getImageName() : image.getOriginalFilename();
        dbProduct.setImageName(imageName);

        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setStock(product.getStock());

        dbProduct.setDiscount(product.getDiscount());
        // Logic for finding amount to be deducted when discount will be given in percentage
        Double discount=product.getPrice()*(product.getDiscount()/100.0);
        Double discountPrice=product.getPrice()-discount;
        dbProduct.setDiscountPrice(discountPrice);

        Product updateProduct = prodcutRepository.save(dbProduct);
        if(!ObjectUtils.isEmpty(updateProduct)){
            if (!image.isEmpty()){

                try{
                File savedFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                Files.copy(image.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

                }
                catch (IOException e) {
                    System.err.println("Error saving file: " + e.getMessage());
                    e.printStackTrace();
                    // Handle the error appropriately
                }
            }
        }
        return updateProduct;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products =null;
        if(ObjectUtils.isEmpty(category)){
           products= prodcutRepository.findByIsActiveTrue();
        }
        else{
           products= prodcutRepository.findByCategory(category);
        }
        return products;
        }
}
