/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.tool;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static java.util.Locale.UK;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.TaskProgressView;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.application.dialog.LoginDialog;
import org.jevis.jeconfig.Constants;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author fs
 */
public class LoginGlass extends AnchorPane {

    private final Stage mainStage;
    private final TaskProgressView processView = new TaskProgressView();
    private final Button loginButton = new Button("Login");
    private final Button closeButton = new Button("Close");
    private final TextField userName = new TextField();
    private final CheckBox storeConfig = new CheckBox("Remember me");
    private final PasswordField userPassword = new PasswordField();
    private final GridPane authGrid = new GridPane();
    private final ComboBox<SimpleServerConfig> serverSelection = new ComboBox<SimpleServerConfig>();
    private JEVisDataSource _ds;
    private final Preferences jevisPref = Preferences.userRoot().node("JEVis");
    private final Preferences serverPref = Preferences.userRoot().node("JEVis.Server");
    private final Preferences prefServer1 = Preferences.userRoot().node("JEVis.Server.1");
    private final Preferences prefServer2 = Preferences.userRoot().node("JEVis.Server.2");
    private final Preferences prefServer3 = Preferences.userRoot().node("JEVis.Server.3");
    private List<JEVisObject> rootObjects = new ArrayList<>();
    private List<JEVisClass> classes = new ArrayList<>();
    private List<String> serverKeys = new ArrayList<>();

    private int lastServer = -1;
    private Stage statusDialog = new Stage(StageStyle.TRANSPARENT);

    //workaround, need some coll OO implementaion
    private List<PreloadTask> tasks = new ArrayList<>();

    private SimpleBooleanProperty loginStatus = new SimpleBooleanProperty(false);
    private final String URL_SYNTAX = "user:password@server:port/jevis";

    private List<PopOver> openPopups = new ArrayList<>();
    private final ObservableList<SimpleServerConfig> serverConfigurations = FXCollections.observableList(new ArrayList<>());

