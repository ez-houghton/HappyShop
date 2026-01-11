package ci553.happyshop.client.picker;

import java.io.IOException;

public class PickerController {
    public PickerModel pickerModel;

    /**
     * Runs the progressing method in Model
     * @throws IOException if issue with file handling
     */
    public void doProgressing() throws IOException {
        pickerModel.doProgressing();
    }

    /**
     * Runs collected method in Model
     * @throws IOException if issue with file handling
     */
    public void doCollected() throws IOException {
        pickerModel.doCollected();
    }
}
