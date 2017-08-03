package br.com.pismotest.cart.repository;

import br.com.pismotest.cart.domain.CartProduct;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author José San Pedro
 */
@Repository
public interface CartRepository extends CrudRepository<CartProduct, Long> {
    @Override
    CartProduct findOne(Long id);
    
    List<CartProduct> findByCartId(Long cartId);
    
    List<CartProduct> findByProductId(Long productId);
}
