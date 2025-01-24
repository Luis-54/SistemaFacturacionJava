package Services;

import Model.Product;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ProductServices {

    private List<Product> products = new ArrayList<>();

    //Metodo para agregar un Producto
    public void addProduct(Product product){
        product.add(product);
    }

    //Metodo para editar un producto
    public boolean editarProduct(int id, Product newProduct){
        Optional<Product> productExist = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (productExist.isPresent()){
            Product products = productExist.get();
            products.setName(newProduct.getName());
            products.setPrice(newProduct.getPrice());
            products.setStock(newProduct.getStock());
            return true;
        }
        return false;
    }

    //Metodo para eliminar un Producto
    public boolean deleteProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }

    //Metodo para listar productos
    public List<Product> listaProducts(){
        return new ArrayList<>(products);
    }

    //Metodo buscar producto por nombre
    public Product searchProductName(String name){
        return products.stream()
                .filter(p -> p .getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


}
