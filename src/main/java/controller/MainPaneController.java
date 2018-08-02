package controller;

import function.RotateLeftFunction;
import function.RotateRightFunction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import locale.Context;
import locale.ContextChangeListener;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import plugin.SimplePlugin;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;

public class MainPaneController implements Initializable {

    @FXML
    private Menu menuFile;

    @FXML
    private MenuItem menuItemClose;

    @FXML
    private Menu menuLanguage;

    @FXML
    private CheckMenuItem menuItemEnglish;

    @FXML
    private CheckMenuItem menuItemPolish;

    @FXML
    private Menu menuPhoto;

    @FXML
    private MenuItem menuItemLoad;

    @FXML
    private MenuItem menuItemSave;

    @FXML
    private ImageView imageView;

    @FXML
    private Button buttonUndo;

    @FXML
    private Button buttonRedo;

    @FXML
    private Button buttonClear;

    @FXML
    private Button buttonLeftRotate;

    @FXML
    private Button buttonRightRotate;

    @FXML
    private VBox toolbar;


    private List<SimplePlugin> simplePlugins;
    private List<SimplePlugin> actions;
    private List<SimplePlugin> deletedActions;
    private List<Button> pluginButtons;
    private Mat mat;
    private Mat clearImage;
    private Context context;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();

        loadPlugins("plugins");

        addComponentAcions();

