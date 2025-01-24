package Model;

public class Customer {

    private int id;
    private String name;
    private boolean customerFrequent;
    private double discount;

    public Customer(int id, String  name, boolean customerFrequent, double discount){
        this.id = id;
        this.name = name;
        this.customerFrequent = false;
        this.discount = 0.0;
    }

    public boolean isCustomerFrequent(){
        return customerFrequent;
    }

    public double getDiscount(){
        return discount;
    }


}
