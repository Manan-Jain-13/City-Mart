package com.ecom.service.impl;

import com.ecom.entity.Cart;
import com.ecom.entity.Product;
import com.ecom.entity.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProdcutRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProdcutRepository prodcutRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = prodcutRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart=null;

        if (ObjectUtils.isEmpty(cartStatus)){
           cart=new Cart();
           cart.setProduct(product);
           cart.setUser(userDtls);
           cart.setQuantity(1);
           cart.setTotalPrice(1*product.getDiscountPrice());
        }
        else{
            cart=cartStatus;
            cart.setQuantity(cart.getQuantity()+1);
            cart.setTotalPrice(cart.getQuantity()*cart.getProduct().getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);

        return saveCart;
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        Double totalOrderPrice=0.0;
        List<Cart> updateCarts=new ArrayList<>();
        for(Cart c:carts){
           Double totalPrice=(c.getProduct().getDiscountPrice()*c.getQuantity());
            c.setTotalPrice(totalPrice);
            totalOrderPrice+=totalPrice;
            c.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(c);
        }

        //carts.get(0).setTotalPrice(totalPrice);
        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);
        return countByUserId;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        int updateQuantity;
        Cart cart = cartRepository.findById(cid).get();
        if(sy.equalsIgnoreCase("dec")){
            updateQuantity=cart.getQuantity()-1;

            if (updateQuantity<=0){
                cartRepository.delete(cart);
            }
            else {
                cart.setQuantity(updateQuantity);
                Cart update = cartRepository.save(cart);

            }
        }
        else{
            updateQuantity=cart.getQuantity()+1;
            cart.setQuantity(updateQuantity);
            Cart update = cartRepository.save(cart);

        }
    }
}
