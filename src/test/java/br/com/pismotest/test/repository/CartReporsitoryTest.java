package br.com.pismotest.test.repository;

import br.com.pismotest.cart.domain.CartProduct;
import br.com.pismotest.cart.domain.Product;
import br.com.pismotest.cart.repository.CartRepository;
import br.com.pismotest.test.AbstractTest;
import static org.assertj.core.api.Java6Assertions.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Teste do repositório
 * 
 * @author José San Pedro
 */
@DataJpaTest
public class CartReporsitoryTest extends AbstractTest {
    
    @Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private CartRepository cartRepository;
    
    @Test
    public void whenFindByCartId_thenReturnCartProduct() throws Exception {
        // given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCartId(123l);
        cartProduct.setProductId(1l);
        entityManager.persist(cartProduct);
        entityManager.flush();

        // when
        CartProduct found = cartRepository.findByCartId(123l).get(0);

        // then
        assertThat(found.getCartId()).isEqualTo(cartProduct.getCartId());
        assertThat(found.getProductId()).isEqualTo(cartProduct.getProductId());
    }
}