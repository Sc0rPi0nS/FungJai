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
        timeCol.setCellValueFactory(data -> data.getValue().timeProperty());

        TableColumn<SongRow, String> dateCol = new TableColumn<>("Date Added");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());

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
                    SongRow row = getTableRow().getItem();
                    if (row != null) {
                        onEditSong(row);
                    }
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
                setGraphic(empty ? null : btn);
            }
        });

        // add columns
        table.getColumns().addAll(titleCol, artistCol, timeCol, dateCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(tv -> {
            TableRow<SongRow> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    SongRow r = row.getItem();

                    Song song = libraryService.getLibrary()
                            .getMySongs()
                            .stream()
                            .filter(s -> s.getId().equals(r.getId()))
                            .findFirst()
                            .orElse(null);

                    if (song != null) {
                        home.getPlayerService().playLibrary(
                                libraryService.getLibrary().getMySongs(),
                                song
                        );
                        home.setSongInfo(song, null);
                    }
                }
            });

            return row;
        });

        // ===== Load songs — ใช้ duration จริงจาก Song ที่ save ไว้แล้ว =====
        for (Song s : libraryService.getLibrary().getMySongs()) {
            SongRow row = new SongRow(
                    s.getId(),
                    s.getTitle(),
                    s.getArtist(),
                    s.getFilePathMp3(),
                    s.getDuration(),           // ใช้ค่าที่ save ไว้ ถ้า > 0 แสดงทันที
                    s.getDateAddedString()
            );
            tableData.add(row);

            // ถ้า duration ยังเป็น 0 (เพลงเก่าที่ยังไม่มีข้อมูล) → โหลดจาก MediaPlayer แล้ว save
            if (s.getDuration() <= 0) {
                javafx.scene.media.MediaPlayer player = s.getMediaPlayer();
                player.setOnReady(() -> {
                    javafx.util.Duration d = player.getMedia().getDuration();
                    if (d != null && !d.isUnknown() && !d.isIndefinite()) {
                        long secs = (long) d.toSeconds();
                        long min = secs / 60;
                        long sec = secs % 60;
                        s.setDuration(secs);
                        libraryService.forceSave();
                        javafx.application.Platform.runLater(() ->
                            row.timeProperty().set(min + ":" + String.format("%02d", sec))
                        );
                    }
                });
            }
        }

        // ===== Buttons =====
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> onAddSong());

        HBox buttons = new HBox(10, addBtn);
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

        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.contains(" ")) titleField.setText(newVal.replace(" ", "-"));
        });

        artistField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.contains(" ")) artistField.setText(newVal.replace(" ", "-"));
        });

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

                Song song = new Song(title, artist, filePath[0], 0);
                libraryService.addSong(song);

                // สร้าง SongRow แสดง "..." ก่อน
                SongRow row = new SongRow(
                        song.getId(),
                        title,
                        artist,
                        filePath[0],
                        0,
                        song.getDateAddedString()
                );
                tableData.add(row);

                // ดึง duration จาก MediaPlayer ของ Song ตัวจริง ไม่สร้าง player ซ้ำ
                javafx.scene.media.MediaPlayer player = song.getMediaPlayer();
                player.setOnReady(() -> {
                    javafx.util.Duration d = player.getMedia().getDuration();
                    if (d != null && !d.isUnknown() && !d.isIndefinite()) {
                        long secs = (long) d.toSeconds();
                        long min = secs / 60;
                        long sec = secs % 60;
                        // บันทึก duration กลับเข้า Song แล้ว save ลง library ทันที
                        song.setDuration(secs);
                        libraryService.forceSave();
                        javafx.application.Platform.runLater(() ->
                            row.timeProperty().set(min + ":" + String.format("%02d", sec))
                        );
                    }
                });

                popup.close();
            }
        });

        VBox layout = new VBox(
                10,
                new Label("Title"), titleField,
                new Label("Artist"), artistField,
                chooseBtn, fileLabel,
                saveBtn
        );
        layout.setStyle("-fx-padding: 20");
        popup.setScene(new Scene(layout, 300, 250));
        popup.show();
    }

    private void onEditSong(SongRow row) {

        Song song = libraryService.getLibrary()
                .getMySongs()
                .stream()
                .filter(s -> s.getId().equals(row.getId()))
                .findFirst()
                .orElse(null);

        if (song == null) return;

        Stage popup = new Stage();
        popup.setTitle("Edit Song");

        TextField titleField = new TextField(song.getTitle());
        TextField artistField = new TextField(song.getArtist());

        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.contains(" ")) titleField.setText(newVal.replace(" ", "-"));
        });
        artistField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.contains(" ")) artistField.setText(newVal.replace(" ", "-"));
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            String newTitle = titleField.getText();
            String newArtist = artistField.getText();
            if (!newTitle.isEmpty() && !newArtist.isEmpty()) {
                libraryService.updateSong(song.getId(), newTitle, newArtist);
                row.setTitle(newTitle);
                row.setArtist(newArtist);
                table.refresh();
                popup.close();
            }
        });

        VBox layout = new VBox(10,
                new Label("Title"), titleField,
                new Label("Artist"), artistField,
                saveBtn
        );
        layout.setStyle("-fx-padding:20;");
        popup.setScene(new Scene(layout, 300, 200));
        popup.show();
    }
}