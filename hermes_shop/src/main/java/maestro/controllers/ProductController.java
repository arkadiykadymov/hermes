package maestro.controllers;

import maestro.dto.NewProductDTO;
import maestro.dto.ProductDTO;
import maestro.properties.PaginationProperties;
import maestro.sevices.IProductService;
import maestro.sorting.ISorter;
import maestro.sorting.ProductBackendSorting;
import maestro.sorting.SortingValuesDTO;
import maestro.model.Product;
import maestro.repo.ProductRepo;
import maestro.sevices.imp.ProductService;
import maestro.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {

    @Value("${upload.path}")
    private String uploadPath;

    private final IProductService productService;
    private final ProductBackendSorting productBackendSorting;

    @Autowired
    public ProductController(IProductService productService, PaginationProperties paginationProperties) {
        this.productService = productService;
        this.productBackendSorting = new ProductBackendSorting(paginationProperties.getBackendProduct());
    }

    @GetMapping("/{category}/{page}")
    public ResponseEntity<Object> getProductsByCategory(
            SortingValuesDTO sortingValues,
            @PathVariable("category") String category,
            @PathVariable(value = "page", required = false) String page) {
        if(page != null){
            sortingValues.setPage(Integer.valueOf(page));
        }
        PageRequest request = productBackendSorting.updateSorting(sortingValues);
        Page<Product> p = productService.findByCategories(category, request);
        return Util.createResponseEntity(p);
    }


    @GetMapping("/all/{page}")
    public ResponseEntity<Object> getAllProducts(
            SortingValuesDTO sortingValues,
            @PathVariable(value = "page", required = false) String page) {
        if(page != null){
            sortingValues.setPage(Integer.valueOf(page));
        }
        PageRequest request = productBackendSorting.updateSorting(sortingValues);
        Page<Product> p = productService.findAll(request);
        return Util.createResponseEntity(p);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Object> addProduct(@ModelAttribute NewProductDTO productDTO,
                                             @RequestParam(required = false, name = "file") MultipartFile file) throws IOException {
        String resultFileName = "default.jpeg";
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File fileDir = new File(uploadPath);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            String uuidFileId = UUID.randomUUID().toString();
            resultFileName = uuidFileId + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
        }
        return Util.createResponseEntity(productService.createProduct(productDTO, resultFileName));
    }

    @GetMapping("delete/{productId}")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Object> deleteProducts(@PathVariable("productId") String name) {
        try {
            productService.deleteProductByName(name);
            return Util.createResponseEntity("Product deleted");
        } catch (Exception e) {
            return Util.createResponseEntity("Error" + e);
        }
    }


}
