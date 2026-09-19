package com.workintech.ecommerce.config;

import com.workintech.ecommerce.entity.Category;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.entity.ProductImage;
import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.repository.CategoryRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import com.workintech.ecommerce.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        return args -> {

            // Sistem rollerini oluşturur.
            initializeRoles(roleRepository);

            // Demo kategori ve ürünleri oluşturur.
            initializeProducts(
                    categoryRepository,
                    productRepository
            );
        };
    }

    private void initializeRoles(
            RoleRepository roleRepository
    ) {

        if (roleRepository.findByCode("admin").isEmpty()) {
            Role admin = new Role();
            admin.setCode("admin");
            admin.setName("Admin");
            roleRepository.save(admin);
        }

        if (roleRepository.findByCode("store").isEmpty()) {
            Role store = new Role();
            store.setCode("store");
            store.setName("Store");
            roleRepository.save(store);
        }

        if (roleRepository.findByCode("customer").isEmpty()) {
            Role customer = new Role();
            customer.setCode("customer");
            customer.setName("Customer");
            roleRepository.save(customer);
        }
    }

    private void initializeProducts(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {

        // Seed verileri daha önce eklendiyse tekrar ekleme.
        if (categoryRepository.count() > 0
                || productRepository.count() > 0) {
            return;
        }

        Category women = createCategory(
                "k:giyim",
                "Women's Clothing",
                "https://picsum.photos/seed/women-category/600/600",
                4.9
        );

        Category men = createCategory(
                "e:giyim",
                "Men's Clothing",
                "https://picsum.photos/seed/men-category/600/600",
                4.8
        );

        Category shoes = createCategory(
                "k:ayakkabi",
                "Shoes",
                "https://picsum.photos/seed/shoes-category/600/600",
                4.7
        );

        Category accessories = createCategory(
                "k:aksesuar",
                "Accessories",
                "https://picsum.photos/seed/accessories-category/600/600",
                4.6
        );

        Category electronics = createCategory(
                "e:elektronik",
                "Electronics",
                "https://picsum.photos/seed/electronics-category/600/600",
                4.5
        );

        categoryRepository.saveAll(
                List.of(
                        women,
                        men,
                        shoes,
                        accessories,
                        electronics
                )
        );

        Product tshirt = createProduct(
                "Classic Cotton T-Shirt",
                "Soft and comfortable cotton t-shirt for everyday wear.",
                new BigDecimal("29.99"),
                25,
                4.8,
                145,
                women
        );

        addImage(
                tshirt,
                "https://picsum.photos/seed/tshirt-1/800/1000",
                0
        );

        addImage(
                tshirt,
                "https://picsum.photos/seed/tshirt-2/800/1000",
                1
        );


        Product jacket = createProduct(
                "Modern Casual Jacket",
                "Lightweight casual jacket designed for everyday comfort.",
                new BigDecimal("89.99"),
                15,
                4.6,
                98,
                men
        );

        addImage(
                jacket,
                "https://picsum.photos/seed/jacket-1/800/1000",
                0
        );

        addImage(
                jacket,
                "https://picsum.photos/seed/jacket-2/800/1000",
                1
        );


        Product sneakers = createProduct(
                "Everyday Sneakers",
                "Comfortable sneakers suitable for daily use and walking.",
                new BigDecimal("74.50"),
                30,
                4.9,
                230,
                shoes
        );

        addImage(
                sneakers,
                "https://picsum.photos/seed/sneakers-1/800/1000",
                0
        );

        addImage(
                sneakers,
                "https://picsum.photos/seed/sneakers-2/800/1000",
                1
        );


        Product bag = createProduct(
                "Minimal Shoulder Bag",
                "A compact shoulder bag with a clean and modern design.",
                new BigDecimal("54.90"),
                18,
                4.5,
                76,
                accessories
        );

        addImage(
                bag,
                "https://picsum.photos/seed/bag-1/800/1000",
                0
        );


        Product headphones = createProduct(
                "Wireless Headphones",
                "Wireless over-ear headphones with clear sound and comfortable cushions.",
                new BigDecimal("129.99"),
                12,
                4.7,
                184,
                electronics
        );

        addImage(
                headphones,
                "https://picsum.photos/seed/headphones-1/800/1000",
                0
        );


        Product hoodie = createProduct(
                "Essential Hoodie",
                "Warm and comfortable hoodie with a relaxed everyday fit.",
                new BigDecimal("64.99"),
                20,
                4.4,
                67,
                men
        );

        addImage(
                hoodie,
                "https://picsum.photos/seed/hoodie-1/800/1000",
                0
        );

        productRepository.saveAll(
                List.of(
                        tshirt,
                        jacket,
                        sneakers,
                        bag,
                        headphones,
                        hoodie
                )
        );
    }

    private Category createCategory(
            String code,
            String title,
            String img,
            Double rating
    ) {

        Category category = new Category();

        category.setCode(code);
        category.setTitle(title);
        category.setImg(img);
        category.setRating(rating);

        return category;
    }

    private Product createProduct(
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            Double rating,
            Integer sellCount,
            Category category
    ) {

        Product product = new Product();

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setRating(rating);
        product.setSellCount(sellCount);
        product.setCategory(category);

        return product;
    }

    private void addImage(
            Product product,
            String url,
            Integer index
    ) {

        ProductImage image = new ProductImage();

        image.setUrl(url);
        image.setImageIndex(index);
        image.setProduct(product);

        product.getImages().add(image);
    }
}