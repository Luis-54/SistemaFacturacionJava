package Model;
import java.util.List;

public class Bill {

    private int numBill;
    private Customer customer;
    private List<Product> products;
    private double total;
    private double tax;
    private double totalWhitTax;

    public Bill(int numBill, Customer customer, List products, double total){
        this.numBill = numBill;
        this.customer = customer;
        this.products = products;
        calculateTotal();
    }

    private void calculateTotal(){
        total = products.stream().mapToDouble(Product :: getPrice).sum();
        if (customer.isCustomerFrequent()){
            total -= total * customer.getDiscount() / 100;
        }
        tax = total * 0.19;
        totalWhitTax = total + tax;
    }


}
