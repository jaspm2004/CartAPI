package br.com.pismotest.cart.controllers;

import br.com.pismotest.cart.domain.CartProduct;
import br.com.pismotest.cart.domain.Product;
import br.com.pismotest.cart.repository.CartRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author José San Pedro
 */
@RestController
@RequestMapping(value = "/carts")
public class CartController {
    
    @Autowired
    private CartRepository repository;

    @Value(value = "${productapi.host}")
    private String host;

    @Value(value = "${productapi.port}")
    private int port;
    
    @Value(value = "${productapi.app}")
    private String app;
    
    /**
     * Retorna uma lista contendo os produtos que pertencem ao carrinho com o cartId correspondente
     * 
     * @param cartId id do carrinho
     * @return      200 se os produtos do carrinho são encontrados, 
     *              404 se não existe carrinho com esse cartId, 
     *              400 se o cartId não é recebido
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity listByCartId(@RequestParam(value = "cartId", required = false, defaultValue = "") Long cartId) {
        // se o cartId é null, retorna todos os produtos dos carrinhos
        if (cartId == null) {
            return new ResponseEntity(repository.findAll(), HttpStatus.OK);
        }
        
        // se o cartId não é null, filtra
        List<CartProduct> listById = repository.findByCartId(cartId);
        if (listById.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity(listById, HttpStatus.OK);
    }
    
    /**
     * Insere um novo produto no carrinho. Verifica se há stock disponível antes de inserir
     * 
     * @param cartProduct o produto que será inserido
     * @return      200 se o produto é inserido com sucesso no carrinho, 
     *              404 se falta informação para inserir,
     *              404 se não foi possível inserir o produto por falta de stock 
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createCartProduct(@RequestBody CartProduct cartProduct) {
        // o cartId e o productId não podem ser null
        if (cartProduct.getCartId() == null || cartProduct.getProductId() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        
        // verifica se a quantidade do produto no carrinho é menor que o stock
        try {
            // faz a contagem da quantidade de produtos do mesmo tipo já no cart
            long productId = cartProduct.getProductId();
            int productCount = repository.findByProductId(productId).size();
            System.out.println("productId: " + productId + " - on cart: " + productCount);
            
            String productAPIurl = "http://" + host + ":" + port + "/" + app + "/products/";
            URI urlObj;
            Product produto;
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            urlObj = new URI(productAPIurl + cartProduct.getProductId());
            produto = restTemplate.getForObject(urlObj, Product.class);
            System.out.println(produto.toString());
            
            // se a quantidade é menor do que o stock, pode acrescentar
            if (productCount < produto.getStock())
                return new ResponseEntity(repository.save(cartProduct), HttpStatus.OK);
        } catch (URISyntaxException ex) {
            Logger.getLogger(CartController.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Faz o checkout do carrinho. Atualiza o stock do produto durante o checkout
     * 
     * @param cartId    id do carrinho
     * @return      200 se o checkout foi feito com sucesso, 
     *              404 se não existe carrinho com esse cartId      
     */
    @RequestMapping(value = "{cartId}/checkout", method = RequestMethod.PUT)
    public ResponseEntity checkOut(@PathVariable("cartId") Long cartId) {
        // get product with the given id
        List<CartProduct> cartProducts = repository.findByCartId(cartId);
        
        // check if the product exists
        if (cartProducts == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        // lista os produtos no carrinho e atualiza o stock
        String productAPIurl = "http://" + host + ":" + port + "/" + app + "/products/";
        URI urlObj;
        Product produto;
        for (CartProduct cartProduct : cartProducts) {
            try {
                RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                urlObj = new URI(productAPIurl + cartProduct.getProductId() + "/stock/-1");
                produto = restTemplate.patchForObject(urlObj, null, Product.class);
                System.out.println(produto.toString());
                
                // apaga as entradas do carrinho
                repository.delete(cartProduct);
            } catch (URISyntaxException ex) {
                Logger.getLogger(CartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return new ResponseEntity(HttpStatus.OK);
    }
}