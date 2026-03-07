
import java.io.File;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MySongWindow {

    private HomeWindow home;
    private LibraryService libraryService;
    private String date;

    private TableView<SongRow> table;
    private ObservableList<SongRow> tableData;

    public MySongWindow(HomeWindow home, LibraryService libraryService) {
        this.home = home;
        this.libraryService = libraryService;
    }

    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        LocalDate today = LocalDate.now();

        DateTimeFormatter format
                = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        date = today.format(format);

        tableData = FXCollections.observableArrayList();
        table = new TableView<>(tableData);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<SongRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<SongRow, String> artistCol = new TableColumn<>("Artist");
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));

        TableColumn<SongRow, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<SongRow, String> dateCol = new TableColumn<>("Date Added");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));

// และอย่าลืมแอดคอลัมน์ลง table ด้วย
        // ===== Button Column =====
        TableColumn<SongRow, Void> actionCol = new TableColumn<>("...");
table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

titleCol.setPrefWidth(350);
artistCol.setPrefWidth(150);
timeCol.setPrefWidth(80);
dateCol.setPrefWidth(70);
actionCol.setPrefWidth(50);

titleCol.setResizable(false);
artistCol.setResizable(false);
timeCol.setResizable(false);
dateCol.setResizable(false);
actionCol.setResizable(false);

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

            String time = formatTime(s);

            System.out.println("TIME = " + time);
            System.out.println("DATE = " + date);

            tableData.add(new SongRow(
                    s.getId(),
                    s.getTitle(),
                    s.getArtist(),
                    time,
                    date,
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
        

        System.out.println("show() called");
        System.out.println("songs size = " + libraryService.getLibrary().getMySongs().size());
        stage.setScene(new Scene(root, 700, 400));
        stage.setResizable(false);
        stage.setTitle("MySong");
        stage.show();
    }

    // ===============================
    // UI → Service
    // ===============================
    private String formatTime(Song song) {

        int sec = song.getDurationSrc();

        int min = sec / 60;
        int s = sec % 60;

        return String.format("%d:%02d", min, s);
    }

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

            File file = chooser.showOpenDialog(popup);

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

                Media media = new Media(new File(filePath[0]).toURI().toString());
                MediaPlayer player = new MediaPlayer(media);

                player.setOnReady(() -> {

                    int duration = (int) player.getTotalDuration().toSeconds();

                    Song song = new Song(
                            title,
                            artist,
                            filePath[0],
                            duration
                    );

                    libraryService.addSong(song);

                    tableData.add(new SongRow(
                            song.getId(),
                            title,
                            artist,
                            formatTime(song),
                            date,
                            filePath[0]
                    ));

                    popup.close();
                });
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
