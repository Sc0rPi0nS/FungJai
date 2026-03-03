import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class PlaylistWindow extends Application {

    private final ObservableList<PlaylistItem> playlists = FXCollections.observableArrayList();

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

    // ── หน้าหลัก ─────────────────────────────────────────────

    private BorderPane buildMainPane(Stage owner) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #d4d0c8;");
        root.setTop(titleBar("PLAYLIST"));
        root.setCenter(buildCenterArea(owner));
        return root;
    }

    private VBox buildCenterArea(Stage owner) {
        HBox cardPane = new HBox();
        cardPane.setAlignment(Pos.CENTER);
        cardPane.setPadding(new Insets(14, 20, 14, 20));
        cardPane.setStyle("-fx-background-color:white;-fx-border-color:#808080 #fff #fff #808080;-fx-border-width:2;");
        for (int i = 0; i < playlists.size(); i++) {
            if (i > 0) { Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS); cardPane.getChildren().add(sp); }
            cardPane.getChildren().add(buildCard(playlists.get(i), owner));
        }
        Button btnCreate = btn("Create Playlist"), btnEdit = btn("Edit Playlist");
        btnCreate.setOnAction(e -> openCreateDialog(owner));
        btnEdit.setOnAction(e -> { if (playlists.isEmpty()) alert("ยังไม่มี Playlist"); else openEditDialog(owner, playlists.get(0)); });
        HBox bar = new HBox(8, btnCreate, btnEdit);
        bar.setPadding(new Insets(8, 10, 10, 10));
        VBox center = new VBox(0, cardPane, bar);
        center.setPadding(new Insets(10, 10, 0, 10));
        return center;
    }

    private VBox buildCard(PlaylistItem p, Stage owner) {
        Label lbl = new Label(p.getName());
        lbl.setFont(Font.font("Arial", 11));
        lbl.setWrapText(true); lbl.setAlignment(Pos.CENTER); lbl.setMaxWidth(90);
        lbl.setStyle("-fx-text-alignment:center;");
        VBox card = new VBox(6, buildThumbnail(90, 76), lbl);
        card.setAlignment(Pos.TOP_CENTER); card.setPadding(new Insets(4)); card.setCursor(Cursor.HAND);
        String sNormal = "-fx-background-color:transparent;-fx-border-color:transparent;-fx-border-width:1;";
        String sHover  = "-fx-background-color:#e8e8ff;-fx-border-color:#aaaacc;-fx-border-width:1;";
        card.setStyle(sNormal);
        card.setOnMouseEntered(e -> card.setStyle(sHover));
        card.setOnMouseExited(e  -> card.setStyle(sNormal));
        card.setOnMouseClicked(e -> openDetailWindow(owner, p));
        return card;
    }

    private Pane buildThumbnail(double w, double h) {
        Pane pane = new Pane();
        pane.setPrefSize(w, h);
        pane.setStyle("-fx-background-color:#f0f0f0;-fx-border-color:#808080 #fff #fff #808080;-fx-border-width:2;");
        Line l1 = new Line(0,0,w,h); l1.setStroke(Color.GRAY);
        Line l2 = new Line(w,0,0,h); l2.setStroke(Color.GRAY);
        pane.getChildren().addAll(l1, l2);
        return pane;
    }

    // ── Dialogs ───────────────────────────────────────────────

    private void openCreateDialog(Stage owner) {
        Stage dlg = makeDialog(owner, "Create Playlist");
        TextField tfTitle = inputField("Playlist Title"), tfDesc = inputField("Playlist Description");
        Button btnOk = btn("Create"), btnCancel = btn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnOk.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) { alert("กรุณากรอกชื่อ Playlist"); return; }
            playlists.add(new PlaylistItem(name, tfDesc.getText().trim()));
            dlg.close(); refreshMain(owner);
        });
        HBox body = new HBox(12, buildThumbnail(80, 72), vstack(tfTitle, tfDesc));
        body.setPadding(new Insets(12));
        dlg.setScene(new Scene(dialogPane("Create Playlist", body, footer(btnOk, btnCancel)), 310, 180));
        dlg.showAndWait();
    }

    private void openEditDialog(Stage owner, PlaylistItem p) {
        Stage dlg = makeDialog(owner, "Edit Playlist");
        TextField tfTitle = inputField(p.getName()), tfDesc = inputField(p.getDescription());
        tfTitle.setText(p.getName()); tfDesc.setText(p.getDescription());
        Button btnDel = btn("Delete"), btnSave = btn("Save"), btnCancel = btn("Cancel");
        btnDel.setOnAction(e -> { playlists.remove(p); dlg.close(); refreshMain(owner); });
        btnCancel.setOnAction(e -> dlg.close());
        btnSave.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) { alert("กรุณากรอกชื่อ Playlist"); return; }
            p.setName(name); p.setDescription(tfDesc.getText().trim());
            dlg.close(); refreshMain(owner);
        });
        HBox body = new HBox(12, buildThumbnail(80, 72), vstack(tfTitle, tfDesc, btnDel));
        body.setPadding(new Insets(12));
        dlg.setScene(new Scene(dialogPane("Edit Playlist", body, footer(btnSave, btnCancel)), 310, 200));
        dlg.showAndWait();
    }

    private void openDetailWindow(Stage owner, PlaylistItem p) {
        Stage win = makeDialog(owner, "PLAYLIST — " + p.getName());
        ObservableList<SongItem> songs = FXCollections.observableArrayList(
            new SongItem("Blinding Lights", "The Weeknd", "2024-01-10", "3:20"),
            new SongItem("Shape of You",    "Ed Sheeran",  "2024-01-12", "3:53"),
            new SongItem("Stay",            "J. Bieber",   "2024-02-01", "2:21")
        );
        Button btnAdd = btn("+");
        btnAdd.setPrefWidth(32);
        btnAdd.setOnAction(e -> openAddSongDialog(win, songs));
        HBox foot = new HBox(btnAdd); foot.setPadding(new Insets(6, 10, 10, 10));
        VBox center = new VBox(0, buildSongTable(win, songs), foot);
        center.setPadding(new Insets(10));
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#d4d0c8;");
        root.setTop(titleBar("PLAYLIST — " + p.getName()));
        root.setCenter(center);
        win.setScene(new Scene(root, 420, 260)); win.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private TableView<SongItem> buildSongTable(Stage owner, ObservableList<SongItem> songs) {
        TableView<SongItem> table = new TableView<>(songs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(190);
        TableColumn<SongItem, Void> cAct = new TableColumn<>("");
        cAct.setMinWidth(40); cAct.setMaxWidth(40);
        cAct.setCellFactory(c -> new TableCell<>() {
            private final Button b = btn("...");
            { b.setPrefWidth(32); b.setPrefHeight(24);
              b.setOnAction(e -> confirmRemove(owner, getTableView().getItems().get(getIndex()), getTableView().getItems())); }
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
        Button btnOk = btn("Add"), btnCancel = btn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnOk.setOnAction(e -> {
            if (tfTitle.getText().trim().isEmpty()) { alert("กรุณากรอกชื่อเพลง"); return; }
            songs.add(new SongItem(tfTitle.getText(), tfArtist.getText(), java.time.LocalDate.now().toString(), tfDur.getText()));
            dlg.close();
        });
        VBox body = vstack(new Label("Song Title:"), tfTitle, new Label("Artist:"), tfArtist, new Label("Duration:"), tfDur);
        body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Add Song", body, footer(btnOk, btnCancel)), 280, 250));
        dlg.showAndWait();
    }

    private void confirmRemove(Stage owner, SongItem s, ObservableList<SongItem> list) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "ลบ \"" + s.getTitle() + "\" ออกจาก playlist?", ButtonType.YES, ButtonType.NO);
        a.initOwner(owner); a.setHeaderText(null);
        a.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> list.remove(s));
    }

    private void refreshMain(Stage owner) { owner.getScene().setRoot(buildMainPane(owner)); }

    // ── UI Helpers ────────────────────────────────────────────

    private Label titleBar(String text) {
        Label l = new Label("  " + text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setStyle("-fx-background-color:linear-gradient(to right,#000080,#1084d0);-fx-text-fill:white;-fx-padding:4 6 4 6;-fx-pref-width:9999;");
        return l;
    }

    private Button btn(String text) {
        Button b = new Button(text); b.setFont(Font.font("Arial", 11));
        String up   = "-fx-background-color:#d4d0c8;-fx-border-color:#fff #808080 #808080 #fff;-fx-border-width:2;-fx-padding:3 10 3 10;-fx-cursor:hand;";
        String down = "-fx-background-color:#d4d0c8;-fx-border-color:#808080 #fff #fff #808080;-fx-border-width:2;-fx-padding:3 10 3 10;";
        b.setStyle(up); b.setOnMousePressed(e -> b.setStyle(down)); b.setOnMouseReleased(e -> b.setStyle(up));
        return b;
    }

    private TextField inputField(String prompt) {
        TextField tf = new TextField(); tf.setPromptText(prompt); tf.setPrefWidth(180);
        tf.setStyle("-fx-background-color:white;-fx-border-color:#808080 #fff #fff #808080;-fx-border-width:2;-fx-padding:2 4 2 4;");
        return tf;
    }

    private VBox vstack(Node... nodes) { VBox v = new VBox(6, nodes); v.setAlignment(Pos.TOP_LEFT); return v; }

    private HBox footer(Button... btns) {
        HBox h = new HBox(8, btns); h.setAlignment(Pos.CENTER_RIGHT); h.setPadding(new Insets(0,14,12,14)); return h;
    }

    private BorderPane dialogPane(String title, Node center, Node bottom) {
        BorderPane bp = new BorderPane(); bp.setStyle("-fx-background-color:#d4d0c8;");
        bp.setTop(titleBar(title)); bp.setCenter(center); bp.setBottom(bottom); return bp;
    }

    private Stage makeDialog(Stage owner, String title) {
        Stage s = new Stage(); s.initModality(Modality.WINDOW_MODAL); s.initOwner(owner); s.setTitle(title); s.setResizable(false); return s;
    }

    private <T> TableColumn<T, String> tableCol(String header, String prop, double w) {
        TableColumn<T, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop)); c.setMinWidth(60 * w); return c;
    }

    private void alert(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK) {{ setHeaderText(null); }}.showAndWait(); }

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