    private VBox mainHBox = new VBox();
    private ProgressIndicator progress = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);

    //Workaround replace later, in the moment i have problems showing the xeporion in an thread as an Alart
    private Exception lastExeption = null;

    private LoginGlass() {
        mainStage = null;
    }

    public LoginGlass(Stage stage) {
        super();
        mainStage = stage;
        init();
    }

    public List<JEVisClass> getAllClasses() {
        return classes;
    }

    public List<JEVisObject> getRootObjects() {
        return rootObjects;
    }

    private Alert buildAlarm(Exception ex) {
        System.out.println("Show alert:" + ex);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(ex.getMessage());

        //Does a crash if opend....
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        ex.printStackTrace(pw);
//        String exceptionText = sw.toString();
//
//        Label label = new Label("The exception stacktrace was:");
//
//        TextArea textArea = new TextArea(exceptionText);
//        textArea.setEditable(false);
//        textArea.setWrapText(true);
//
//        textArea.setMaxWidth(Double.MAX_VALUE);
//        textArea.setMaxHeight(Double.MAX_VALUE);
//        GridPane.setVgrow(textArea, Priority.ALWAYS);
//        GridPane.setHgrow(textArea, Priority.ALWAYS);
//
//        GridPane expContent = new GridPane();
//        expContent.setMaxWidth(Double.MAX_VALUE);
//        expContent.add(label, 0, 0);
//        expContent.add(textArea, 0, 1);
// Set expandable Exception into the dialog pane.
//        alert.getDialogPane().setExpandableContent(expContent);
        return alert;

    }

    private void showStatus() {

//        AnchorPane rootPane = new AnchorPane();
//        VBox box = new VBox();
//        box.setStyle("-fx-background-color: transparent;");
//
//        rootPane.setPadding(new Insets(40));
//
//        rootPane.setStyle(
//                "-fx-background-color: white;"
//                + "-fx-effect: dropshadow(gaussian, " + Constants.Color.LIGHT_BLUE2 + ", " + 40 + ", 0, 0, 0);"
//                + "-fx-background-insets: " + 40 + ";"
//        );
//
//        rootPane.getChildren().setAll(processView);
//        AnchorPane.setBottomAnchor(processView, 0.0);
//        AnchorPane.setLeftAnchor(processView, 0.0);
//        AnchorPane.setTopAnchor(processView, 0.0);
//        AnchorPane.setRightAnchor(processView, 0.0);
//
//        Scene scene = new Scene(rootPane, 500, 270);
//
////        mainStage.setScene(scene);
////        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
//        scene.getStylesheets().add("-fx-effect: dropshadow(three-pass-box, derive(cadetblue, -20%), 10, 0, 4, 4); -fx-background-insets: 50;");
////        statusDialog.getScene().getStylesheets().add(getClass().getResource("modal-dialog.css").toExternalForm());
//        statusDialog.setTitle("Loading");
//        scene.setFill(Color.TRANSPARENT);
//        statusDialog.initModality(Modality.WINDOW_MODAL);
//        statusDialog.initOwner(mainStage);
//        statusDialog.setScene(scene);
//        statusDialog.centerOnScreen();
//        statusDialog.setX(userName.localToScreen(userName.getBoundsInLocal()).getMaxX() + 100);
//        statusDialog.setY(userName.localToScreen(userName.getBoundsInLocal()).getMinY() - 30);
//        statusDialog.show();
    }

    private void doLogin() throws JEVisException {

        Task<Void> loginTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Start loginTask");

                    SimpleServerConfig serverConfig = serverSelection.getSelectionModel().getSelectedItem();
                    System.out.println("ServerConfig: " + serverConfig);
                    updateTitle("Login");
                    updateMessage("Connecting to " + serverConfig + "  usinf db-user: " + serverConfig.getUsername());
                    updateProgress(-2, -1);
                    _ds = new JEVisDataSourceSQL(
                            serverConfig.getServer(),
                            serverConfig.getPort(),
                            serverConfig.getSchema(),
                            serverConfig.getUsername(),
                            serverConfig.getPassword());//"openjevis.org", "13306", "jevis", "jevis", "jevistest");

                    updateMessage("Authenticate User " + userName.getText());

                    if (_ds.connect(userName.getText(), userPassword.getText())) {
//                        updateTitle("User is Authenticated  ");
                        updateMessage("User is Authenticated");
//                        super.succeeded();

                        updateMessage("Loading system configuration");
                        classes = _ds.getJEVisClasses();
                        updateMessage("Loading root Objects");
                        rootObjects = _ds.getRootObjects();
                        this.succeeded();
                    }

                } catch (Exception jex) {
                    System.out.println(jex);
                    lastExeption = jex;
                    this.failed();
                    this.cancel();

                }
                return null;
            }
        };

        loginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("loginTask.sucess");
                SimpleServerConfig serverConfig = serverSelection.getSelectionModel().getSelectedItem();
                serverConfig.save();

                if (storeConfig.isSelected()) {
                    storePreference();
                }

                statusDialog.hide();
                loginStatus.setValue(Boolean.TRUE);
            }
        });

        EventHandler<WorkerStateEvent> faildEvent = new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("faild");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        authGrid.setDisable(false);
                        statusDialog.hide();
                        progress.setVisible(false);
                    }
                });
                Alert alart = buildAlarm(lastExeption);
                alart.showAndWait();

            }
        };
        loginTask.setOnCancelled(faildEvent);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progress.setVisible(true);
            }
        });

        new Thread(loginTask).start();
    }

    /**
     * old version
     *
     * @throws JEVisException
     */
    private void doLogin2() throws JEVisException {

        Task<Void> loginTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Start loginTask");

                    SimpleServerConfig serverConfig = serverSelection.getSelectionModel().getSelectedItem();
                    System.out.println("ServerConfig: " + serverConfig);
                    updateTitle("Login");
                    updateMessage("Connecting to " + serverConfig);
                    updateProgress(-2, -1);
                    _ds = new JEVisDataSourceSQL(
                            serverConfig.getServer(),
                            serverConfig.getPort(),
                            serverConfig.getSchema(),
                            serverConfig.getUsername(),
                            serverConfig.getPassword());//"openjevis.org", "13306", "jevis", "jevis", "jevistest");

//                    updateTitle("Authenticate User " + userName.getText());
                    updateMessage("Authenticate User " + userName.getText());

                    if (_ds.connect(userName.getText(), userPassword.getText())) {
//                        updateTitle("User is Authenticated  ");
                        updateMessage("User is Authenticated");
                        super.succeeded();

                    } else {
//                        updateTitle("Error Authentication faild");
                        updateMessage("Error Authentication faild");
                        System.out.println("asdasd");
                        super.failed();
                    }

                } catch (Exception jex) {
                    System.out.println("gggggg");
                    System.out.println(jex);
//                    Alert alart = buildAlarm(jex);
//                    alart.showAndWait();
                    lastExeption = jex;
                    System.out.println("llllllllll");
                    this.failed();
                    this.cancel();

                }
                return null;
            }
        };

