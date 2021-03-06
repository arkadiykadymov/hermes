package maestro.sevices.imp;

import maestro.dto.NewProductDTO;
import maestro.exceptions.UnknownEntityException;
import maestro.model.Cart;
import maestro.model.CartItem;
import maestro.model.Product;
import maestro.repo.CartRepo;
import maestro.repo.ProductRepo;
import maestro.sevices.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final CartRepo cartRepo;

    @Autowired
    public ProductService(ProductRepo productRepo, CartRepo cartRepo) {
        this.productRepo = productRepo;
        this.cartRepo = cartRepo;
    }

    @Override
    @Transactional
    public boolean createProduct(NewProductDTO newProductDTO, String filename) {
        try {
            Set<String> cats = new HashSet<>(newProductDTO.getCategories());
            Product product = new Product.Builder()
                    .withName(newProductDTO.getName())
                    .withDesc(newProductDTO.getDescription())
                    .withPrice(newProductDTO.getPrice())
                    .withStorageCount(newProductDTO.getStorageCount())
                    .withFilename(filename)
                    .withCategories(cats)
                    .build();
            productRepo.save(product);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Product> findByAvailability(String available) {
        List<Product> products;
        if ("all".equals(available))
            products = productRepo.findAll();
        else {
            products = productRepo.findAll().stream()
                    .filter((product -> product.getStorageCount() > 0))
                    .collect(Collectors.toList());
        }
        return products;
    }

    @Override
    @Transactional
    public void deleteProductByName(String productName) {
        Product p = productRepo.findByName(productName);
        productRepo.deleteById(p.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductByName(String name) {
        return productRepo.findByName(name);
    }

    @Override
    public Page<Product> findByCategories(String category, PageRequest pageable) {
        return productRepo.findByCategories(category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(PageRequest pageRequest) {
        return productRepo.findAll(pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(String category) {
        return null;
    }

    @Override
    @Transactional
    public Product getProduct(Long productId) throws UnknownEntityException {
        return productRepo.findById(productId)
                .orElseThrow(() -> new UnknownEntityException(Product.class, productId));
    }
}
