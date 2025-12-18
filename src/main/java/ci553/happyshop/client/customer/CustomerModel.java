package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * TODO
 * You can either directly modify the CustomerModel class to implement the required tasks,
 * or create a subclass of CustomerModel and override specific methods where appropriate.
 */
public class CustomerModel {
    public CustomerView cusView;
    public DatabaseRW databaseRW; //Interface type, not specific implementation
                                  //Benefits: Flexibility: Easily change the database implementation.

    private Product theProduct =null; // product found from search
    private ArrayList<Product> trolley =  new ArrayList<>(); // a list of products in trolley

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "No Product was searched yet"; // Label showing search result message (Search Page)
    private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";                                // Text area content showing receipt after checkout (Receipt Page);
    //SELECT productID, description, image, unitPrice,inStock quantity

    /**
     * Searches database for products matching the name or ID in the Search box
     * @throws SQLException
     */
    void search() throws SQLException{
        String prodInput = cusView.tfSearch.getText().trim();
        if(!prodInput.isEmpty()){
            theProduct= databaseRW.searchByProductId(prodInput);
            if(theProduct==null){
                theProduct=searchByName(prodInput);
            }
            if(theProduct!=null){
                String productId = theProduct.getProductId();
                double unitPrice = theProduct.getUnitPrice();
                String description = theProduct.getProductDescription();
                int stock = theProduct.getStockQuantity();
                String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", productId, description, unitPrice);
                String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                displayLaSearchResult = baseInfo + quantityInfo;
                System.out.println(displayLaSearchResult);
            }else{
                displayLaSearchResult = "No Product was found with given search term: " + prodInput;
                System.out.println("No Product was found with search term: " + prodInput);
            }
            updateView();

        }
    }

    /**
     * Searches database for product by name. opens a window to specify selection if more than one result returned.
     * @param prodInput searchable name
     * @return Product which is selected.
     * @throws SQLException
     */
    private Product searchByName(String prodInput) throws SQLException{
        SelectProductWindow productWindow = new SelectProductWindow();
        productWindow.cusView=cusView;
        ArrayList<Product> prodList= databaseRW.searchProduct(prodInput);
        if (prodList.size()>1){
            productWindow.createWindow(prodList);
            return productWindow.selected;
        }else if(prodList.size()==1){return prodList.getFirst();}
        else{return null;}
    }


