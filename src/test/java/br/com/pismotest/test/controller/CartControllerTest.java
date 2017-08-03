package br.com.pismotest.test.controller;

import br.com.pismotest.cart.Application;
import br.com.pismotest.cart.controllers.CartController;
import br.com.pismotest.cart.domain.CartProduct;
import br.com.pismotest.cart.repository.CartRepository;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.given;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Teste do controller
 * 
 * @author José San Pedro
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CartController.class)
@ContextConfiguration(classes = Application.class)
public class CartControllerTest {
    
    @Autowired
    private MockMvc mvc;
 
    @MockBean
    private CartRepository repository;
    
    @Test
    public void givenCartProducts_whenGetCartProducts_thenReturnJsonArray() throws Exception {
        // given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCartId(123l);
        cartProduct.setProductId(1l);
        repository.save(cartProduct);

        List<CartProduct> allCartProducts = new ArrayList<>();
        allCartProducts.add(cartProduct);

        given(repository.findAll()).willReturn(allCartProducts);

        // then
        mvc.perform(get("/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cartId", is(cartProduct.getCartId().intValue())))
                .andExpect(jsonPath("$[0].productId", is(cartProduct.getProductId().intValue())));
    }
}