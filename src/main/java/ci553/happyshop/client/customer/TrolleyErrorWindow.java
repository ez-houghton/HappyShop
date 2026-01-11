package ci553.happyshop.client.customer;

import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WindowBounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class TrolleyErrorWindow {
    public CustomerView cusView; //tracking the window of cusView

    private final int WIDTH = UIStyle.selectWindowWidth;
    private Stage window; //window for Select Product
    private Scene scene; // Scene for Select Product

    /**
     * Creates the checkout error window.
     * @param trolleyError The error.
     */
    public void createWindow(String trolleyError) {
        Label laTitle = new Label("Cannot checkout!");
        laTitle.setStyle(UIStyle.labelTitleStyle);
        Label error = new Label(trolleyError+"\nPlease edit your trolley and try again.");
        error.setStyle(UIStyle.labelStyle);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e ->{
            window.close();
        });

        closeButton.setStyle(UIStyle.cancelButtonStyle);
        VBox main = new VBox(9,laTitle,error,closeButton);

        main.setStyle(UIStyle.rootStyle);
        main.setAlignment(Pos.TOP_CENTER);
        scene = new Scene(main);

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //Sets window as forced, cannot select any other window until this is closed
        window.setTitle("Checkout Error");

        window.setScene(scene);

        //get bounds of betterCustomer window which trigers the window
        // so that we can put the window at a suitable position
        WindowBounds bounds = cusView.getWindowBounds();
        window.setX(bounds.x + bounds.width -WIDTH -10); // Position to the right of warehouse window
        window.setY(bounds.y + bounds.height / 2 + 40);
        window.showAndWait();
    }
}
