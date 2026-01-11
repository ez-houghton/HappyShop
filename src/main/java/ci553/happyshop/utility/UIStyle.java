package ci553.happyshop.utility;

/**
 * UIStyle is a centralized Java record that holds all JavaFX UI-related style and size constants
 * used across all client views in the system.
 *
 * These values are grouped here rather than being hardcoded throughout the codebase:
 * - improves maintainability, ensures style consistency,
 * - avoids hardcoded values scattered across the codebase.
 *
 * Example usages:
 * - UIStyle.HistoryWinHeight for setting the height of the order history window
 * - UIStyle.labelStyle for applying consistent styling to labels
 *
 * UIStyle is deliberately defined as a `record` instead of a normal class for several reasons:
 *  - Lightweight and memory-efficient: Records are designed to be compact data carriers
 *    with minimal memory overhead compared to traditional classes.
 *  - No instance needed: Since this holds only static constants, using a record clearly
 *    communicates that no state or behavior is expected.
 *  - Final and immutable by default: Records cannot be extended and implicitly prevent misuse.
 *  - Cleaner syntax: Avoids unnecessary boilerplate (constructors, getters, etc.).
 */

public record UIStyle() {

    public static final int customerWinWidth = 710;
    public static final int customerWinHeight = 300;
    public static final int removeProNotifierWinWidth = customerWinWidth/2 +160;
    public static final int removeProNotifierWinHeight = 230;

    public static final int pickerWinWidth = 310;
    public static final int pickerWinHeight = 300;

    public static final int trackerWinWidth = 210;
    public static final int trackerWinHeight = 300;

    public static final int warehouseWinWidth = 630;
    public static final int warehouseWinHeight = 300;
    public static final int AlertSimWinWidth = 300;
    public static final int AlertSimWinHeight = 170;
    public static final int HistoryWinWidth = 300;
    public static final int HistoryWinHeight = 140;

    public static final int EmergencyExitWinWidth = 200;
    public static final int EmergencyExitWinHeight = 300;

    public static final int selectWindowWidth = customerWinWidth +160;
    public static final int selectWindowHeight = 230;

    public static final String labelTitleStyle ="-fx-font-weight: bold; " +
            "-fx-font-size: 16px; -fx-text-fill: #005799;";

    public static final String labelStyle = "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: black; " +
            "-fx-background-color: transparent;";

    public static final String comboBoxStyle ="-fx-font-weight: bold; " +
            "-fx-font-size: 14px;" +
            "-fx-border:0;" +
            "-fx-border-radius:0;" +
            "-fx-background-color:#00cc00";

    public static final String trolleyStyle = "-fx-font-size:12px;" +
            "-fx-padding:2px,0px,2px,0px;" +
            "-fx-margin:0;" +
            "-fx-background-color:transparent;" +
            "-fx-border:0; -fx-border-radius:0;" +
            "-fx-alignment:CENTER_LEFT";
    public static final String trolleyButtons = "-fx-font-size:14px;" +
            "-fx-padding:2px,0,2px,0;" +
            "-fx-margin:3px;" +
            "-fx-min-width:20px;" +
            "-fx-max-width:20px;" +
            "-fx-background-color:#2ba3ff;" +
            "-fx-border:0;" +
            "-fx-text-fill:#fefefe";
    public static final String rootStyleBlue = "-fx-padding: 8px; " +
            "-fx-background-color: lightblue";

    public static final String rootStyleGray = "-fx-padding: 8px; " +
            "-fx-background-color: lightgray";
    public static final String rootStyle = "-fx-padding: 8px; " +
            "-fx-background-color: #f2f2f2;";


    public static final String textFieldStyle = "-fx-font-size: 16;" +
            "-fx-border:none;";

    public static final String labelMulLineStyle= "-fx-font-size: 16px; " +
            "-fx-background-color: transparent";

    public static final String listViewStyle = "-fx-border-color: #ccc; " +
            "-fx-border-width: 1px; -fx-background-color: white; -fx-font-size: 14px;";

    public static final String manageStockChildStyle = "-fx-background-color: lightgrey; " +
            "-fx-border-color: lightpink; " +
            "-fx-border-width: 3px; " +
            "-fx-padding: 5px;";

    public static final String manageStockChildStyle1 = "-fx-background-color: lightgrey; " +
            "-fx-border-color: #00ab06; " +
            "-fx-border-width: 3px; " +
            "-fx-padding: 5px;";
    public static final String buttonStyle = "-fx-background-color:#2ba3ff;" +
            "-fx-text-fill:#fefefe;" +
            "-fx-border:none;" +
            "-fx-border-radius:10px;" +
            "-fx-font-size: 15;";

    public static final String cancelButtonStyle = "-fx-background-color:#ff3333;" +
            "-fx-text-fill:#fefefe;" +
            "-fx-border:none;" +
            "-fx-border-radius:10px;" +
            "-fx-font-size: 15;";

    public static final String greenFillBtnStyle = "-fx-background-color: #009900; " +
            "-fx-text-fill: white; -fx-font-size: 14px;";
    public static final String redFillBtnStyle ="-fx-background-color: red; " +
            "-fx-text-fill: white; -fx-font-size: 14px; ";

    public static final String grayFillBtnStyle = "-fx-background-color: gray; " +
            "-fx-text-fill: white; -fx-font-size: 14px; ";

    public static final String alertBtnStyle ="-fx-background-color: green; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;";

    public static final String alertTitleLabelStyle = "-fx-font-size: 17px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #050505; " + "-fx-background-color: lightpink;";

    public static final String alertContentTextAreaStyle = "-fx-font-size: 14px;" +
            "-fx-font-weight: normal;-fx-control-inner-background: lightyellow; -fx-text-fill: darkblue;";

    public static final String alertContentUserActionStyle = "-fx-font-size: 14px;" +
            "-fx-font-weight: normal; -fx-text-fill: green;";

}
