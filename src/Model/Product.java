package Model;

public class Product {

    //Atributos o Variable
    private static int id = 0;
    public String name;
    private double price;
    private int stock;


    // Constructores

    public Product(int id, String name, double price, int stock){
        this.id = ++id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void add(Product product) {
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String setName(String name){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public double setPrice( double price){
        return price;
    }

    public int getStock(){
        return stock;
    }

    public int setStock(int stock){
        return stock;
    }



}
