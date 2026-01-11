package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;
import ci553.happyshop.utility.UIStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

/**
 * Model for Customer Package
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
     * @throws SQLException if SQL error
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
     * @throws SQLException  if SQL error
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

    /**
     * Adds a product to the trolley list. If no product has been searched, outputs an error.
     */
    void addToTrolley(){
        if(theProduct!= null){
            trolley.add(theProduct);
        }
        else{
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");
        }
        displayTaReceipt=""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    /**
     * Attempts to checkout with the user's current trolley
     * @throws IOException if issue with input or output from files
     * @throws SQLException if SQL error
     */
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

                //You can use the provided RemoveProductNotifier class and its showRemovalMsg method for this purpose.
                //remember close the message window where appropriate (using method closeNotifierWindow() of RemoveProductNotifier class)
                displayTaTrolley = "Checkout failed due to insufficient stock:\n" + errorMsg;
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
     *Groups products by their productId to optimize database queries and updates.
     * By grouping products, we can check the stock for a given `productId` once, rather than repeatedly
     * @param proList list of ungrouped products
     * @return List of grouped products
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
        ArrayList<Product> ret = new ArrayList<>(grouped.values());
        Collections.sort(ret);
        return ret;
    }

    /**
     * Clears current Trolley
     */
    void cancel(){
        trolley.clear();
        displayTaTrolley="";
        updateView();
    }

    /**
     * Clears receipt.
     */
    void closeReceipt(){
        displayTaReceipt="";
    }

    /**
     * Creates the trolley object to be outputted to user
     * @return trolleyList. VBox full of Hboxes, which has product information for trolleyed products
     */
    private ArrayList<Node> createTrolleyList(){
        ArrayList<Node> trolleyList= new ArrayList<>();
        for(Product pr : groupProductsById(trolley)) {
            Button addButton = new Button("+");
            addButton.setOnAction(actionEvent -> {
                pr.setOrderedQuantity(1);
                trolley.add(pr);
                updateView();
            });
            Button subButton = new Button("-");
            subButton.setOnAction(actionEvent -> {
                for(Product i : trolley){
                    if( i.getProductId().equals(pr.getProductId()))   {
                        trolley.remove(i);
                        break;}
                }
                updateView();
            });
            subButton.setStyle(UIStyle.trolleyButtons);
            addButton.setStyle(UIStyle.trolleyButtons);

            TextField pDesc = new TextField(pr.getProductDescription());
            pDesc.setEditable(false);
            pDesc.setStyle(UIStyle.trolleyStyle);

            TextField pQuant = new TextField(Integer.toString(pr.getOrderedQuantity()));
            pQuant.setPrefWidth(50);    pQuant.setEditable(false);
            pQuant.setStyle(UIStyle.trolleyStyle);

            TextField pPrice = new TextField(String.format("%.2f",(pr.getOrderedQuantity() * pr.getUnitPrice())));
            pPrice.setEditable(false);
            pPrice.setStyle(UIStyle.trolleyStyle);
            pPrice.setPrefWidth(70);

            HBox itemInfo = new HBox(addButton,subButton, pDesc, pQuant, pPrice);
            itemInfo.setAlignment(Pos.CENTER);
            itemInfo.setMaxWidth(290);
            itemInfo.setPrefWidth(290);
            itemInfo.setPadding(new Insets(0,0,3,0));

            trolleyList.add(itemInfo);
        }
        return trolleyList;
    }

    /**
     * Updates the view with model's new information.
     */
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

        cusView.update(imageName, displayLaSearchResult, createTrolleyList(),displayTaTrolley,displayTaReceipt);
    }
     // extra notes:
     //Path.toUri(): Converts a Path object (a file or a directory path) to a URI object.
     //File.toURI(): Converts a File object (a file on the filesystem) to a URI object

}
