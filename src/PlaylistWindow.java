import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaylistWindow extends Application {

    private static final String BG     = "#f5f5f5";
    private static final String PANEL  = "#ffffff";
    private static final String BORDER = "#e0e0e0";
    private static final String TEXT   = "#333333";
    private static final String MUTED  = "#888888";

    private static final String C_BODY  = "#2b3a4a";
    private static final String C_WIN   = "#1a252f";
    private static final String C_REEL  = "#8a9ba8";
    private static final String C_HUB   = "#1a252f";
    private static final String C_TAPE  = "#3d2b1f";
    private static final String C_LABEL = "#3a5068";

    private static final ObservableList<PlaylistItem> playlists = FXCollections.observableArrayList();
    private static final PlayerService playerService = new PlayerService();
    private static boolean initialized = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initData();
        openWindow(stage);
    }

    public void show(Stage owner) {
        initData();
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        openWindow(stage);
    }

    public static PlayerService getPlayerService() {
        initData();
        return playerService;
    }

    public static void ensureDefaults() {
        initData();
    }

    private static void initData() {
        if (initialized) {
            return;
        }
        initialized = true;

        playlists.clear();
        playlists.addAll(
            new PlaylistItem("My Favorites", "Songs I love"),
            new PlaylistItem("Chill Vibes",  "Relaxing music"),
            new PlaylistItem("Workout Mix",  "High energy")
        );
    }

    private void openWindow(Stage stage) {
        stage.setTitle("My Playlist");
        stage.setResizable(false);
        stage.setScene(new Scene(buildMainPane(stage), 460, 310));
        stage.show();
    }

    private BorderPane buildMainPane(Stage owner) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(buildTopBar());
        root.setCenter(buildCenterArea(owner));
        return root;
    }

    private HBox buildTopBar() {
        Label title = new Label("MYPLAYLIST");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill:" + TEXT + ";");
        HBox bar = new HBox(title);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10));
        bar.setStyle("-fx-background-color:#eeeeee;-fx-border-color:#cccccc;-fx-border-width:0 0 1 0;");
        return bar;
    }

    private VBox buildCenterArea(Stage owner) {
        HBox cardPane = new HBox();
        cardPane.setAlignment(Pos.CENTER);
        cardPane.setPadding(new Insets(20, 24, 20, 24));
        cardPane.setStyle(
            "-fx-background-color:" + PANEL + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;"
        );

        for (int i = 0; i < playlists.size(); i++) {
            if (i > 0) {
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                cardPane.getChildren().add(sp);
            }
            cardPane.getChildren().add(buildCard(playlists.get(i)));
        }

        Button btnCreate = flatBtn("＋  Create Playlist");
        Button btnEdit   = flatBtn("✎  Edit Playlist");

        btnCreate.setOnAction(e -> openCreateDialog(owner));
        btnEdit.setOnAction(e -> {
            if (playlists.isEmpty()) {
                alert("ยังไม่มี Playlist");
            } else {
                openEditListDialog(owner);
            }
        });

        HBox bar = new HBox(6, btnCreate, btnEdit);
        bar.setPadding(new Insets(10, 4, 4, 4));

        VBox center = new VBox(0, cardPane, bar);
        center.setPadding(new Insets(14, 16, 14, 16));
        return center;
    }

    private VBox buildCard(PlaylistItem p) {
        Pane cassette = buildCassette(104, 70);

        Label lbl = new Label(p.getName());
        lbl.setFont(Font.font("Arial", 11));
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(100);
        lbl.setStyle("-fx-text-alignment:center;-fx-text-fill:" + TEXT + ";");

        Label sub = new Label(p.getDescription());
        sub.setFont(Font.font("Arial", 9));
        sub.setAlignment(Pos.CENTER);
        sub.setMaxWidth(100);
        sub.setStyle("-fx-text-alignment:center;-fx-text-fill:" + MUTED + ";");

        VBox card = new VBox(6, cassette, lbl, sub);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(8, 6, 8, 6));
        card.setCursor(Cursor.HAND);

        String sOff = "-fx-background-color:transparent;-fx-border-color:transparent;-fx-border-radius:8;-fx-background-radius:8;";
        String sOn  = "-fx-background-color:rgba(0,0,0,0.04);-fx-border-color:#dddddd;-fx-border-radius:8;-fx-background-radius:8;";
        card.setStyle(sOff);
        card.setOnMouseEntered(e -> card.setStyle(sOn));
        card.setOnMouseExited(e  -> card.setStyle(sOff));

        // กดทั้งรูปเทปหรือทั้ง card = เล่น playlist อัตโนมัติ
        card.setOnMouseClicked(e -> playPlaylistNow(p));

        return card;
    }

    private Pane buildCassette(double w, double h) {
        Pane p = new Pane();
        p.setPrefSize(w, h);

        Rectangle body = new Rectangle(0, 0, w, h);
        body.setArcWidth(10);
        body.setArcHeight(10);
        body.setFill(Color.web(C_BODY));

        Rectangle shine = new Rectangle(2, 1, w - 4, h * 0.18);
        shine.setArcWidth(8);
        shine.setArcHeight(8);
        shine.setFill(Color.web("#ffffff", 0.07));

        double wx = w * 0.12, wy = h * 0.12, ww = w * 0.76, wh = h * 0.50;
        Rectangle win = new Rectangle(wx, wy, ww, wh);
        win.setArcWidth(5);
        win.setArcHeight(5);
        win.setFill(Color.web(C_WIN));
        win.setStroke(Color.web("#4a6070"));
        win.setStrokeWidth(1);

        double cy = wy + wh * 0.52;
        double r  = wh * 0.36;
        double lx = wx + ww * 0.25, rx = wx + ww * 0.75;

        Circle reelL = reel(lx, cy, r);
        Circle reelR = reel(rx, cy, r);
        Circle hubL  = hub(lx, cy, r * 0.36);
        Circle hubR  = hub(rx, cy, r * 0.36);

        double tapeY = cy + r * 0.55;
        Line tape = new Line(lx + r * 0.55, tapeY, rx - r * 0.55, tapeY);
        tape.setStroke(Color.web(C_TAPE));
        tape.setStrokeWidth(2.5);

        Rectangle label = new Rectangle(w * 0.08, h * 0.67, w * 0.84, h * 0.25);
        label.setArcWidth(4);
        label.setArcHeight(4);
        label.setFill(Color.web(C_LABEL));

        Circle botHole = new Circle(w * 0.5, h * 0.92, 3.5);
        botHole.setFill(Color.web(C_HUB));
        botHole.setStroke(Color.web("#4a6070"));
        botHole.setStrokeWidth(0.8);

        p.getChildren().addAll(body, shine, win, reelL, reelR, hubL, hubR, tape, label, botHole);
        return p;
    }

    private Circle reel(double cx, double cy, double r) {
        Circle c = new Circle(cx, cy, r);
        c.setFill(Color.web(C_REEL));
        c.setStroke(Color.web("#6a8090"));
        c.setStrokeWidth(1);
        return c;
    }

    private Circle hub(double cx, double cy, double r) {
        Circle c = new Circle(cx, cy, r);
        c.setFill(Color.web(C_HUB));
        return c;
    }

    private void openCreateDialog(Stage owner) {
        Stage dlg = makeDialog(owner, "Create Playlist");
        TextField tfTitle = inputField("Playlist Title");
        TextField tfDesc = inputField("Playlist Description");
        Button btnOk = flatBtn("Create");
        Button btnCancel = flatBtn("Cancel");

        btnCancel.setOnAction(e -> dlg.close());
        btnOk.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) {
                alert("กรุณากรอกชื่อ Playlist");
                return;
            }
            playlists.add(new PlaylistItem(name, tfDesc.getText().trim()));
            dlg.close();
            refreshMain(owner);
        });

        HBox body = new HBox(14, buildCassette(90, 62), vstack(tfTitle, tfDesc));
        body.setAlignment(Pos.CENTER_LEFT);
        body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Create Playlist", body, footer(btnOk, btnCancel)), 330, 192));
        dlg.showAndWait();
    }

    // แทนหน้ารูปที่ 2 ด้วยหน้ารายการ playlist สำหรับแก้ไขได้ทันที
    private void openEditListDialog(Stage owner) {
        Stage dlg = makeDialog(owner, "Edit Playlist");

        VBox listBox = new VBox(8);
        listBox.setPadding(new Insets(12));

        for (PlaylistItem p : playlists) {
            Button itemBtn = flatBtn(p.getName());
            itemBtn.setPrefWidth(240);
            itemBtn.setAlignment(Pos.CENTER_LEFT);
            itemBtn.setOnAction(e -> {
                dlg.close();
                openEditDialog(owner, p);
            });
            listBox.getChildren().add(itemBtn);
        }

        BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color:" + BG + ";");
        pane.setTop(buildSubBar("Edit Playlist"));
        pane.setCenter(listBox);

        dlg.setScene(new Scene(pane, 300, 220));
        dlg.showAndWait();
    }

    // หน้า edit จะเป็นแบบรูปที่ 3
    private void openEditDialog(Stage owner, PlaylistItem p) {
        Stage dlg = makeDialog(owner, p.getName());

        ObservableList<SongItem> songs = FXCollections.observableArrayList(toSongItems(p.getPlaylist()));
        TableView<SongItem> table = buildSongTable(dlg, p, songs);

        Button btnAddSong = flatBtn("＋  Add Song");

        btnAddSong.setOnAction(e -> openAddSongDialog(dlg, p, songs));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(buildSubBar(p.getName()));

        VBox center = new VBox(0, table);
        center.setPadding(new Insets(10, 14, 0, 14));

        HBox bottomBar = new HBox(btnAddSong);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(8, 14, 12, 14));

        root.setCenter(center);
        root.setBottom(bottomBar);

        dlg.setScene(new Scene(root, 460, 290));
        dlg.showAndWait();
    }

    private List<SongItem> toSongItems(Playlist playlist) {
        List<SongItem> items = new ArrayList<>();
        for (Song song : playlist.getSongs()) {
            items.add(new SongItem(song));
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    private TableView<SongItem> buildSongTable(Stage owner, PlaylistItem playlistItem, ObservableList<SongItem> songs) {
        TableView<SongItem> table = new TableView<>(songs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        table.setStyle("-fx-background-color:" + PANEL + ";-fx-border-color:" + BORDER + ";");

        TableColumn<SongItem, Void> cAct = new TableColumn<>("");
        cAct.setMinWidth(50);
        cAct.setMaxWidth(50);
        cAct.setCellFactory(c -> new TableCell<>() {
            private final Button b = flatBtn("•••");
            {
                b.setOnAction(e -> {
                    SongItem item = getTableView().getItems().get(getIndex());
                    confirmRemove(owner, playlistItem, item, getTableView().getItems());
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : b);
            }
        });

        table.getColumns().addAll(
            tableCol("Song Title", "title", 2.5),
            tableCol("Artist", "artist", 1.5),
            tableCol("Date Add", "dateAdded", 1.2),
            tableCol("Time", "duration", 0.8),
            cAct
        );

        table.setRowFactory(tv -> {
            TableRow<SongItem> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() >= 2) {
                    int index = row.getIndex();
                    playerService.playSongInPlaylist(playlistItem.getPlaylist(), index);
                }
            });
            return row;
        });

        return table;
    }

    private void openAddSongDialog(Stage owner, PlaylistItem playlistItem, ObservableList<SongItem> songs) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Song File");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aac"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = chooser.showOpenDialog(owner);
        if (selectedFile == null) {
            return;
        }

        Stage dlg = makeDialog(owner, "Add Song");
        TextField tfTitle = inputField("Song Title");
        TextField tfArtist = inputField("Artist");
        TextField tfDur = inputField("Duration e.g. 3:30");

        tfTitle.setText(stripExtension(selectedFile.getName()));

        Button btnOk = flatBtn("Add");
        Button btnCancel = flatBtn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnOk.setOnAction(e -> {
            String title = tfTitle.getText().trim();
            if (title.isEmpty()) {
                alert("กรุณากรอกชื่อเพลง");
                return;
            }

            int durationSec = parseDurationToSeconds(tfDur.getText().trim());
            Song song = new Song(title, tfArtist.getText().trim(), selectedFile.getAbsolutePath(), durationSec);
            playlistItem.getPlaylist().addSong(song);
            songs.setAll(toSongItems(playlistItem.getPlaylist()));
            dlg.close();
        });

        Label fileLabel = lbl("File: " + selectedFile.getName());
        VBox body = vstack(
            fileLabel,
            lbl("Song Title:"), tfTitle,
            lbl("Artist:"), tfArtist,
            lbl("Duration:"), tfDur
        );
        body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Add Song", body, footer(btnOk, btnCancel)), 290, 290));
        dlg.showAndWait();
    }

    private void confirmRemove(Stage owner, PlaylistItem playlistItem, SongItem s, ObservableList<SongItem> list) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "ลบ \"" + s.getTitle() + "\" ?", ButtonType.YES, ButtonType.NO);
        a.initOwner(owner);
        a.setHeaderText(null);
        a.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
            playlistItem.getPlaylist().removeSong(s.getSong().getId());
            list.setAll(toSongItems(playlistItem.getPlaylist()));
        });
    }

    private void playPlaylistNow(PlaylistItem playlistItem) {
        if (playlistItem.getPlaylist().getSongs().isEmpty()) {
            alert("Playlist นี้ยังไม่มีเพลง");
            return;
        }
        playerService.playPlaylist(playlistItem.getPlaylist());
    }

    private void refreshMain(Stage owner) {
        owner.getScene().setRoot(buildMainPane(owner));
    }

    private HBox buildSubBar(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setStyle("-fx-text-fill:" + TEXT + ";");
        HBox bar = new HBox(l);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 14, 8, 14));
        bar.setStyle("-fx-background-color:#eeeeee;-fx-border-color:#cccccc;-fx-border-width:0 0 1 0;");
        return bar;
    }

    private Button flatBtn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        String off = "-fx-background-color:transparent;-fx-border-color:transparent;-fx-font-weight:bold;-fx-text-fill:#444444;-fx-cursor:hand;";
        String on  = "-fx-background-color:rgba(0,0,0,0.08);-fx-border-color:transparent;-fx-font-weight:bold;-fx-text-fill:#111111;";
        b.setStyle(off);
        b.setOnMouseEntered(e -> b.setStyle(on));
        b.setOnMouseExited(e  -> b.setStyle(off));
        return b;
    }

    private TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(185);
        tf.setStyle("-fx-background-color:white;-fx-border-color:#cccccc;-fx-border-width:0 0 1 0;-fx-padding:4;");
        return tf;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:10px;");
        return l;
    }

    private VBox vstack(Node... nodes) {
        VBox v = new VBox(7, nodes);
        v.setAlignment(Pos.TOP_LEFT);
        return v;
    }

    private HBox footer(Button... btns) {
        HBox h = new HBox(8, btns);
        h.setAlignment(Pos.CENTER_RIGHT);
        h.setPadding(new Insets(0, 14, 12, 14));
        return h;
    }

    private BorderPane dialogPane(String title, Node center, Node bottom) {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color:" + BG + ";");
        bp.setTop(buildSubBar(title));
        bp.setCenter(center);
        bp.setBottom(bottom);
        return bp;
    }

    private Stage makeDialog(Stage owner, String title) {
        Stage s = new Stage();
        s.initModality(Modality.WINDOW_MODAL);
        s.initOwner(owner);
        s.setTitle(title);
        s.setResizable(false);
        return s;
    }

    private <T> TableColumn<T, String> tableCol(String header, String prop, double w) {
        TableColumn<T, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setMinWidth(60 * w);
        return c;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private int parseDurationToSeconds(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            if (value.contains(":")) {
                String[] parts = value.split(":");
                if (parts.length == 2) {
                    return Integer.parseInt(parts[0].trim()) * 60 + Integer.parseInt(parts[1].trim());
                }
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatDuration(int seconds) {
        int mins = Math.max(0, seconds) / 60;
        int secs = Math.max(0, seconds) % 60;
        return String.format("%d:%02d", mins, secs);
    }

    public static class PlaylistItem {
        private String name;
        private String description;
        private final Playlist playlist;

        public PlaylistItem(String n, String d) {
            this.name = n;
            this.description = d;
            this.playlist = new Playlist(n, d);
        }

        public String getName() {
            return name;
        }

        public void setName(String v) {
            name = v;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String v) {
            description = v;
        }

        public Playlist getPlaylist() {
            return playlist;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public class SongItem {
        private final Song song;
        private final StringProperty title;
        private final StringProperty artist;
        private final StringProperty dateAdded;
        private final StringProperty duration;

        public SongItem(Song song) {
            this.song = song;
            this.title = new SimpleStringProperty(song.getTitle());
            this.artist = new SimpleStringProperty(song.getArtist());
            this.dateAdded = new SimpleStringProperty(LocalDate.now().toString());
            this.duration = new SimpleStringProperty(formatDuration(song.getDurationSrc()));
        }

        public Song getSong() {
            return song;
        }

        public String getTitle() {
            return title.get();
        }

        public StringProperty titleProperty() {
            return title;
        }

        public String getArtist() {
            return artist.get();
        }

        public StringProperty artistProperty() {
            return artist;
        }

        public String getDateAdded() {
            return dateAdded.get();
        }

        public StringProperty dateAddedProperty() {
            return dateAdded;
        }

        public String getDuration() {
            return duration.get();
        }

        public StringProperty durationProperty() {
            return duration;
        }
    }
}