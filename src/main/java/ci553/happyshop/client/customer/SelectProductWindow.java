package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WindowBounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SelectProductWindow {
    public CustomerView cusView; //tracking the window of cusView
    public boolean closed = false;
    private static int WIDTH = UIStyle.selectWindowWidth;
    private static int HEIGHT = UIStyle.selectWindowHeight;

    private Stage window; //window for Select Product
    private Scene scene; // Scene for Select Product
    private HBox itemList;
    public Product selected;

    /**
     * Creates a container box to show information about the item.
     * @param prod the item to be viewed
     * @return a vbox containing information about the product.
     */
    private VBox showItem(Product prod){
        String imageName = prod.getProductImageName();
        String relativeImageUrl = StorageLocation.imageFolder +imageName;
        Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
        String imURL = imageFullPath.toUri().toString();
        ImageView iv = new ImageView(imURL);
        TextArea text = new TextArea();
        text.setEditable(false);
        String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", prod.getProductId(), prod.getProductDescription(), prod.getUnitPrice());
        String quantityInfo = String.format("\n%d units left.", prod.getStockQuantity());
        text.setPrefRowCount(4);
        text.setText(baseInfo+quantityInfo);
        Button selectButton= new Button("Select");
        selectButton.setOnAction(e ->{
            selected=prod;
            window.close();
        });
        VBox container =  new VBox(iv, text,selectButton);
        container.setStyle("-fx-border-color:black");
        return container;
    }



    /**
     * Creates the Select Product Window.
     * @param prodList The list of products to select from
     */
    public void createWindow(ArrayList<Product> prodList) {
        Label laTitle = new Label("Select a Product!"); // ⚠️
        laTitle.setStyle(UIStyle.alertTitleLabelStyle);
        itemList= new HBox();
        itemList.setStyle("-fx-border-color:black");
        for(Product i : prodList){
            itemList.getChildren().add(showItem(i));
        }

        Button closeButton = new Button("None of these.");
        closeButton.setOnAction(e ->{
            selected=null;
            window.close();
        });
        // Top level GridPane layout
        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.add(laTitle, 0, 0);

        pane.add(itemList,0,1);
        pane.add(closeButton, 0, 2);
        pane.setStyle(UIStyle.rootStyleGray);

        scene = new Scene(pane, WIDTH, HEIGHT);

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //Sets window as forced, cannot select any other window until this is closed
        window.setTitle("Select a Product");
        window.setScene(scene);

        //get bounds of betterCustomer window which trigers the window
        // so that we can put the window at a suitable position
        WindowBounds bounds = cusView.getWindowBounds();
        window.setX(bounds.x + bounds.width -WIDTH -10); // Position to the right of warehouse window
        window.setY(bounds.y + bounds.height / 2 + 40);
        window.showAndWait();
    }
}
