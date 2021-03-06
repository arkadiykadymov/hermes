package maestro.sevices;

import maestro.dto.CartItemDTO;
import maestro.dto.ListItemsDTO;
import maestro.dto.NewProductDTO;
import maestro.model.Cart;

import java.util.List;

public interface ICartService {
    Cart getCartOrCreate(String userEmail);
    void addToCart(String username, String productName, int quantity) throws  maestro.exceptions.UnknownEntityException;
    Cart setDelivery(String username, boolean deliveryIncluded);
    Cart clearCart(String userEmail);
    ListItemsDTO getAllCartItems(String username);
    void deleteProductFromCart(String username, String productId);
    int getCartItemsCount(String username);
    List<Cart> getAllCarts();
    void saveAll(List<Cart> cartList);
}
