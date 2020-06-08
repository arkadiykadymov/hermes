package maestro.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "cart_item")
public class CartItem implements Serializable {

    @EmbeddedId
    private CartItemId pk = new CartItemId();

    @JoinColumn(name = "cart_id")
    @ManyToOne
    private Cart cart;

    @MapsId("productId")
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    public CartItem() {
    }

    public CartItem(Cart shoppingCart, Product product, int quantity) {
        this.pk = new CartItemId(shoppingCart.getId(), product.getId());
        this.cart = shoppingCart;
        this.product = product;
        this.quantity = quantity;
    }

    public double calculateCost() {
        return quantity * product.getPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Cart getOrder() {
        return cart;
    }

    public void setOrder(Cart cart) {
        this.cart = cart;
        pk.setCart(cart.getId());
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        pk.setProduct(product.getId());
    }

    public CartItemId getPk() {
        return pk;
    }

    public void setPk(CartItemId pk) {
        this.pk = pk;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CartItem that = (CartItem) o;
        return Objects.equals(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return (pk != null ? pk.hashCode() : 0);
    }
}