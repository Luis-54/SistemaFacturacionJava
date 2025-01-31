package Model;

public class Product {
    private static int nextId = 1; // Variable estática para generar IDs incrementales
    private final int id; // ID único del producto
    private String name;
    private double price;
    private int stock;

    // Constructor
    public Product(String name, double price, int stock) {
        this.id = nextId++; // Asignar y aumentar el ID
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // Métodos getters y setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}