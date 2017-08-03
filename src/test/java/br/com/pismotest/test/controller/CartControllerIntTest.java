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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Teste do controller
 * 
 * @author José San Pedro
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
                classes = Application.class)
@AutoConfigureMockMvc
public class CartControllerIntTest {
    
    @Autowired
    private MockMvc mvc;
 
    @Autowired
    private CartRepository repository;
    
    @Test
    public void givenCartProducts_whenGetCartProducts_thenStatus200() throws Exception {
        // given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCartId(123l);
        cartProduct.setProductId(1l);
        repository.save(cartProduct);

        // then
        mvc.perform(get("/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cartId", is(cartProduct.getCartId().intValue())))
                .andExpect(jsonPath("$[0].productId", is(cartProduct.getProductId().intValue())));
    }
}