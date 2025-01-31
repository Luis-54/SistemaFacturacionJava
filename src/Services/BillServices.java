package Services;

import Model.Bill;
import Model.Customer;
import Model.Product;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BillServices {
    private static int lastBillNumber = 0;
    private final Map<String, DraftBill> drafts = new HashMap<>();
    private static final double IVA_RATE = 0.19; // 19% IVA
    private final List<Customer> customers = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<Bill> savedBills = new ArrayList<>();

    // Constructor
    public BillServices() {
        // Inicializa algunos productos y clientes para pruebas
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Agregar algunos productos de ejemplo
        products.add(new Product("Producto 1", 100.0, 1));
        products.add(new Product("Producto 2", 200.0, 2));
        products.add(new Product("Producto 3", 300.0, 3));
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }

    public Product getProductById(int id) {
        return products.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Product> searchProductsByName(String name) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    public String startNewDraft(Customer customer) {
        String draftId = "DRAFT-" + System.currentTimeMillis();
        drafts.put(draftId, new DraftBill(customer));
        return draftId;
    }

    public void addProductToDraft(String draftId, Product product) {
        DraftBill draft = getDraft(draftId);
        draft.products.add(product);
        draft.recalculateAmounts();
    }

    public void removeProductFromDraft(String draftId, Product product) {
        DraftBill draft = getDraft(draftId);
        draft.products.remove(product);
        draft.recalculateAmounts();
    }

    public DraftBill getDraft(String draftId) {
        DraftBill draft = drafts.get(draftId);
        if (draft == null) {
            throw new IllegalArgumentException("Borrador no encontrado: " + draftId);
        }
        return draft;
    }

    public Map<String, Double> getDraftPriceDetails(String draftId) {
        DraftBill draft = getDraft(draftId);
        Map<String, Double> details = new HashMap<>();
        details.put("subtotal", draft.getSubtotal());
        details.put("discount", draft.getDiscount());
        details.put("tax", draft.getTax());
        details.put("total", draft.getTotal());
        return details;
    }

    public double getProductPriceWithTax(Product product) {
        return product.getPrice() * (1 + IVA_RATE);
    }

    public Bill createBillFromDraft(String draftId) {
        DraftBill draft = getDraft(draftId);
        lastBillNumber++;
        Bill bill = new Bill(lastBillNumber, draft.customer, draft.products, draft.getTotal());
        drafts.remove(draftId);
        return bill;
    }

    public void cancelDraft(String draftId) {
        drafts.remove(draftId);
    }

    public void saveBill(Bill bill) {
        savedBills.add(bill);
    }

    public List<Bill> getAllSavedBills() {
        return savedBills;
    }

    private class DraftBill {
        private final Customer customer;
        private final List<Product> products;
        private double subtotal;
        private double discount;
        private double tax;
        private double total;

        public DraftBill(Customer customer) {
            this.customer = customer;
            this.products = new ArrayList<>();
            this.subtotal = 0.0;
            this.discount = 0.0;
            this.tax = 0.0;
            this.total = 0.0;
        }

        public void recalculateAmounts() {
            // Calcular el subtotal sumando los precios de los productos
            this.subtotal = products.stream()
                    .mapToDouble(Product::getPrice) // Asegúrate de usar mapToDouble
                    .sum(); // Sumar todos los precios

            // Calcular el descuento basado en si el cliente es frecuente
            this.discount = customer.isCustomerFrequent() ? subtotal * (customer.getDiscount() / 100.0) : 0.0;

            // Calcular el IVA y el total
            this.tax = (subtotal - discount) * IVA_RATE;
            this.total = subtotal - discount + tax;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public double getDiscount() {
            return discount;
        }

        public double getTax() {
            return tax;
        }

        public double getTotal() {
            return total;
        }
    }
}