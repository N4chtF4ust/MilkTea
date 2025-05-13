package com.kiosk;
import java.util.Scanner;

import com.kiosk.Model.Product;
public class AddProductConsole {
    private Scanner scanner;
    private clientSideCart milkTeaShop;

    public AddProductConsole(clientSideCart milkTeaShop) {
        this.milkTeaShop = milkTeaShop;
        this.scanner = new Scanner(System.in);
    }

    public void addProduct() {
    	inneraddProduct();
    }

    public void inneraddProduct(){
    	System.out.print("Enter product id: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Price1: ");
        double price1 = scanner.nextDouble();

        System.out.print("Price2: ");
        double price2 = scanner.nextDouble();

        System.out.print("Price3: ");
        double price3 = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Image filename: ");
        String image = scanner.nextLine();

        System.out.print("Available (true/false): ");
        boolean available = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline

        // Add the product to the MilkTeaShop products list
        clientSideCart.products.add(new Product(id, name, price1, price2, price3, image, available));

        // Refresh the product panel
        milkTeaShop.refreshProductPanel();
        System.out.println("Product added successfully!");
        addProduct();

    }
}
