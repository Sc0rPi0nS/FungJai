import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.UUID;

public class MySongWindow extends Application {

    private LibraryService libraryService;

    private TableView<SongRow> table;
    private ObservableList<SongRow> tableData;

    @Override
    public void start(Stage stage) {

        libraryService = new LibraryService();

        tableData = FXCollections.observableArrayList();
        table = new TableView<>(tableData);

        // ===== Columns =====
        TableColumn<SongRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<SongRow, String> artistCol = new TableColumn<>("Artist");
        artistCol.setCellValueFactory(data -> data.getValue().artistProperty());

        table.getColumns().addAll(titleCol, artistCol);

        // ===== Buttons =====
        Button addBtn = new Button("Add");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");

        addBtn.setOnAction(e -> onAddSong());
        editBtn.setOnAction(e -> onEditSong());
        deleteBtn.setOnAction(e -> onDeleteSong());

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn);
        VBox root = new VBox(10, table, buttons);

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("MySong");
        stage.show();

        refreshTable();
    }

    // ===============================
    // UI → Service
    // ===============================

    private void refreshTable() {
        tableData.clear();

        for (Song song : libraryService.getAllSongs()) {
            tableData.add(new SongRow(
                    song.getId(),
                    song.getTitle(),
                    song.getArtist()
            ));
        }
    }

    private void onAddSong() {

        // ตัวอย่างง่าย ๆ (ปกติควรมี dialog รับข้อมูล)
        libraryService.importMp4(
                "C:/music/test.mp4",
                "New Song",
                "Unknown Artist",
                180
        );

        refreshTable();
    }

    private void onEditSong() {

        SongRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        UUID id = selected.getId();

        libraryService.editSong(
                id,
                "Edited Title",
                "Edited Artist"
        );

        refreshTable();
    }

    private void onDeleteSong() {

        SongRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        libraryService.deleteSong(selected.getId());

        refreshTable();
    }

    public static void main(String[] args) {
        launch(args);
    }
}