//        new Thread(LoginLoader).start();
        Task<Void> loadClassesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("start loadClassesTask");
                    updateProgress(-2, -1);
                    if (_ds != null && _ds.isConnectionAlive()) {
                        updateTitle("Loading system configuration");
                        updateMessage("Loading system configuration");
                        classes = _ds.getJEVisClasses();
                        super.succeeded();
                    }
                } catch (Exception ex) {
                    lastExeption = ex;
//                    Alert alart = buildAlarm(ex);
//                    alart.showAndWait();
                    this.cancel();
                }
                return null;
            }

        };

        Task<Void> loadRoot = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Start loadRoot");
                try {
                    if (_ds != null && _ds.isConnectionAlive()) {
                        updateTitle("Loading root Objects");
                        updateMessage("Loading root Objects");
                        rootObjects = _ds.getRootObjects();
                        super.succeeded();
                    }
                } catch (Exception ex) {
                    lastExeption = ex;
//                    Alert alart = buildAlarm(ex);
//                    alart.showAndWait();
                    super.failed();
                }

                return null;
            }

        };

        loginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("loginTask.sucess");
                new Thread(loadClassesTask).start();
            }
        });

        loadClassesTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("loadClassesTask.sucess");
                new Thread(loadRoot).start();
            }
        });

        processView.getTasks().setAll(loginTask, loadClassesTask, loadRoot);

        loadRoot.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                SimpleServerConfig serverConfig = serverSelection.getSelectionModel().getSelectedItem();
                serverConfig.save();
                statusDialog.hide();
                loginStatus.setValue(Boolean.TRUE);

            }
        });

        EventHandler<WorkerStateEvent> faildEvent = new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("Faildexent");
//                loadRoot.cancel();
//                loginTask.cancel();
//                loadClassesTask.cancel();

                authGrid.setDisable(false);
                statusDialog.hide();

                Alert alart = buildAlarm(lastExeption);
                alart.showAndWait();

            }
        };
//        loadRoot.setOnFailed(faildEvent);
        loginTask.setOnFailed(faildEvent);