    void addToTrolley(){
        if(theProduct!= null){

            // trolley.add(theProduct) — Product is appended to the end of the trolley.
            // To keep the trolley organized, add code here or call a method that:

            trolley.add(theProduct);
            displayTaTrolley = ProductListFormatter.buildString(groupProductsById(trolley)); //build a String for trolley so that we can show it


        }
        else{
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");
        }
        displayTaReceipt=""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    void checkOut() throws IOException, SQLException {
        if(!trolley.isEmpty()){
            // Group the products in the trolley by productId to optimize stock checking
            // Check the database for sufficient stock for all products in the trolley.
            // If any products are insufficient, the update will be rolled back.
            // If all products are sufficient, the database will be updated, and insufficientProducts will be empty.
            // Note: If the trolley is already organized (merged and sorted), grouping is unnecessary.
            ArrayList<Product> groupedTrolley= groupProductsById(trolley);
            ArrayList<Product> insufficientProducts= databaseRW.purchaseStocks(groupedTrolley);

            if(insufficientProducts.isEmpty()){ // If stock is sufficient for all products
                //get OrderHub and tell it to make a new Order
                OrderHub orderHub =OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(trolley);
                trolley.clear();
                displayTaTrolley ="";
                displayTaReceipt = String.format(
                        "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                        theOrder.getOrderId(),
                        theOrder.getOrderedDateTime(),
                        ProductListFormatter.buildString(theOrder.getProductList())
                );
                System.out.println(displayTaReceipt);
            }
            else{ // Some products have insufficient stock — build an error message to inform the customer
                StringBuilder errorMsg = new StringBuilder();
                for(Product p : insufficientProducts){
                    errorMsg.append("\u2022 "+ p.getProductId()).append(", ")
                            .append(p.getProductDescription()).append(" (Only ")
                            .append(p.getStockQuantity()).append(" available, ")
                            .append(p.getOrderedQuantity()).append(" requested)\n");
                }
                theProduct=null;

                //TODO
                // Add the following logic here:
                // 1. Remove products with insufficient stock from the trolley.
                // 2. Trigger a message window to notify the customer about the insufficient stock, rather than directly changing displayLaSearchResult.
                //You can use the provided RemoveProductNotifier class and its showRemovalMsg method for this purpose.
                //remember close the message window where appropriate (using method closeNotifierWindow() of RemoveProductNotifier class)
                displayLaSearchResult = "Checkout failed due to insufficient stock for the following products:\n" + errorMsg.toString();
                System.out.println("stock is not enough");
            }
        }
        else{
            displayTaTrolley = "Your trolley is empty";
            System.out.println("Your trolley is empty");
        }
        updateView();
    }

    /**
     * Groups products by their productId to optimize database queries and updates.
     * By grouping products, we can check the stock for a given `productId` once, rather than repeatedly
     */
    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                // Make a shallow copy to avoid modifying the original
                grouped.put(id,new Product(p.getProductId(),p.getProductDescription(),
                        p.getProductImageName(),p.getUnitPrice(),p.getStockQuantity()));
            }
        }
        return new ArrayList<>(grouped.values());
    }

    void cancel(){
        trolley.clear();
        displayTaTrolley="";
        updateView();
    }
    void closeReceipt(){
        displayTaReceipt="";
    }
    private ArrayList<Node> createTrolleyList(){
        ArrayList<Node> trolleyList= new ArrayList<>();
        for(Product pr : groupProductsById(trolley)) {
            Button addButton = new Button("+");
            addButton.setOnAction(actionEvent -> {
                pr.setOrderedQuantity(1);
                trolley.add(pr);
                System.out.println("Ordered quant:"+pr.getOrderedQuantity());
                updateView();
            });
            Button subButton = new Button("-");
            subButton.setOnAction(actionEvent -> {

                for(Product i : trolley){
                    if( i.getProductId().equals(pr.getProductId()))   {
                        trolley.remove(i);
                        break;
                    }
                }
                System.out.println(trolley);
                System.out.println("Ordered quant:"+pr.getOrderedQuantity());
                updateView();
            });
            subButton.setPadding(new Insets(2,0,2,0));
            addButton.setPadding(new Insets(2,0,2,0));
            subButton.setMinWidth(20);
            addButton.setMinWidth(20);
            TextField pID = new TextField(pr.getProductId());
            pID.setEditable(false);
            pID.setPrefWidth(110);
            pID.setPadding(new Insets(2,0,2,0));
            TextField pDesc = new TextField(pr.getProductDescription());
            pDesc.setEditable(false);
            pDesc.setPrefWidth(200);
            pDesc.setPadding(new Insets(2,0,2,0));
            TextField pQuant = new TextField(Integer.toString(pr.getOrderedQuantity()));
            pQuant.setPrefWidth(100);
            pQuant.setEditable(false);
            pQuant.setPadding(new Insets(2,0,2,0));
            TextField pPrice = new TextField(Double.toString(pr.getOrderedQuantity() * pr.getUnitPrice()));
            pPrice.setEditable(false);
            pPrice.setPadding(new Insets(2,0,2,0));
            HBox itemInfo = new HBox(addButton,subButton,pID, pDesc, pQuant, pPrice);
            trolleyList.add(itemInfo);
        }
        return trolleyList;
    }
    void updateView() {
        if(theProduct != null){
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder +imageName; //relative file path, eg images/0001.jpg
            // Get the full absolute path to the image
            Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
            imageName = imageFullPath.toUri().toString(); //get the image full Uri then convert to String
            System.out.println("Image absolute path: " + imageFullPath); // Debugging to ensure path is correct
        }
        else{
            imageName = "imageHolder.jpg";
        }

        cusView.update(imageName, displayLaSearchResult, createTrolleyList(),displayTaReceipt);
    }
     // extra notes:
     //Path.toUri(): Converts a Path object (a file or a directory path) to a URI object.
     //File.toURI(): Converts a File object (a file on the filesystem) to a URI object

    //for test only
    public ArrayList<Product> getTrolley() {
        return trolley;
    }
}