        setTextes();
    }

    private void init() {
        simplePlugins = new ArrayList<>();
        actions = new ArrayList<>();
        deletedActions = new ArrayList<>();
        pluginButtons = new ArrayList<>();
        File file = new File("first.PNG");
        if (file.exists()) {
            mat = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
        } else {
            mat = new Mat(2, 2, Imgcodecs.IMREAD_COLOR);
        }
        clearImage = new Mat();
        mat.copyTo(clearImage);
        updateImage();
        context = new Context("MyResources");
    }

    private void addComponentAcions() {
        menuItemClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });

        buttonClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearImage.copyTo(mat);
                actions.clear();
                deletedActions.clear();
                updateImage();
            }
        });

        buttonUndo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int size = actions.size();
                if (size > 0) {
                    SimplePlugin lastItem = actions.get(size - 1);
                    actions.remove(size - 1);
                    deletedActions.add(lastItem);
//                    refresh();
                    refreshWithoutClear();
                }
            }
        });

        buttonRedo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int size = deletedActions.size();
                if (size > 0) {
                    SimplePlugin lastItem = deletedActions.get(size - 1);
                    deletedActions.remove(size - 1);
                    actions.add(lastItem);
                    lastItem.actionOnImage(mat);
                    updateImageWithoutClear();
                }
            }
        });

        buttonLeftRotate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SimplePlugin rotateLeftFunction = new RotateLeftFunction();
                actions.add(rotateLeftFunction);
                rotateLeftFunction.actionOnImage(mat);
                updateImage();
            }
        });

        buttonRightRotate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SimplePlugin rotateRightFunction = new RotateRightFunction();
                actions.add(rotateRightFunction);
                rotateRightFunction.actionOnImage(mat);
                updateImage();
            }
        });

        menuItemEnglish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                context.setLocale(Locale.ENGLISH);
                menuItemPolish.setSelected(false);

            }
        });

        menuItemPolish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                System.out.println("Wchodzi tu");
                context.setLocale(new Locale("PL"));
                menuItemEnglish.setSelected(false);

            }
        });

        menuItemLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!actions.isEmpty()) {
                    ButtonType buttonYes = new ButtonType(context.getBundle().getString("yes"), ButtonBar.ButtonData.YES);
                    ButtonType buttonNo = new ButtonType(context.getBundle().getString("no"), ButtonBar.ButtonData.NO);
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, context.getBundle().getString("alertChanges"), buttonYes, buttonNo);
                    alert.setHeaderText(context.getBundle().getString("load"));
                    alert.setTitle(context.getBundle().getString("load"));

                    alert.showAndWait();

                    if (alert.getResult().equals(buttonYes)) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(context.getBundle().getString("image"), "*.png", "*.jpg", "*.jpeg", "*.bmp"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
                        File file = fileChooser.showOpenDialog(new Stage());
                        if (file != null) {
                            mat = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
                            mat.copyTo(clearImage);
                            refreshWithoutClear();
                        }
                    } else if (alert.getResult().equals(buttonNo)) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(context.getBundle().getString("image"), "*.png", "*.jpg", "*.jpeg", "*.bmp"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
                        File file = fileChooser.showOpenDialog(new Stage());
                        if (file != null) {
                            mat = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
                            mat.copyTo(clearImage);
                            deletedActions.clear();
                            actions.clear();
                            updateImage();
                        }
                    }
                } else {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(context.getBundle().getString("image"), "*.png", "*.jpg", "*.jpeg", "*.bmp"));
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
                    File file = fileChooser.showOpenDialog(new Stage());
                    if (file != null) {
//                        try {
//                            BufferedImage bufferedImage = ImageIO.read(file);
//                            byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
//                            int rows = bufferedImage.getWidth();
//                            int cols = bufferedImage.getHeight();
////                            int type = CvType.CV_8UC3;
//                            mat = new Mat(rows,cols, Imgcodecs.IMREAD_COLOR);
//
//                            mat.put(0, 0, pixels);
//                            mat.copyTo(clearImage);
//                            updateImage();
//                        } catch (IOException er) {
//                            er.printStackTrace();
//                        }
//                        file.getpa
                        String path = file.getAbsolutePath();
//                        path = new String()
//                        byte[] ascii = path.getBytes(StandardCharsets.UTF_16LE);
//                        path = new String(ascii, )
//                        try {
//                            path = URLDecoder.decode(path, "UTF-8");
//                        } catch(UnsupportedEncodingException er)
//                        {
//                            er.printStackTrace();
//                        }

                        mat = Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR);
                        mat.copyTo(clearImage);
                        updateImage();
                    }
                }
            }

        });

        menuItemSave.setOnAction(new EventHandler<ActionEvent>()

        {
            @Override
            public void handle(ActionEvent event) {
                // Create the custom dialog.
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle(context.getBundle().getString("save"));
                dialog.setHeaderText(context.getBundle().getString("enterDirectory"));

// Set the button types.
                ButtonType loginButtonType = new ButtonType(context.getBundle().getString("confirm"), ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButtonType = new ButtonType(context.getBundle().getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, cancelButtonType);

// Create the username and password labels and fields.
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField fileName = new TextField();
                fileName.setPromptText(context.getBundle().getString("fileName"));


                grid.add(new Label(context.getBundle().getString("fileName")), 0, 0);
                grid.add(fileName, 1, 0);

                Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                loginButton.setDisable(true);

                fileName.textProperty().addListener((observable, oldValue, newValue) -> {
                    loginButton.setDisable(newValue.trim().isEmpty());
                });

                dialog.getDialogPane().setContent(grid);

                Platform.runLater(() -> fileName.requestFocus());

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == loginButtonType) {
                        return fileName.getText();
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        File file = new File(s);
                        String[] extensions = {"png", "bmp", "jpg", "jpeg", "PNG", "BMP", "JPG", "JPEG"};

                        if (!FilenameUtils.isExtension(file.getAbsolutePath(), extensions)) {
                            file = new File(file.getAbsolutePath() + ".png");
                        }

                        if (file.exists()) {
                            ButtonType buttonYes = new ButtonType(context.getBundle().getString("yes"), ButtonBar.ButtonData.YES);
                            ButtonType buttonNo = new ButtonType(context.getBundle().getString("no"), ButtonBar.ButtonData.NO);
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, context.getBundle().getString("alertSave") + " " + file.getName() + "?", buttonYes, buttonNo);
                            alert.setHeaderText(context.getBundle().getString("save"));
                            alert.setTitle(context.getBundle().getString("save"));

                            alert.showAndWait();

                            if (alert.getResult().equals(buttonYes)) {
                                Imgcodecs.imwrite(file.getAbsolutePath(), mat);
                            }
                        } else {
                            Imgcodecs.imwrite(file.getAbsolutePath(), mat);
                        }
                    }
                });
            }
        });

        for (SimplePlugin plugin : simplePlugins) {

            Button newButton = new Button();
            newButton.setMaxWidth(Double.POSITIVE_INFINITY);
            toolbar.getChildren().add(newButton);
            newButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    actions.add(plugin);
                    plugin.actionOnImage(mat);
                    updateImage();
                }
            });
            pluginButtons.add(newButton);
        }

    }

    private void setTextes() {

        context.setLocale(Locale.ENGLISH);
        context.addContextChangeListener(new ContextChangeListener() {
            @Override
            public void contextChanged() {
                menuPhoto.setText(context.getBundle().getString("photo"));
                menuItemLoad.setText(context.getBundle().getString("load"));
                menuItemSave.setText(context.getBundle().getString("save"));
                buttonClear.setText(context.getBundle().getString("clear"));
                menuFile.setText(context.getBundle().getString("menuFile"));
                menuLanguage.setText(context.getBundle().getString("menuLanguage"));
                menuItemClose.setText(context.getBundle().getString("close"));
                menuItemEnglish.setText(context.getBundle().getString("english"));
                menuItemPolish.setText(context.getBundle().getString("polish"));

                for (Button button : pluginButtons) {
//                    try {
                    button.setText(simplePlugins.get(pluginButtons.indexOf(button)).getButtonText(context.getLocale()));
//                    } catch (MissingResourceException er) {
//                        button.setText(simplePlugins.get(pluginButtons.indexOf(button)).getButtonText());
//                    }
                }

            }
        });
//        context.setLocale(new Locale("PL"));

        if (context.getLocale().equals(Locale.ENGLISH)) {
            menuItemEnglish.setSelected(true);
        } else if(context.getLocale().equals(new Locale("PL")))
        {
            menuItemPolish.setSelected(true);
        }
    }

    private void updateImage() {
//        imageView.setPreserveRatio(true);
//        imageView.setFitWidth(mat.cols());
//        imageView.setFitHeight(mat.rows());
        deletedActions.clear();
        imageView.setImage(util.Util.mat2Image(mat));
    }

    private void updateImageWithoutClear() {
        imageView.setImage(util.Util.mat2Image(mat));
    }

    private void refresh() {
        clearImage.copyTo(mat);
        for (SimplePlugin action : actions) {
            action.actionOnImage(mat);
        }

        updateImage();
    }

    private void refreshWithoutClear() {
        clearImage.copyTo(mat);
        for (SimplePlugin action : actions) {
            action.actionOnImage(mat);
        }
        updateImageWithoutClear();
    }

    private void loadPlugins(String folderName) {
        File loc = new File("plugins");

        File[] flist = loc.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getPath().toLowerCase().endsWith(".jar");
            }
        });
        URL[] urls = new URL[flist.length];
        for (int i = 0; i < flist.length; i++) {
            try {
                urls[i] = flist[i].toURI().toURL();
            } catch (MalformedURLException er) {
                er.printStackTrace();
            }
            System.out.println(urls[i]);
        }
        URLClassLoader ucl = new URLClassLoader(urls);

        ServiceLoader<SimplePlugin> sl = ServiceLoader.load(SimplePlugin.class, ucl);
        Iterator<SimplePlugin> apit = sl.iterator();
        while (apit.hasNext()) {
//            System.out.println(apit.next().getButtonText());
            SimplePlugin plugin = apit.next();
            simplePlugins.add(plugin);
        }
    }
}