//        loadClassesTask.setOnFailed(faildEvent);

        showStatus();
        new Thread(loginTask).start();
    }

    public JEVisDataSource getDataSource() {
        return _ds;
    }

    private Node buildHeader() {
        AnchorPane header = new AnchorPane();
        header.setStyle("-fx-background-color: " + Constants.Color.LIGHT_BLUE);

        ImageView logo = new ImageView(new Image("/icons/openjevis_longlogo.png"));

        header.getChildren().add(logo);
        AnchorPane.setBottomAnchor(logo, 0.0);
        AnchorPane.setLeftAnchor(logo, 0.0);

        return header;
    }

    private Node buildFooter() {
        AnchorPane footer = new AnchorPane();
        footer.setStyle("-fx-background-color: " + Constants.Color.LIGHT_BLUE);

        Node buildInfo = buldBuildInfos();
        AnchorPane.setBottomAnchor(buildInfo, 5.0);
        AnchorPane.setRightAnchor(buildInfo, 5.0);
        footer.getChildren().add(buildInfo);

        return footer;
    }

    private Node buidButtonsbar() {
        Region spacer = new Region();
        spacer.setStyle("-fx-background-color: transparent;");
        HBox buttonBox = new HBox(10);
        Node link = buildLink("http://openjevis.org/account/register");
        buttonBox.getChildren().setAll(link, spacer, loginButton, closeButton);
        HBox.setHgrow(link, Priority.NEVER);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(loginButton, Priority.NEVER);
        return buttonBox;
    }

    private Node buildAuthForm() {

        Node buttonBox = buidButtonsbar();
        Node serverConfigBox = buildServerSelection();
        ComboBox langSelect = buildLanguageBox();
        loadPreference(true, "");

        Label userL = new Label("Username:");
        Label passwordL = new Label("Password:");
        Label serverL = new Label("Server:");
        Label languageL = new Label("Language: ");

        loginButton.setDefaultButton(true);
        closeButton.setCancelButton(true);

        userName.requestFocus();

        loginButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Login!");
                try {
                    doLogin();

                } catch (JEVisException ex) {
                    Logger.getLogger(LoginGlass.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        authGrid.setHgap(10);
        authGrid.setVgap(5);
        authGrid.setPadding(new Insets(10, 10, 10, 10));

        int columns = 2;
        //x,x...
        int row = 0;
        authGrid.add(userL, 0, row);
        authGrid.add(userName, 1, row);

        row++;
        authGrid.add(passwordL, 0, row);
        authGrid.add(userPassword, 1, row);

        row++;
        authGrid.add(languageL, 0, row);
        authGrid.add(langSelect, 1, row);

        row++;
        authGrid.add(serverL, 0, row);
        authGrid.add(serverConfigBox, 1, row);

        row++;
//        grid.add(serverL, 0, row);
        authGrid.add(storeConfig, 1, row);

        Region cTobSpacer = new Region();
        cTobSpacer.setStyle("-fx-background-color: transparent;");
        cTobSpacer.setPrefHeight(30);

        row++;
        authGrid.add(cTobSpacer, 0, row, columns, 1);

        row++;
        authGrid.add(buttonBox, 0, row, columns, 1);

        return authGrid;
    }

    private void init() {
        loadPreference(true, URL_SYNTAX);

        ImageView logo = new ImageView(new Image("/icons/openjevislogo_simple2.png"));
        logo.setPreserveRatio(true);

        AnchorPane leftSpacer = new AnchorPane();
        AnchorPane rightSpacer = new AnchorPane();
//
//        processView.setPrefSize(300, 350);
//        processView.setStyle("-fx-background-color:transparent;");
//        AnchorPane.setTopAnchor(processView, 10.0);
//        AnchorPane.setLeftAnchor(processView, 200.0);
//        rightSpacer.getChildren().add(processView);

        progress.setPrefSize(80, 80);
        progress.setVisible(false);
        AnchorPane.setTopAnchor(progress, 70d);
        AnchorPane.setLeftAnchor(progress, 100d);
        rightSpacer.getChildren().setAll(progress);

        leftSpacer.setMinWidth(200);//todo 20%

        Node authForm = buildAuthForm();

        HBox body = new HBox();
        body.getChildren().setAll(leftSpacer, authForm, rightSpacer);
        HBox.setHgrow(authForm, Priority.NEVER);
        HBox.setHgrow(leftSpacer, Priority.NEVER);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
//        loginLevel.setStyle("-fx-effect: dropshadow(three-pass-box, white, 20, 0, 0, 0);-fx-background-insets: 20;");

        Node header = buildHeader();
        Node footer = buildFooter();

        mainHBox = new VBox();
        mainHBox.getChildren().setAll(header, body, footer);
        VBox.setVgrow(body, Priority.NEVER);
        VBox.setVgrow(header, Priority.ALWAYS);
        VBox.setVgrow(footer, Priority.ALWAYS);

        body.setStyle("-fx-background-color: white;");
        leftSpacer.setStyle("-fx-background-color: white;");
        rightSpacer.setStyle("-fx-background-color: white;");

        setStyle("-fx-background-color: red;");
        mainHBox.setStyle("-fx-background-color: yellow;");
        AnchorPane.setTopAnchor(mainHBox, 0.0);
        AnchorPane.setRightAnchor(mainHBox, 0.0);
        AnchorPane.setLeftAnchor(mainHBox, 0.0);
        AnchorPane.setBottomAnchor(mainHBox, 0.0);

        getChildren().setAll(mainHBox);

    }

    private Node buildServerSelection() {
        VBox root = new VBox(10);
        Label titel = new Label("Server Configuration");
        titel.setStyle("-fx-font-weight: bold;");

        Label nameLabel = new Label("Name:");
        TextField nameF = new TextField();
        Label urlLabel = new Label("Server:");
        TextField urlF = new TextField();
        Label portLabel = new Label("Port:");
        TextField portF = new TextField();
        Label schema = new Label("Schema:");
        TextField schemaF = new TextField();
        Label userL = new Label("Username:");
        TextField userF = new TextField();
        Label passL = new Label("Password:");
        TextField passF = new TextField();

//        capsError.applyRequiredDecoration(passF);
//        ValidationSupport support = new ValidationSupport();
//        Validator<String> portValidator = new Validator<String>() {
//            @Override
//            public ValidationResult apply(Control control, String value) {
//                boolean condition
//                        = value != null
//                                ? !value
//                                .matches(
//                                        "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*")
//                                : value == null;
//
//                System.out.println(condition);
//
//                return ValidationResult.fromMessageIf(control, "not a number", Severity.ERROR, condition);
//            }
//        };
//        support.registerValidator(portF, true, portValidator);
        Button ok = new Button("Save");
        ok.setDefaultButton(true);
        Button addNewButton = new Button("Save as new");
        ok.setDefaultButton(true);
        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);
        Region spacer = new Region();

        HBox buttons = new HBox(8);
        buttons.getChildren().setAll(spacer, ok, addNewButton, cancel);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        int columns = 2;
        //x,x...
        int row = 0;
        grid.add(nameLabel, 0, row);
        grid.add(nameF, 1, row);
        row++;
        grid.add(urlLabel, 0, row);
        grid.add(urlF, 1, row);
        row++;
        grid.add(portLabel, 0, row);
        grid.add(portF, 1, row);
        row++;
        grid.add(schema, 0, row);
        grid.add(schemaF, 1, row);
        row++;
        grid.add(userL, 0, row);
        grid.add(userF, 1, row);
        row++;
        grid.add(passL, 0, row);
        grid.add(passF, 1, row);

        VBox.setMargin(titel, new Insets(10, 30, 10, 30));
        Region bottomSpacer = new Region();
        bottomSpacer.setPrefHeight(10);

        root.getChildren().setAll(titel, grid, buttons, bottomSpacer);
        root.setPadding(new Insets(10));

        Button configureServer = new Button("", JEConfig.getImage("Service Manager.png", 16, 16));

        PopOver serverConfigPop = new PopOver(root);
        serverConfigPop.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        serverConfigPop.setDetachable(true);
        serverConfigPop.setHideOnEscape(true);
        serverConfigPop.setAutoFix(true);

        if (serverConfigurations.isEmpty()) {
            //@AITBilal - Logindaten für AIT
            SimpleServerConfig jevisAIT = new SimpleServerConfig(1, "JevisAIT", "localhost", "3306", "jevis", "jevis", "jevis");
            serverConfigurations.add(jevisAIT);
            SimpleServerConfig openJEvisOrg = new SimpleServerConfig(1, "OpenJEVis.org", "openjevis.org", "13306", "jevis", "jevis", "jevistest");
            serverConfigurations.add(openJEvisOrg);
            SimpleServerConfig Coffee = new SimpleServerConfig(2, "COFFEE Project", "coffee-project.eu", "13306", "jevis", "jevis", "jevistest");
            serverConfigurations.add(Coffee);
            SimpleServerConfig localhost = new SimpleServerConfig(2, "Localhost", "127.0.0.1", "3306", "jevis", "jevis", "jevistest");
            serverConfigurations.add(localhost);
        }

        Callback<ListView<SimpleServerConfig>, ListCell<SimpleServerConfig>> cellFactory = new SimpleServerConfig().buildCellRenderer();
        serverSelection.valueProperty().addListener(new ChangeListener<SimpleServerConfig>() {

            @Override
            public void changed(ObservableValue<? extends SimpleServerConfig> observable, SimpleServerConfig oldValue, SimpleServerConfig newValue) {
                System.out.println("Server changed: " + newValue.getName());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        nameF.setText(newValue.getName());
                        urlF.setText(newValue.getServer());
                        portF.setText(newValue.getPort());
                        schemaF.setText(newValue.getSchema());
                        userF.setText(newValue.getUsername());
                        passF.setText(newValue.getPassword());

//                        System.out.println("load username/pw: " + newValue.getJevisUser() + "/" + newValue.getJevisPassword());
//                        userName.setText(newValue.getJevisUser());
//                        userPassword.setText(newValue.getJevisPassword());
                    }
                });

            }
        });
        serverSelection.setCellFactory(cellFactory);
        serverSelection.setButtonCell(cellFactory.call(null));
        System.out.println("Server count: " + serverConfigurations.size());
        serverSelection.setItems(serverConfigurations);
        serverSelection.getSelectionModel().selectFirst();

