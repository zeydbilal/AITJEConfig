/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object.attribute;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisFile;
import org.jevis.api.JEVisSample;
import org.jevis.jeconfig.JEConfig;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author fs
 */
public class FileEdior implements AttributeEditor {

    HBox box = new HBox();
    public JEVisAttribute _attribute;
    private boolean _hasChanged = false;
    private Button _downloadButton;
    private Button _uploadButton;

    private final BooleanProperty _changed = new SimpleBooleanProperty(false);

    public FileEdior(JEVisAttribute att) {
        _attribute = att;
    }

    @Override
    public boolean hasChanged() {
//        System.out.println(_attribute.getName() + " changed: " + _hasChanged);
        return _hasChanged;
    }

    @Override
    public BooleanProperty getValueChangedProperty() {
        return _changed;
    }

    @Override
    public void commit() throws JEVisException {
    }

    @Override
    public Node getEditor() {
        try {
            init();
        } catch (Exception ex) {

        }

        return box;
    }

    private void init() throws JEVisException {

        _downloadButton = new Button("Download file", JEConfig.getImage("698925-icon-92-inbox-download-48.png", 18, 18));
        _uploadButton = new Button("Upload new File", JEConfig.getImage("1429894158_698394-icon-130-cloud-upload-48.png", 18, 18));

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        try {
            if (_attribute.hasSample()) {

                JEVisFile lasTFile = _attribute.getLatestSample().getValueAsFile();
                _downloadButton.setText("Download " + lasTFile.getFilename());
            } else {
                _downloadButton.setDisable(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }

        box.setSpacing(10);

        box.getChildren().setAll(_uploadButton, _downloadButton, rightSpacer);

        _uploadButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        //                            new ExtensionFilter("Text Files", "*.txt"),
                        //                            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                        //                            new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                        new ExtensionFilter("All Files", "*.*"));
                File selectedFile = fileChooser.showOpenDialog(JEConfig.getStage());
                if (selectedFile != null) {
                    try {
                        System.out.println("add new file: " + selectedFile);
                        JEVisSample sample = _attribute.buildSample(new DateTime(), Files.readAllBytes(selectedFile.toPath()));

                        JEVisFile jfile = sample.getValueAsFile();
                        System.out.println("set jevis filename: " + selectedFile.getName());
                        jfile.setFilename(selectedFile.getName());
//                        jfile.setFileExtension("txt");

//                        JEVisFile jfile = new JEVisFileSQL(null);
                        sample.commit();

                        try {
                            if (_attribute.hasSample()) {
                                JEVisFile lasTFile = _attribute.getLatestSample().getValueAsFile();
                                _downloadButton.setText("Download " + lasTFile.getFilename());
                            } else {
                                _downloadButton.setDisable(true);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }

//                        mainStage.display(selectedFile);
                    } catch (Exception ex) {
                        Logger.getLogger(FileEdior.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        _downloadButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                try {
                    JEVisFile file = _attribute.getLatestSample().getValueAsFile();

                    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

                    FileChooser fileChooser = new FileChooser();
//                    fileChooser.setInitialFileName(_attribute.getObject().getName() + "_" + fmt.print(_attribute.getLatestSample().getTimestamp()));
                    fileChooser.setInitialFileName(file.getFilename());
                    fileChooser.setTitle("Open Resource File");
                    fileChooser.getExtensionFilters().addAll(
                            //                            new ExtensionFilter("Text Files", "*.txt"),
                            //                            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                            //                            new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                            new ExtensionFilter("All Files", "*.*"));
                    File selectedFile = fileChooser.showSaveDialog(JEConfig.getStage());
                    if (selectedFile != null) {
                        file.saveToFile(selectedFile);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FileEdior.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

}
