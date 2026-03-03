import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.stage.*;

/**
 * PlaylistWindow — MDI Child Form
 * รันแบบ standalone ได้ก่อน เชื่อม MDI ทีหลังโดยลบ main() และ extends Application ออก
 */
public class PlaylistWindow extends Application {

    private final ObservableList<PlaylistItem> playlists = FXCollections.observableArrayList();

    // ── Entry point (standalone) ──────────────────────────────
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        playlists.addAll(
            new PlaylistItem("My Favorites", "Songs I love"),
            new PlaylistItem("Chill Vibes",  "Relaxing music"),
            new PlaylistItem("Workout Mix",  "High energy")
        );
        stage.setTitle("Playlist");
        stage.setResizable(false);
        stage.setScene(new Scene(buildMainPane(stage), 420, 260));
        stage.show();
    }

    // ════════════════════════════════════════════════════════
    // หน้าหลัก
    // ════════════════════════════════════════════════════════

    private BorderPane buildMainPane(Stage owner) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #d4d0c8;");
        root.setTop(titleBar("PLAYLIST"));
        root.setCenter(buildCenterArea(owner));
        return root;
    }

    /** พื้นที่กลาง = กล่อง card + ปุ่มด้านล่าง */
    private VBox buildCenterArea(Stage owner) {
        // กล่อง card ห่อตามเนื้อหา (ไม่ scroll — ถ้า playlist เยอะค่อยเพิ่ม ScrollPane ทีหลัง)
        TilePane cardPane = new TilePane(Orientation.HORIZONTAL, 16, 16);
        cardPane.setPadding(new Insets(14, 16, 14, 16));
        cardPane.setPrefColumns(4);
        cardPane.setTileAlignment(Pos.TOP_CENTER);
        cardPane.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #808080 #ffffff #ffffff #808080;" +
            "-fx-border-width: 2;"
        );
        playlists.forEach(p -> cardPane.getChildren().add(buildCard(p, owner)));

        // ปุ่ม Create / Edit
        Button btnCreate = btn("Create Playlist");
        Button btnEdit   = btn("Edit Playlist");
        btnCreate.setOnAction(e -> openCreateDialog(owner));
        btnEdit.setOnAction(e -> {
            if (playlists.isEmpty()) alert("ยังไม่มี Playlist");
            else                     openEditDialog(owner, playlists.get(0));
        });

        HBox buttonBar = new HBox(8, btnCreate, btnEdit);
        buttonBar.setPadding(new Insets(8, 10, 10, 10));

        // ให้ cardPane ยืดเต็มความกว้าง
        VBox.setVgrow(cardPane, Priority.NEVER);
        HBox.setHgrow(cardPane, Priority.ALWAYS);

        VBox center = new VBox(0, cardPane, buttonBar);
        center.setPadding(new Insets(14, 25, 14, 25));
        return center;
    }

    /** Card แสดง thumbnail + ชื่อ playlist — คลิกเพื่อเปิดหน้าเพลง */
    private VBox buildCard(PlaylistItem p, Stage owner) {
        Label nameLabel = new Label(p.getName());
        nameLabel.setFont(Font.font("Arial", 11));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setStyle("-fx-text-alignment: center;");

        Pane thumb = buildThumbnail(90, 76);
        VBox.setVgrow(thumb, Priority.NEVER);

        VBox card = new VBox(6, thumb, nameLabel);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(100);
        card.setPadding(new Insets(4));
        card.setCursor(Cursor.HAND);
        card.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 1;"
        );
        // hover highlight
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #e8e8ff;" +
            "-fx-border-color: #aaaacc;" +
            "-fx-border-width: 1;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 1;"
        ));
        card.setOnMouseClicked(e -> openDetailWindow(owner, p));
        return card;
    }

    /** กล่อง placeholder รูป X */
    private Pane buildThumbnail(double w, double h) {
        Pane pane = new Pane();
        pane.setPrefSize(w, h);
        pane.setStyle(
            "-fx-background-color: #f0f0f0;" +
            "-fx-border-color: #808080 #ffffff #ffffff #808080;" +
            "-fx-border-width: 2;"
        );
        Line l1 = new Line(0, 0, w, h); l1.setStroke(Color.GRAY);
        Line l2 = new Line(w, 0, 0, h); l2.setStroke(Color.GRAY);
        pane.getChildren().addAll(l1, l2);
        return pane;
    }

    // ════════════════════════════════════════════════════════
    // Dialog: Create Playlist
    // ════════════════════════════════════════════════════════

    private void openCreateDialog(Stage owner) {
        Stage dlg = makeDialog(owner, "Create Playlist");

        TextField tfTitle = inputField("Playlist Title");
        TextField tfDesc  = inputField("Playlist Description");

        Button btnCreate = btn("Create");
        Button btnCancel = btn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnCreate.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) { alert("กรุณากรอกชื่อ Playlist"); return; }
            playlists.add(new PlaylistItem(name, tfDesc.getText().trim()));
            dlg.close();
            refreshMain(owner);
        });

        HBox body = new HBox(12, buildThumbnail(80, 72), vstack(tfTitle, tfDesc));
        body.setPadding(new Insets(12));

        dlg.setScene(new Scene(dialogPane("Create Playlist", body, footer(btnCreate, btnCancel)), 310, 180));
        dlg.showAndWait();
    }

    // ════════════════════════════════════════════════════════
    // Dialog: Edit Playlist
    // ════════════════════════════════════════════════════════

    private void openEditDialog(Stage owner, PlaylistItem p) {
        Stage dlg = makeDialog(owner, "Edit Playlist");

        TextField tfTitle = inputField(p.getName());
        TextField tfDesc  = inputField(p.getDescription());
        tfTitle.setText(p.getName());
        tfDesc.setText(p.getDescription());

        Button btnDelete = btn("Delete");
        Button btnSave   = btn("Save");
        Button btnCancel = btn("Cancel");

        btnDelete.setOnAction(e -> { playlists.remove(p); dlg.close(); refreshMain(owner); });
        btnCancel.setOnAction(e -> dlg.close());
        btnSave.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) { alert("กรุณากรอกชื่อ Playlist"); return; }
            p.setName(name);
            p.setDescription(tfDesc.getText().trim());
            dlg.close();
            refreshMain(owner);
        });

        HBox body = new HBox(12, buildThumbnail(80, 72), vstack(tfTitle, tfDesc, btnDelete));
        body.setPadding(new Insets(12));

        dlg.setScene(new Scene(dialogPane("Edit Playlist", body, footer(btnSave, btnCancel)), 310, 200));
        dlg.showAndWait();
    }

    // ════════════════════════════════════════════════════════
    // หน้า Playlist Detail (รายชื่อเพลง)
    // ════════════════════════════════════════════════════════

    private void openDetailWindow(Stage owner, PlaylistItem p) {
        Stage win = makeDialog(owner, "PLAYLIST — " + p.getName());

        // ข้อมูลตัวอย่าง (จะเชื่อมกับ DB จริงภายหลัง)
        ObservableList<SongItem> songs = FXCollections.observableArrayList(
            new SongItem("Blinding Lights", "The Weeknd", "2024-01-10", "3:20"),
            new SongItem("Shape of You",    "Ed Sheeran",  "2024-01-12", "3:53"),
            new SongItem("Stay",            "J. Bieber",   "2024-02-01", "2:21")
        );

        TableView<SongItem> table = buildSongTable(win, songs);

        Button btnAdd = btn("+");
        btnAdd.setPrefWidth(32);
        btnAdd.setOnAction(e -> openAddSongDialog(win, songs));

        HBox foot = new HBox(btnAdd);
        foot.setPadding(new Insets(6, 10, 10, 10));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #d4d0c8;");
        root.setTop(titleBar("PLAYLIST — " + p.getName()));
        root.setCenter(new VBox(0, table, foot) {{ setPadding(new Insets(10)); }});

        win.setScene(new Scene(root, 420, 260));
        win.showAndWait();
    }

    /** สร้าง TableView พร้อม column ครบ */
    @SuppressWarnings("unchecked")
    private TableView<SongItem> buildSongTable(Stage owner, ObservableList<SongItem> songs) {
        TableView<SongItem> table = new TableView<>(songs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(190);

        // คอลัมน์ปุ่ม "..."
        TableColumn<SongItem, Void> cAction = new TableColumn<>("");
        cAction.setMinWidth(40);
        cAction.setMaxWidth(40);
        cAction.setCellFactory(c -> new TableCell<>() {
            private final Button b = btn("...");
            {
                b.setPrefWidth(32);
                b.setPrefHeight(24);
                b.setOnAction(e -> confirmRemove(
                    owner,
                    getTableView().getItems().get(getIndex()),
                    getTableView().getItems()
                ));
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : b);
            }
        });

        table.getColumns().addAll(
            tableCol("Song Title", "title",     2.5),
            tableCol("Artist",     "artist",    1.5),
            tableCol("Date Add",   "dateAdded", 1.2),
            tableCol("Time",       "duration",  0.8),
            cAction
        );
        return table;
    }

    // ════════════════════════════════════════════════════════
    // Dialog: Add Song
    // ════════════════════════════════════════════════════════

    private void openAddSongDialog(Stage owner, ObservableList<SongItem> songs) {
        Stage dlg = makeDialog(owner, "Add Song");

        TextField tfTitle    = inputField("Song Title");
        TextField tfArtist   = inputField("Artist");
        TextField tfDuration = inputField("Duration  e.g. 3:30");

        Button btnAdd    = btn("Add");
        Button btnCancel = btn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnAdd.setOnAction(e -> {
            if (tfTitle.getText().trim().isEmpty()) { alert("กรุณากรอกชื่อเพลง"); return; }
            songs.add(new SongItem(
                tfTitle.getText(), tfArtist.getText(),
                java.time.LocalDate.now().toString(), tfDuration.getText()
            ));
            dlg.close();
        });

        VBox body = vstack(
            new Label("Song Title:"), tfTitle,
            new Label("Artist:"),     tfArtist,
            new Label("Duration:"),   tfDuration
        );
        body.setPadding(new Insets(14));

        dlg.setScene(new Scene(dialogPane("Add Song", body, footer(btnAdd, btnCancel)), 280, 250));
        dlg.showAndWait();
    }

    /** ยืนยันก่อนลบเพลง */
    private void confirmRemove(Stage owner, SongItem s, ObservableList<SongItem> list) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
            "ลบ \"" + s.getTitle() + "\" ออกจาก playlist?",
            ButtonType.YES, ButtonType.NO);
        a.initOwner(owner);
        a.setHeaderText(null);
        a.showAndWait()
         .filter(r -> r == ButtonType.YES)
         .ifPresent(r -> list.remove(s));
    }

    /** rebuild หน้าหลักหลังแก้ไข playlist */
    private void refreshMain(Stage owner) {
        owner.getScene().setRoot(buildMainPane(owner));
    }

    // ════════════════════════════════════════════════════════
    // UI Builder Helpers
    // ════════════════════════════════════════════════════════

    /** Title bar สีน้ำเงิน Windows-style */
    private Label titleBar(String text) {
        Label l = new Label("  " + text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setStyle(
            "-fx-background-color: linear-gradient(to right, #000080, #1084d0);" +
            "-fx-text-fill: white; -fx-padding: 4 6 4 6; -fx-pref-width: 9999;"
        );
        return l;
    }

    /** ปุ่มสไตล์ Windows 95 — กด/ปล่อยมี border สลับ */
    private Button btn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", 11));
        String styleUp   = "-fx-background-color:#d4d0c8;-fx-border-color:#fff #808080 #808080 #fff;-fx-border-width:2;-fx-padding:3 10 3 10;-fx-cursor:hand;";
        String styleDown = "-fx-background-color:#d4d0c8;-fx-border-color:#808080 #fff #fff #808080;-fx-border-width:2;-fx-padding:3 10 3 10;";
        b.setStyle(styleUp);
        b.setOnMousePressed(e  -> b.setStyle(styleDown));
        b.setOnMouseReleased(e -> b.setStyle(styleUp));
        return b;
    }

    /** TextField สไตล์ inset */
    private TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(180);
        tf.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #808080 #ffffff #ffffff #808080;" +
            "-fx-border-width: 2; -fx-padding: 2 4 2 4;"
        );
        return tf;
    }

    /** VBox จัดซ้าย ช่องห่าง 6 */
    private VBox vstack(Node... nodes) {
        VBox v = new VBox(6, nodes);
        v.setAlignment(Pos.TOP_LEFT);
        return v;
    }

    /** แถวปุ่มชิดขวา (footer ของ dialog) */
    private HBox footer(Button... btns) {
        HBox h = new HBox(8, btns);
        h.setAlignment(Pos.CENTER_RIGHT);
        h.setPadding(new Insets(0, 14, 12, 14));
        return h;
    }

    /** BorderPane ครอบ dialog (titlebar + center + footer) */
    private BorderPane dialogPane(String title, Node center, Node bottom) {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #d4d0c8;");
        bp.setTop(titleBar(title));
        bp.setCenter(center);
        bp.setBottom(bottom);
        return bp;
    }

    /** Stage สำหรับ dialog/popup */
    private Stage makeDialog(Stage owner, String title) {
        Stage s = new Stage();
        s.initModality(Modality.WINDOW_MODAL);
        s.initOwner(owner);
        s.setTitle(title);
        s.setResizable(false);
        return s;
    }

    /** TableColumn ทั่วไป พร้อม PropertyValueFactory */
    private <T> TableColumn<T, String> tableCol(String header, String prop, double widthFactor) {
        TableColumn<T, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setMinWidth(60 * widthFactor);
        return c;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    // ════════════════════════════════════════════════════════
    // Data Models
    // ════════════════════════════════════════════════════════

    public static class PlaylistItem {
        private String name, description;

        public PlaylistItem(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName()               { return name; }
        public void   setName(String v)       { name = v; }
        public String getDescription()        { return description; }
        public void   setDescription(String v){ description = v; }
    }

    public static class SongItem {
        private final StringProperty title, artist, dateAdded, duration;

        public SongItem(String title, String artist, String dateAdded, String duration) {
            this.title     = new SimpleStringProperty(title);
            this.artist    = new SimpleStringProperty(artist);
            this.dateAdded = new SimpleStringProperty(dateAdded);
            this.duration  = new SimpleStringProperty(duration);
        }

        public String getTitle()              { return title.get(); }
        public StringProperty titleProperty() { return title; }

        public String getArtist()              { return artist.get(); }
        public StringProperty artistProperty() { return artist; }

        public String getDateAdded()               { return dateAdded.get(); }
        public StringProperty dateAddedProperty()  { return dateAdded; }

        public String getDuration()               { return duration.get(); }
        public StringProperty durationProperty()  { return duration; }
    }
}