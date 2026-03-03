import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.UUID;

public class MySongWindow extends Application {

    private LibraryService libraryService;   // ใช้ของจริง

    private TableView<SongRow> table;
    private ObservableList<SongRow> tableData;

    @Override
    public void start(Stage primaryStage) {

        libraryService = new LibraryService(); // เพื่อน implement

        tableData = FXCollections.observableArrayList();

        table = new TableView<>();
        table.setItems(tableData);

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

        addBtn.setOnAction(e -> onAddSongClicked());
        editBtn.setOnAction(e -> {
            SongRow selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                onEditSongSelected(selected.getId());
            }
        });

        deleteBtn.setOnAction(e -> {
            SongRow selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                onDeleteSongSelected(selected.getId());
            }
        });

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn);
        VBox layout = new VBox(10, table, buttons);

        primaryStage.setScene(new Scene(layout, 600, 400));
        primaryStage.setTitle("MySong");
        primaryStage.show();

        refreshTable(); // โหลดข้อมูลตอนเปิด
    }

    // ===============================
    // UI → Service
    // ===============================

    private void refreshTable() {
        tableData.clear();

        List<Song> songs = libraryService.getAllSongs();

        for (Song song : songs) {
            tableData.add(new SongRow(
                    song.getId(),
                    song.getTitle(),
                    song.getArtist()
            ));
        }
    }

    public void onAddSongClicked() {
        libraryService.addSong("New Song", "Unknown");
        refreshTable();
    }

    public void onEditSongSelected(UUID songID) {
        libraryService.editSong(songID, "Edited Title", "Edited Artist");
        refreshTable();
    }

    public void onDeleteSongSelected(UUID songID) {
        libraryService.deleteSong(songID);
        refreshTable();
    }

    public static void main(String[] args) {
        launch(args);
    }
}