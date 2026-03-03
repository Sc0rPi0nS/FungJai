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

public class PlaylistWindow extends Application {

    // ── สี match HomeWindow (#f5f5f5 theme) ──────────────────
    private static final String BG     = "#f5f5f5";
    private static final String PANEL  = "#ffffff";
    private static final String BORDER = "#e0e0e0";
    private static final String TEXT   = "#333333";
    private static final String MUTED  = "#888888";

    // ── สีเทปคาสเซ็ท (จับจากภาพ HomeWindow) ─────────────────
    private static final String C_BODY  = "#2b3a4a"; // ตัวเทปน้ำเงินเข้ม
    private static final String C_WIN   = "#1a252f"; // ช่องกระจกดำ
    private static final String C_REEL  = "#8a9ba8"; // ล้อเทปสีเงิน
    private static final String C_HUB   = "#1a252f"; // รูกลางล้อ
    private static final String C_TAPE  = "#3d2b1f"; // แถบเทปสีน้ำตาล
    private static final String C_LABEL = "#3a5068"; // ป้ายบนเทป

    private final ObservableList<PlaylistItem> playlists = FXCollections.observableArrayList();

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) { initData(); openWindow(stage); }

    public void show(Stage owner) {
        initData();
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        openWindow(stage);
    }

    private void initData() {
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

    // ── หน้าหลัก ─────────────────────────────────────────────

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
            if (i > 0) { Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS); cardPane.getChildren().add(sp); }
            cardPane.getChildren().add(buildCard(playlists.get(i), owner));
        }

        Button btnCreate = flatBtn("＋  Create Playlist");
        Button btnEdit   = flatBtn("✎  Edit Playlist");
        btnCreate.setOnAction(e -> openCreateDialog(owner));
        btnEdit.setOnAction(e -> {
            if (playlists.isEmpty()) alert("ยังไม่มี Playlist");
            else openEditDialog(owner, playlists.get(0));
        });

        HBox bar = new HBox(6, btnCreate, btnEdit);
        bar.setPadding(new Insets(10, 4, 4, 4));

        VBox center = new VBox(0, cardPane, bar);
        center.setPadding(new Insets(14, 16, 14, 16));
        return center;
    }

    private VBox buildCard(PlaylistItem p, Stage owner) {
        Label lbl = new Label(p.getName());
        lbl.setFont(Font.font("Arial", 11));
        lbl.setWrapText(true); lbl.setAlignment(Pos.CENTER); lbl.setMaxWidth(100);
        lbl.setStyle("-fx-text-alignment:center;-fx-text-fill:" + TEXT + ";");

        Label sub = new Label(p.getDescription());
        sub.setFont(Font.font("Arial", 9));
        sub.setAlignment(Pos.CENTER); sub.setMaxWidth(100);
        sub.setStyle("-fx-text-alignment:center;-fx-text-fill:" + MUTED + ";");

        VBox card = new VBox(6, buildCassette(104, 70), lbl, sub);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(8, 6, 8, 6));
        card.setCursor(Cursor.HAND);

        String sOff = "-fx-background-color:transparent;-fx-border-color:transparent;-fx-border-radius:8;-fx-background-radius:8;";
        String sOn  = "-fx-background-color:rgba(0,0,0,0.04);-fx-border-color:#dddddd;-fx-border-radius:8;-fx-background-radius:8;";
        card.setStyle(sOff);
        card.setOnMouseEntered(e -> card.setStyle(sOn));
        card.setOnMouseExited(e  -> card.setStyle(sOff));
        card.setOnMouseClicked(e -> openDetailWindow(owner, p));
        return card;
    }

    /**
     * วาดเทปคาสเซ็ทจำลอง ให้คล้ายกล่องเทปใน HomeWindow
     * ——————————————————————————————————
     *  ┌────────────────────────────┐
     *  │  ┌──────────────────────┐  │  ← ช่องกระจก
     *  │  │  ○──────────────○   │  │  ← ล้อ + แถบเทป
     *  │  └──────────────────────┘  │
     *  │  [  label / ชื่อเทป  ]     │
     *  └────────────────────────────┘
     */
    private Pane buildCassette(double w, double h) {
        Pane p = new Pane();
        p.setPrefSize(w, h);

        // ตัวเทป
        Rectangle body = new Rectangle(0, 0, w, h);
        body.setArcWidth(10); body.setArcHeight(10);
        body.setFill(Color.web(C_BODY));

        // ไฮไลท์ขอบบน (ให้ดู 3D เล็กน้อย)
        Rectangle shine = new Rectangle(2, 1, w - 4, h * 0.18);
        shine.setArcWidth(8); shine.setArcHeight(8);
        shine.setFill(Color.web("#ffffff", 0.07));

        // ช่องกระจกใส
        double wx = w * 0.12, wy = h * 0.12, ww = w * 0.76, wh = h * 0.50;
        Rectangle win = new Rectangle(wx, wy, ww, wh);
        win.setArcWidth(5); win.setArcHeight(5);
        win.setFill(Color.web(C_WIN));
        win.setStroke(Color.web("#4a6070")); win.setStrokeWidth(1);

        // ล้อซ้าย-ขวา
        double cy = wy + wh * 0.52;
        double r  = wh * 0.36;
        double lx = wx + ww * 0.25, rx = wx + ww * 0.75;

        Circle reelL = reel(lx, cy, r);
        Circle reelR = reel(rx, cy, r);
        Circle hubL  = hub(lx, cy, r * 0.36);
        Circle hubR  = hub(rx, cy, r * 0.36);

        // เส้นแถบเทประหว่างล้อ
        double tapeY = cy + r * 0.55;
        Line tape = new Line(lx + r * 0.55, tapeY, rx - r * 0.55, tapeY);
        tape.setStroke(Color.web(C_TAPE)); tape.setStrokeWidth(2.5);

        // ป้ายบนเทป (label)
        Rectangle label = new Rectangle(w * 0.08, h * 0.67, w * 0.84, h * 0.25);
        label.setArcWidth(4); label.setArcHeight(4);
        label.setFill(Color.web(C_LABEL));

        // รูตรงกลางด้านล่าง
        Circle botHole = new Circle(w * 0.5, h * 0.92, 3.5);
        botHole.setFill(Color.web(C_HUB));
        botHole.setStroke(Color.web("#4a6070")); botHole.setStrokeWidth(0.8);

        p.getChildren().addAll(body, shine, win, reelL, reelR, hubL, hubR, tape, label, botHole);
        return p;
    }

    private Circle reel(double cx, double cy, double r) {
        Circle c = new Circle(cx, cy, r);
        c.setFill(Color.web(C_REEL));
        c.setStroke(Color.web("#6a8090")); c.setStrokeWidth(1);
        return c;
    }

    private Circle hub(double cx, double cy, double r) {
        Circle c = new Circle(cx, cy, r);
        c.setFill(Color.web(C_HUB));
        return c;
    }

    // ── Dialogs ───────────────────────────────────────────────

    private void openCreateDialog(Stage owner) {
        Stage dlg = makeDialog(owner, "Create Playlist");
        TextField tfTitle = inputField("Playlist Title"), tfDesc = inputField("Playlist Description");
        Button btnOk = flatBtn("Create"), btnCancel = flatBtn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnOk.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) { alert("กรุณากรอกชื่อ Playlist"); return; }
            playlists.add(new PlaylistItem(name, tfDesc.getText().trim()));
            dlg.close(); refreshMain(owner);
        });
        HBox body = new HBox(14, buildCassette(90, 62), vstack(tfTitle, tfDesc));
        body.setAlignment(Pos.CENTER_LEFT); body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Create Playlist", body, footer(btnOk, btnCancel)), 330, 192));
        dlg.showAndWait();
    }

    private void openEditDialog(Stage owner, PlaylistItem p) {
        Stage dlg = makeDialog(owner, "Edit Playlist");
        TextField tfTitle = inputField(p.getName()), tfDesc = inputField(p.getDescription());
        tfTitle.setText(p.getName()); tfDesc.setText(p.getDescription());
        Button btnDel = flatBtn("Delete"), btnSave = flatBtn("Save"), btnCancel = flatBtn("Cancel");
        btnDel.setStyle("-fx-background-color:transparent;-fx-border-color:transparent;-fx-font-weight:bold;-fx-text-fill:#cc3333;-fx-cursor:hand;");
        btnDel.setOnAction(e -> { playlists.remove(p); dlg.close(); refreshMain(owner); });
        btnCancel.setOnAction(e -> dlg.close());
        btnSave.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) { alert("กรุณากรอกชื่อ Playlist"); return; }
            p.setName(name); p.setDescription(tfDesc.getText().trim());
            dlg.close(); refreshMain(owner);
        });
        HBox body = new HBox(14, buildCassette(90, 62), vstack(tfTitle, tfDesc, btnDel));
        body.setAlignment(Pos.CENTER_LEFT); body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Edit Playlist", body, footer(btnSave, btnCancel)), 330, 212));
        dlg.showAndWait();
    }

    private void openDetailWindow(Stage owner, PlaylistItem p) {
        Stage win = makeDialog(owner, p.getName());
        ObservableList<SongItem> songs = FXCollections.observableArrayList(
            new SongItem("Blinding Lights", "The Weeknd", "2024-01-10", "3:20"),
            new SongItem("Shape of You",    "Ed Sheeran",  "2024-01-12", "3:53"),
            new SongItem("Stay",            "J. Bieber",   "2024-02-01", "2:21")
        );
        Button btnAdd = flatBtn("＋  Add Song");
        btnAdd.setOnAction(e -> openAddSongDialog(win, songs));
        HBox foot = new HBox(btnAdd);
        foot.setPadding(new Insets(8, 10, 10, 10));
        foot.setStyle("-fx-background-color:" + BG + ";");
        VBox center = new VBox(0, buildSongTable(win, songs), foot);
        center.setPadding(new Insets(10));
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(buildSubBar(p.getName()));
        root.setCenter(center);
        win.setScene(new Scene(root, 460, 290)); win.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private TableView<SongItem> buildSongTable(Stage owner, ObservableList<SongItem> songs) {
        TableView<SongItem> table = new TableView<>(songs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        table.setStyle("-fx-background-color:" + PANEL + ";-fx-border-color:" + BORDER + ";");
        TableColumn<SongItem, Void> cAct = new TableColumn<>("");
        cAct.setMinWidth(50); cAct.setMaxWidth(50);
        cAct.setCellFactory(c -> new TableCell<>() {
            private final Button b = flatBtn("•••");
            { b.setOnAction(e -> confirmRemove(owner, getTableView().getItems().get(getIndex()), getTableView().getItems())); }
            @Override protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); setGraphic(empty ? null : b); }
        });
        table.getColumns().addAll(
            tableCol("Song Title","title",2.5), tableCol("Artist","artist",1.5),
            tableCol("Date Add","dateAdded",1.2), tableCol("Time","duration",0.8), cAct);
        return table;
    }

    private void openAddSongDialog(Stage owner, ObservableList<SongItem> songs) {
        Stage dlg = makeDialog(owner, "Add Song");
        TextField tfTitle = inputField("Song Title"), tfArtist = inputField("Artist"), tfDur = inputField("Duration e.g. 3:30");
        Button btnOk = flatBtn("Add"), btnCancel = flatBtn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnOk.setOnAction(e -> {
            if (tfTitle.getText().trim().isEmpty()) { alert("กรุณากรอกชื่อเพลง"); return; }
            songs.add(new SongItem(tfTitle.getText(), tfArtist.getText(), java.time.LocalDate.now().toString(), tfDur.getText()));
            dlg.close();
        });
        VBox body = vstack(lbl("Song Title:"), tfTitle, lbl("Artist:"), tfArtist, lbl("Duration:"), tfDur);
        body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Add Song", body, footer(btnOk, btnCancel)), 290, 268));
        dlg.showAndWait();
    }

    private void confirmRemove(Stage owner, SongItem s, ObservableList<SongItem> list) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "ลบ \"" + s.getTitle() + "\" ?", ButtonType.YES, ButtonType.NO);
        a.initOwner(owner); a.setHeaderText(null);
        a.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> list.remove(s));
    }

    private void refreshMain(Stage owner) { owner.getScene().setRoot(buildMainPane(owner)); }

    // ── UI Helpers ────────────────────────────────────────────

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

    /** ปุ่ม flat match HomeWindow */
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
        TextField tf = new TextField(); tf.setPromptText(prompt); tf.setPrefWidth(185);
        tf.setStyle("-fx-background-color:white;-fx-border-color:#cccccc;-fx-border-width:0 0 1 0;-fx-padding:4;");
        return tf;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:10px;");
        return l;
    }

    private VBox vstack(Node... nodes) { VBox v = new VBox(7, nodes); v.setAlignment(Pos.TOP_LEFT); return v; }

    private HBox footer(Button... btns) {
        HBox h = new HBox(8, btns); h.setAlignment(Pos.CENTER_RIGHT); h.setPadding(new Insets(0,14,12,14)); return h;
    }

    private BorderPane dialogPane(String title, Node center, Node bottom) {
        BorderPane bp = new BorderPane(); bp.setStyle("-fx-background-color:" + BG + ";");
        bp.setTop(buildSubBar(title)); bp.setCenter(center); bp.setBottom(bottom); return bp;
    }

    private Stage makeDialog(Stage owner, String title) {
        Stage s = new Stage(); s.initModality(Modality.WINDOW_MODAL); s.initOwner(owner);
        s.setTitle(title); s.setResizable(false); return s;
    }

    private <T> TableColumn<T, String> tableCol(String header, String prop, double w) {
        TableColumn<T, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop)); c.setMinWidth(60 * w); return c;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }

    // ── Data Models ───────────────────────────────────────────

    public static class PlaylistItem {
        private String name, description;
        public PlaylistItem(String n, String d) { name = n; description = d; }
        public String getName()                { return name; }
        public void   setName(String v)        { name = v; }
        public String getDescription()         { return description; }
        public void   setDescription(String v) { description = v; }
    }

    public static class SongItem {
        private final StringProperty title, artist, dateAdded, duration;
        public SongItem(String t, String a, String d, String dur) {
            title = new SimpleStringProperty(t); artist = new SimpleStringProperty(a);
            dateAdded = new SimpleStringProperty(d); duration = new SimpleStringProperty(dur);
        }
        public String getTitle()               { return title.get(); }
        public StringProperty titleProperty()  { return title; }
        public String getArtist()              { return artist.get(); }
        public StringProperty artistProperty() { return artist; }
        public String getDateAdded()               { return dateAdded.get(); }
        public StringProperty dateAddedProperty()  { return dateAdded; }
        public String getDuration()               { return duration.get(); }
        public StringProperty durationProperty()  { return duration; }
    }
}