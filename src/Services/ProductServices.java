package Services;

import Model.Product;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ProductServices {
    private final List<Product> products = new ArrayList<>();

    // Método para agregar un Producto
    public void addProduct(String name, double price, int stock) {
        Product product = new Product(name, price, stock);
        products.add(product);
    }

    // Método para editar un producto
    public boolean editProduct(int id, Product newProduct) {
        Optional<Product> productExist = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (productExist.isPresent()) {
            Product existingProduct = productExist.get();
            existingProduct.setName(newProduct.getName());
            existingProduct.setPrice(newProduct.getPrice());
            existingProduct.setStock(newProduct.getStock());
            return true;
        }
        return false;
    }

    // Método para eliminar un Producto
    public boolean deleteProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }

    // Método para listar productos
    public List<Product> listProducts() {
        return new ArrayList<>(products);
    }

    // Método buscar producto por nombre
    public Product searchProductName(String name) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
