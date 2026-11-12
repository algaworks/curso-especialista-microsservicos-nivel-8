package com.algaworks.algashop.ordering.core.ports.in.shoppingcart;

import java.math.BigDecimal;
import java.util.UUID;

public interface ForManagingShoppingCarts {
    void addItem(ShoppingCartItemInput input);
    UUID createNew(UUID rawCustomerId);
    void removeItem(UUID rawShoppingCartId, UUID rawShoppingCartItemId);
    void empty(UUID rawShoppingCartId);
    void delete(UUID rawShoppingCartId);
    void changeProductAvailability(UUID productId, boolean available);
    void refreshProductPrice(UUID productId, BigDecimal salePrice);
}