//        if (lastServer > 0) {
//            serverSelection.getSelectionModel().select(serverConfigurations.get(lastServer - 1));//offest server start at 1 arry at 0
//        } else {
//            System.out.println("--------- no server create new defautl");
//            SimpleServerConfig defaultServer = new SimpleServerConfig(1, "OpenJEVis.org", "coffee-project.eu", "13306", "jevis", "jevis", "jevistest");
//            serverConfigurations.add(defaultServer);
//            serverSelection.getSelectionModel().select(defaultServer);
//        }
        HBox serverConfBox = new HBox(10);
        serverConfBox.getChildren().setAll(serverSelection, configureServer);
        configureServer.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        SimpleServerConfig newValue = serverSelection.getSelectionModel().getSelectedItem();
                        nameF.setText(newValue.getName());
                        urlF.setText(newValue.getServer());
                        portF.setText(newValue.getPort());
                        schemaF.setText(newValue.getSchema());
                        userF.setText(newValue.getUsername());
                        passF.setText(newValue.getPassword());
                    }
                });

                if (!serverConfigPop.isShowing()) {
                    serverConfigPop.show(configureServer);
                } else {
                    serverConfigPop.hide(Duration.seconds(0.3));
                }

            }
        }
        );
        HBox.setHgrow(configureServer, Priority.NEVER);
        HBox.setHgrow(serverSelection, Priority.ALWAYS);

        ok.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                serverConfigPop.hide(Duration.seconds(1));
                SimpleServerConfig newValue = serverSelection.getSelectionModel().getSelectedItem();
                newValue.setName(nameF.getText());
                newValue.setServer(urlF.getText());
                newValue.setPort(portF.getText());
                newValue.setSchema(schemaF.getText());
                newValue.setUsername(userF.getText());
                newValue.setPassword(passF.getText());
                newValue.save();

            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                serverConfigPop.hide(Duration.seconds(1));
            }
        });

        addNewButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                int newkey = 1;
                if (!serverKeys.isEmpty()) {
                    int lastID = Integer.parseInt(serverKeys.get(serverKeys.size() - 1));
                    newkey = lastID + 1;
                }

                SimpleServerConfig newConfig = new SimpleServerConfig(newkey,
                        nameF.getText(), urlF.getText(), portF.getText(), schemaF.getText(), userF.getText(), passF.getText());
                newConfig.save();
                serverConfigurations.add(newConfig);
                serverSelection.getSelectionModel().select(newConfig);

            }
        });

        return serverConfBox;
    }

    private void loadPreference(boolean showServer, String defaultServer) {
        System.out.println("load from disk");
        if (!jevisPref.get("JEVisUser", "").isEmpty()) {
            storeConfig.setSelected(true);
            System.out.println("username: " + jevisPref.get("JEVisUser", ""));
            userName.setText(jevisPref.get("JEVisUser", ""));
            userPassword.setText(jevisPref.get("JEVisPW", ""));
        } else {
            storeConfig.setSelected(false);
        }
    }

    private ComboBox buildLanguageBox() {
        List<Locale> availableLang = new ArrayList<>();
        availableLang.add(UK);

        Callback<ListView<Locale>, ListCell<Locale>> cellFactory = new Callback<ListView<Locale>, ListCell<Locale>>() {
            @Override
            public ListCell<Locale> call(ListView<Locale> param) {
                final ListCell<Locale> cell = new ListCell<Locale>() {
                    {
                        super.setPrefWidth(260);
                    }

                    @Override
                    public void updateItem(Locale item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {

                            HBox box = new HBox(5);
                            box.setAlignment(Pos.CENTER_LEFT);

//                            if (item.getLanguage().equals(US.getLanguage())) {
//
//                            } else if (item.getLanguage().equals(GERMAN.getLanguage())) {
//
//                            }
                            Image img = new Image("/icons/" + item.getLanguage() + ".png");
                            ImageView iv = new ImageView(img);
                            iv.fitHeightProperty().setValue(20);
                            iv.fitWidthProperty().setValue(20);
                            iv.setSmooth(true);

                            Label name = new Label(item.getDisplayLanguage());
                            name.setTextFill(Color.BLACK);

                            box.getChildren().setAll(iv, name);
                            setGraphic(box);

                        }
                    }
                };
                return cell;
            }
        };

        ObservableList<Locale> options = FXCollections.observableArrayList(availableLang);

        final ComboBox<Locale> comboBox = new ComboBox<Locale>(options);
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));

        //TODO: load default lange from Configfile or so
        comboBox.getSelectionModel().select(UK);//Default

        comboBox.setMinWidth(250);
        comboBox.setMaxWidth(Integer.MAX_VALUE);//workaround

        return comboBox;

    }

    private Node buildLink(String url) {
        Hyperlink link = new Hyperlink();
        link.setVisited(true);
        link.setText("Register");
        link.setVisited(true);
        link.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    URI uri = new URI(url);
                    if (Desktop.isDesktopSupported()) {
                        System.out.println("Desktop is supportet");
                        Desktop.getDesktop().browse(uri);
                    } else {
                        //TODO: maybe disable send button if not suppotret at all
                        System.out.println("Desktop is not Supportet");
                        if (System.getProperty("os.name").equals("Linux")) {
                            System.out.println("is limux using xdg-open");
                            Runtime.getRuntime().exec("xdg-open " + uri);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("ex: " + ex);
                    Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return link;
    }

    private Node buildTask(PreloadTask task) {
        HBox box = new HBox(20);
        ProgressIndicator process = new ProgressIndicator();
        process.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Label title = new Label(task.getName());
        box.getChildren().setAll(title, process);
        box.setVisible(false);
        return box;

    }

    public Node buldBuildInfos() {
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: transparent;");
        Label name = new Label(JEConfig.PROGRAMM_INFO.getName());
        Label version = new Label("Version: " + JEConfig.PROGRAMM_INFO.getVersion());
        Label coypLeft = new Label("©Envidatec GmbH 2014-2015");
        Label java = new Label("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));

        name.setTextFill(Color.WHITE);
        version.setTextFill(Color.WHITE);
        coypLeft.setTextFill(Color.WHITE);
        java.setTextFill(Color.WHITE);

        vbox.getChildren().setAll(name, version, coypLeft, java);
        return vbox;
    }

    public SimpleBooleanProperty getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(SimpleBooleanProperty loginStatus) {
        this.loginStatus = loginStatus;
    }

    private void storePreference() {
        System.out.println("save to disk");
        final String jevisUser = userName.getText();
        final String jevisPW = userPassword.getText();

        jevisPref.put("JEVisUser", jevisUser);
        jevisPref.put("JEVisPW", jevisPW);
        try {
            jevisPref.sync();
        } catch (BackingStoreException ex) {
            Logger.getLogger(LoginGlass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadPreference2(boolean showServer, String defaultServer) {
//
        //loading the server from config does not work yet, so we have some default server
//
//        SimpleServerConfig Coffee = new SimpleServerConfig(2, "COFFEE Project", "coffee-project.eu", "13306", "jevis", "jevis", "jevistest");
//        serverConfigurations.add(Coffee);

//        String serverInuse = serverPref.get("InUse", "");
//        String[] servers = serverInuse.split(",");
//        for (String serverID : servers) {
//            try {
//                Preferences pref = Preferences.userRoot().node("JEVis.Server." + serverID);
//                SimpleServerConfig simpleServerConfig = new SimpleServerConfig(pref, serverID);
//            } catch (Exception ex) {
//
//            }
//        }
//        if (lastServer > 0) {
////            serverSelection.getSelectionModel().select(serverConfigurations.get(lastServer - 1));//offest server start at 1 arry at 0
//        } else {
//            System.out.println("--------- no server create new defautl");
//            SimpleServerConfig newDefaultServer = new SimpleServerConfig(1, "OpenJEVis.org", "coffee-project.eu", "13306", "jevis", "jevis", "jevistest");
//            serverConfigurations.add(newDefaultServer);
////            serverSelection.getSelectionModel().select(newDefaultServer);
//        }
//        Collections.sort(serverKeys);
//        lastServer = serverPref.getInt("Last Used", 0);//-1 is now selected
//        System.out.println("Last Server: " + lastServer);
//        SimpleServerConfig simpleServerConfig1 = new SimpleServerConfig(prefServer1);
//        SimpleServerConfig simpleServerConfi2 = new SimpleServerConfig(prefServer2);
//        SimpleServerConfig simpleServerConfig3 = new SimpleServerConfig(prefServer3);
    }

    private class SimpleServerConfig {

        private String name = "";
        private String server = "";
        private String port = "";
        private String schema = "";
        private String username = "";
        private String password = "";
        private String jevisUser = "";
        private String jevisPassword = "";
        private Preferences prefSorage;
        private int serverKey = -1;

        public SimpleServerConfig() {
        }

        public SimpleServerConfig(int pos, String name, String server, String port, String schema, String username, String password) {
//            prefSorage = Preferences.userRoot().node("JEVis.Server." + pos);
            this.name = name;
            this.server = server;
            this.port = port;
            this.schema = schema;
            this.username = username;
            this.password = password;
        }

        private SimpleServerConfig(Preferences pref, String key) {
//            String[] parts= urlString.split("(https?://)([^:^/]*)(:\\d*)?(.*)?");

            if (!prefServer1.get("Name", "").isEmpty()) {
                serverConfigurations.add(this);
                serverKeys.add(key);
            }
            this.prefSorage = pref;

            this.name = pref.get("Name", "");
            this.server = pref.get("Server", "");
            this.port = pref.get("Port", "");
            this.schema = pref.get("Schema", "");
            this.username = pref.get("Username", "");
            this.password = pref.get("password", "");
            this.jevisUser = pref.get("jevisuser", "");
            this.jevisPassword = pref.get("jevispw", "");

        }

        private void save() {
//            System.out.println(toString() + "      !! save");
//            prefSorage.put("Name", name);
//            prefSorage.put("Server", server);
//            prefSorage.put("Port", port);
//            prefSorage.put("Schema", schema);
//            prefSorage.put("Username", username);
//            prefSorage.put("password", password);
//
//            if (storeConfig.isSelected()) {
//                System.out.println("also store user");
//                prefSorage.put("jevisuser", jevisUser);
//                prefSorage.put("jevisPassword", jevisPassword);
//            }
//
//            if (serverKeys.contains(serverKey)) {
//
//            } else {
//                serverKeys.add("" + serverKey);
//                String tmp = serverPref.get("InUse", "");
//                tmp += "," + serverKey;
//                serverPref.put("InUse", tmp);
//            }

        }

        public String getJevisUser() {
            return jevisUser;
        }

        public void setJevisUser(String jevisUser) {
            this.jevisUser = jevisUser;
        }

        public String getJevisPassword() {
            return jevisPassword;
        }

        public void setJevisPassword(String jevisPassword) {
            this.jevisPassword = jevisPassword;
        }

        public String toURL() {
            return username + ":" + password + "@" + server + ":" + port + "/" + schema;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "SimpleServerConfig{" + "name=" + name + ", server=" + server + ", port=" + port + ", schema=" + schema + ", username=" + username + ", password=" + password + ", jevisUser=" + jevisUser + ", jevisPassword=" + jevisPassword + '}';
        }

        public Callback<ListView<SimpleServerConfig>, ListCell<SimpleServerConfig>> buildCellRenderer() {
            Callback<ListView<SimpleServerConfig>, ListCell<SimpleServerConfig>> cellFactory = new Callback<ListView<SimpleServerConfig>, ListCell<SimpleServerConfig>>() {
                @Override
                public ListCell<SimpleServerConfig> call(ListView<SimpleServerConfig> param) {
                    final ListCell<SimpleServerConfig> cell = new ListCell<SimpleServerConfig>() {
                        {
                            super.setPrefWidth(260);
                        }

                        @Override
                        public void updateItem(SimpleServerConfig item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null && !empty) {
//                                Label name = new Label(item.getName());

//                                setGraphic(name);
                                setText(item.getName());
                            } else {
                                setGraphic(null);
                                setText(null);
                            }

                        }
                    };
                    return cell;
                }
            };

            return cellFactory;
        }

    }

}
