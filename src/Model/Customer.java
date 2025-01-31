package Model;

public class Customer {
    private static int nextId = 1; // Variable estática para generar IDs incrementales
    private int id; // ID único del sistema
    private String name;
    private int cedula; // Nuevo campo para la cédula
    private boolean customerFrequent;
    private double discount;
    private int purchaseCount;

    // Constructor completo
    public Customer(String name, int cedula, boolean customerFrequent, double discount) {
        this.id = nextId++; // Asignar y incrementar el ID
        this.name = name;
        this.cedula = cedula;
        this.customerFrequent = customerFrequent;
        this.discount = discount;
        this.purchaseCount = 0;
    }

    public Customer(String name, boolean customerFrequent, double discount) {
    }

    // Métodos para incrementar el conteo de compras
    public void incrementPurchaseCount() {
        this.purchaseCount++;
        // Actualizar si es cliente frecuente
        if (this.purchaseCount >= 5) {
            this.customerFrequent = true;
        }
    }

    // Getters y Setters actualizados
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public boolean isCustomerFrequent() {
        return customerFrequent;
    }

    public void setCustomerFrequent(boolean customerFrequent) {
        this.customerFrequent = customerFrequent;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    // Método para verificar si un cliente es frecuente
    public boolean getFrequent() {
        return customerFrequent;
    }

    // Métodos adicionales
    public void add(Customer customer) {
        // Implementación opcional
    }

    // Método toString para facilitar la depuración y representación
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cedula=" + cedula +
                ", customerFrequent=" + customerFrequent +
                ", discount=" + discount +
                ", purchaseCount=" + purchaseCount +
                '}';
    }

    // Método equals para comparación
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return cedula == customer.cedula || name.equalsIgnoreCase(customer.name);
    }

    public void setFrequent(boolean frequent) {

    }
}
