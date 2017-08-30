package br.com.pismotest.cart.services;

import br.com.pismotest.cart.controllers.CartController;
import br.com.pismotest.cart.domain.CartProduct;
import br.com.pismotest.cart.domain.Product;
import br.com.pismotest.cart.repository.CartRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author José San Pedro
 */
@Service
public class ProductService {
    
    @Autowired
    private CartRepository repository;
    
    @Value(value = "${productapi.host}")
    private String host;

    @Value(value = "${productapi.port}")
    private int port;
    
    @Value(value = "${productapi.app}")
    private String app;
    
    private final String PRODUCT_API_URL = "http://" + host + ":" + port + "/" + app + "/products/";
    
    public boolean isProductInStock(CartProduct cartProduct) {
        // faz a contagem da quantidade de produtos do mesmo tipo já no cart
        long productId = cartProduct.getProductId();
        int productCount = repository.findByProductId(productId).size();
        System.out.println("productId: " + productId + " - on cart: " + productCount);

        URI urlObj;
        Product produto;
        try {
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            urlObj = new URI(PRODUCT_API_URL + cartProduct.getProductId());
            produto = restTemplate.getForObject(urlObj, Product.class);
            System.out.println(produto.toString());

            // se a quantidade é menor do que o stock, pode acrescentar
            if (productCount < produto.getStock())
                return true;
        } catch (URISyntaxException ex) {
            Logger.getLogger(CartController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return false;
    }
    
    public void updateProductStock(CartProduct cartProduct) {
        URI urlObj;
        Product produto;
        
        try {
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            urlObj = new URI(PRODUCT_API_URL + cartProduct.getProductId() + "/stock/-1");
            produto = restTemplate.patchForObject(urlObj, null, Product.class);
            System.out.println(produto.toString());
        } catch (URISyntaxException ex) {
            Logger.getLogger(CartController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
