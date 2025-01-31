package Services;

import Model.Customer;
import Model.Product;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();

    //Metodo para agregar un cliente
    public void addCustomer(String name, boolean customerFrequent, double discount){
        Customer customer = new Customer(name, customerFrequent, discount);
        customers.add(customer);
    }

    //Metodo para editar un cliente
// Método para editar un cliente
    public boolean EditCustomer(int id, Customer newCustomer) {
        Optional<Customer> customerExist = customers.stream()
                .filter(customer -> customer.getId() == id)
                .findFirst();

        if (customerExist.isPresent()) {
            Customer existingCustomer = customerExist.get(); // Cambié el nombre de la variable para mayor claridad
            existingCustomer.setName(newCustomer.getName()); // Llama al método en la instancia
            existingCustomer.setFrequent(newCustomer.getFrequent()); // Llama al método en la instancia
            existingCustomer.setDiscount(newCustomer.getDiscount()); // Llama al método en la instancia
            return true;
        }
        return false;
    }

    //Metodo para eliminar un cliente
    public boolean deleteCustomer(int id){
        return customers.removeIf(customer -> customer.getId() == id);
    }

    //Metodo para listar productos
    public List<Customer> listCustomers(){
        return new ArrayList<>(customers);
    }

    //Metodo buscar clientes por nombres
    public Customer searchCustomerName(String name){
        return customers.stream()
                .filter(customer -> customer.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


    public List<Customer> getAllCustomers() {
        return customers;
    }

    public boolean deleteCustomerByIndex(int index) {
        if (index >= 0 && index < customers.size()) {
            customers.remove(index); // Elimina el cliente de la lista original
            return true;
        }
        return false; // Retorna false si el índice es inválido
    }
}
