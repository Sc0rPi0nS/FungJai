
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.util.UUID;

public class MySongWindow {

    private HomeWindow home;
    private LibraryService libraryService;

    private TableView<SongRow> table;
    private ObservableList<SongRow> tableData;

public MySongWindow(HomeWindow home, LibraryService libraryService) {
    this.home = home;
    this.libraryService = libraryService;
}

    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);

        tableData = FXCollections.observableArrayList();
        table = new TableView<>(tableData);

        // ===== Columns =====
        TableColumn<SongRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<SongRow, String> artistCol = new TableColumn<>("Artist");
        artistCol.setCellValueFactory(data -> data.getValue().artistProperty());

        TableColumn<SongRow, String> timeCol = new TableColumn<>("Time");
        //timeCol.setCellValueFactory(data -> data.getValue().timeProperty());

        TableColumn<SongRow, String> dateCol = new TableColumn<>("Date Added");
        //dateCol.setCellValueFactory(data -> data.getValue().dateProperty());

        // ===== Button Column =====
        TableColumn<SongRow, Void> actionCol = new TableColumn<>("...");

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button btn = new Button("⋮");

            {
                MenuItem edit = new MenuItem("Edit");
                MenuItem delete = new MenuItem("Delete");

                ContextMenu menu = new ContextMenu(edit, delete);

                btn.setOnAction(e -> {
                    menu.show(btn, javafx.geometry.Side.BOTTOM, 0, 0);
                });

                edit.setOnAction(e -> {
                    SongRow song = getTableView().getItems().get(getIndex());
                });

                delete.setOnAction(e -> {
                    SongRow song = getTableView().getItems().get(getIndex());
                    libraryService.deleteSong(song.getId());
                    tableData.remove(song);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
        // add columns
        table.getColumns().addAll(
                titleCol,
                artistCol,
                timeCol,
                dateCol,
                actionCol
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(tv -> {
            TableRow<SongRow> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && !row.isEmpty()) {

                    SongRow r = row.getItem();

                    // หา Song จริงจาก Library ด้วย id
                    Song song = libraryService.getLibrary()
                            .getMySongs()
                            .stream()
                            .filter(s -> s.getId().equals(r.getId()))
                            .findFirst()
                            .orElse(null);

                    if (song != null) {
                        home.setSongInfo(song);
                    }
                }
            });

            return row;
        });

        for (Song s : libraryService.getLibrary().getMySongs()) {

            tableData.add(new SongRow(
                    s.getId(),
                    s.getTitle(),
                    s.getArtist(),
                    s.getFilePathMp3()
            ));
        }

        // ===== Buttons =====
        Button addBtn = new Button("Add");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");

        addBtn.setOnAction(e -> onAddSong());
        //editBtn.setOnAction(e -> onEditSong());
        //deleteBtn.setOnAction(e -> onDeleteSong());

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn);
        VBox root = new VBox(10, table, buttons);

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("MySong");
        stage.show();
    }

    // ===============================
    // UI → Service
    // ===============================
    private void onAddSong() {
        Stage popup = new Stage();
        popup.setTitle("Add Song");

        TextField titleField = new TextField();
        titleField.setPromptText("Song Title");

        TextField artistField = new TextField();
        artistField.setPromptText("Artist");

        Label fileLabel = new Label("No file selected");

        Button chooseBtn = new Button("Choose File");

        final String[] filePath = new String[1];

        chooseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select MP3 File");

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("MP3 Files", "*.mp3")
            );

            java.io.File file = chooser.showOpenDialog(popup);

            if (file != null) {
                filePath[0] = file.getAbsolutePath();
                fileLabel.setText(file.getName());
            }
        });

        Button saveBtn = new Button("Save");

        saveBtn.setOnAction(e -> {

            String title = titleField.getText();
            String artist = artistField.getText();

            if (!title.isEmpty() && !artist.isEmpty() && filePath[0] != null) {

                Song song = new Song(
                        title,
                        artist,
                        filePath[0],
                        0
                );

                libraryService.addSong(song);

                tableData.add(new SongRow(
                        song.getId(),
                        title,
                        artist,
                        filePath[0]
                ));
                popup.close();
            }
        });

        VBox layout = new VBox(
                10,
                new Label("Title"),
                titleField,
                new Label("Artist"),
                artistField,
                chooseBtn,
                fileLabel,
                saveBtn
        );

        layout.setStyle("-fx-padding: 20");

        popup.setScene(new Scene(layout, 300, 250));
        popup.show();
    }

}
