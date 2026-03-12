
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

import java.util.UUID;

public class PlaylistWindow {

    // ── theme ─────────────────────────────────────────────────
    private static final String BG = "#f5f5f5";
    private static final String PANEL = "#ffffff";
    private static final String BORDER = "#e0e0e0";
    private static final String TEXT = "#333333";
    private static final String MUTED = "#888888";

    // cassette palette
    private static final String C_BODY = "#2b3a4a";
    private static final String C_WIN = "#1a252f";
    private static final String C_REEL = "#8a9ba8";
    private static final String C_HUB = "#1a252f";
    private static final String C_TAPE = "#3d2b1f";
    private static final String C_LABEL = "#3a5068";

    // ── state ─────────────────────────────────────────────────
    private LibraryService libraryService;
    private ObservableList<Playlist> playlists;

    public PlaylistWindow(LibraryService libraryService) {
        this.libraryService = libraryService;
        this.playlists = FXCollections.observableArrayList(libraryService.getPlaylists());
    }

    // keep old no-arg constructor so HomeWindow still compiles,
    // but it won't have a libraryService — we guard against null below
    public PlaylistWindow() {
        this.playlists = FXCollections.observableArrayList();
    }

    // ── entry ─────────────────────────────────────────────────
    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        openWindow(stage);
    }

    private void openWindow(Stage stage) {
        stage.setTitle("My Playlist");
        stage.setResizable(false);
        stage.setScene(new Scene(buildMainPane(stage), 500, 330));
        stage.show();
    }

    // ── main pane ─────────────────────────────────────────────
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
        // ── card row (scrollable) ──────────────────────────────
        HBox cardPane = new HBox(12);
        cardPane.setAlignment(Pos.CENTER_LEFT);
        cardPane.setPadding(new Insets(16, 20, 16, 20));
        cardPane.setStyle(
                "-fx-background-color:" + PANEL + ";"
                + "-fx-border-color:" + BORDER + ";"
                + "-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;"
        );

        rebuildCards(cardPane, owner);

        ScrollPane scroll = new ScrollPane(cardPane);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        // ── bottom buttons ─────────────────────────────────────
        Button btnCreate = flatBtn("＋  Create Playlist");
        btnCreate.setOnAction(e -> {
            openCreateDialog(owner);
            rebuildCards(cardPane, owner);
        });

        HBox bar = new HBox(6, btnCreate);
        bar.setPadding(new Insets(10, 4, 4, 4));

        VBox center = new VBox(8, scroll, bar);
        center.setPadding(new Insets(14, 16, 14, 16));
        return center;
    }

    private void rebuildCards(HBox cardPane, Stage owner) {
        cardPane.getChildren().clear();
        playlists.setAll(libraryService != null
                ? libraryService.getPlaylists()
                : java.util.Collections.emptyList());

        for (Playlist p : playlists) {
            cardPane.getChildren().add(buildCard(p, owner, cardPane));
        }

        if (playlists.isEmpty()) {
            Label empty = new Label("No playlists yet. Click ＋ to create one.");
            empty.setStyle("-fx-text-fill:" + MUTED + ";");
            cardPane.getChildren().add(empty);
        }
    }

    private VBox buildCard(Playlist p, Stage owner, HBox cardPane) {
        Label lbl = new Label(p.getName());
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(104);
        lbl.setStyle("-fx-text-alignment:center;-fx-text-fill:" + TEXT + ";");

        Label sub = new Label(p.getSongs().size() + " songs");
        sub.setFont(Font.font("Arial", 9));
        sub.setAlignment(Pos.CENTER);
        sub.setMaxWidth(104);
        sub.setStyle("-fx-text-alignment:center;-fx-text-fill:" + MUTED + ";");

        VBox card = new VBox(6, buildCassette(104, 70), lbl, sub);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(8, 6, 8, 6));
        card.setCursor(Cursor.HAND);

        String sOff = "-fx-background-color:transparent;-fx-border-color:transparent;-fx-border-radius:8;-fx-background-radius:8;";
        String sOn = "-fx-background-color:rgba(0,0,0,0.04);-fx-border-color:#dddddd;-fx-border-radius:8;-fx-background-radius:8;";
        card.setStyle(sOff);
        card.setOnMouseEntered(e -> card.setStyle(sOn));
        card.setOnMouseExited(e -> card.setStyle(sOff));
        card.setOnMouseClicked(e -> {
            openDetailWindow(owner, p);
            // refresh subtitle after adding/removing songs
            sub.setText(p.getSongs().size() + " songs");
        });
        return card;
    }

    // ── cassette drawing ──────────────────────────────────────
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

        double cy = wy + wh * 0.52, r = wh * 0.36;
        double lx = wx + ww * 0.25, rx = wx + ww * 0.75;

        Circle reelL = reel(lx, cy, r), reelR = reel(rx, cy, r);
        Circle hubL = hub(lx, cy, r * 0.36), hubR = hub(rx, cy, r * 0.36);

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

    // ── Create dialog ─────────────────────────────────────────
    private void openCreateDialog(Stage owner) {
        Stage dlg = makeDialog(owner, "Create Playlist");
        TextField tfTitle = inputField("Playlist Name");
        TextField tfDesc = inputField("Description (optional)");
        Button btnOk = flatBtn("Create"), btnCancel = flatBtn("Cancel");

        btnCancel.setOnAction(e -> dlg.close());
        btnOk.setOnAction(e -> {
            String name = tfTitle.getText().trim();
            if (name.isEmpty()) {
                alert("Please enter a playlist name.");
                return;
            }
            if (libraryService != null) {
                libraryService.createPlaylist(name, tfDesc.getText().trim());
            }
            dlg.close();
        });

        VBox body = vstack(lbl("Name:"), tfTitle, lbl("Description:"), tfDesc);
        body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Create Playlist", body, footer(btnOk, btnCancel)), 300, 210));
        dlg.showAndWait();
    }

    // ── Detail window (songs inside a playlist) ───────────────
    private void openDetailWindow(Stage owner, Playlist playlist) {
        Stage win = makeDialog(owner, playlist.getName());

        ObservableList<SongRow> songRows = FXCollections.observableArrayList();
        for (Song s : playlist.getSongs()) {
            songRows.add(new SongRow(s.getId(), s.getTitle(), s.getArtist(), s.getFilePathMp3()));
        }

        TableView<SongRow> table = buildSongTable(win, playlist, songRows);

        // ── "Add Song" opens MySong picker ────────────────────
        Button btnAdd = flatBtn("＋  Add Song");
        Button btnEdit = flatBtn("✎  Edit Playlist");
        Button btnDelete = flatBtn("🗑  Delete Playlist");
        btnDelete.setStyle("-fx-background-color:transparent;-fx-border-color:transparent;"
                + "-fx-font-weight:bold;-fx-text-fill:#cc3333;-fx-cursor:hand;");

        btnAdd.setOnAction(e -> openAddSongPicker(win, playlist, songRows));

        btnEdit.setOnAction(e -> {
            openEditDialog(win, playlist);
            // refresh window title
            win.setTitle(playlist.getName());
        });

        btnDelete.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete playlist \"" + playlist.getName() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            a.initOwner(win);
            a.setHeaderText(null);
            a.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                if (libraryService != null) {
                    libraryService.deletePlaylist(playlist.getId());
                }
                win.close();
            });
        });

        HBox foot = new HBox(8, btnAdd, btnEdit, btnDelete);
        foot.setPadding(new Insets(8, 10, 10, 10));
        foot.setStyle("-fx-background-color:" + BG + ";");

        VBox center = new VBox(0, table, foot);
        center.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(buildSubBar(playlist.getName()));
        root.setCenter(center);

        win.setScene(new Scene(root, 500, 320));
        win.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private TableView<SongRow> buildSongTable(Stage owner, Playlist playlist, ObservableList<SongRow> rows) {
        TableView<SongRow> table = new TableView<>(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(210);
        table.setStyle("-fx-background-color:" + PANEL + ";-fx-border-color:" + BORDER + ";");

        TableColumn<SongRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(d -> d.getValue().titleProperty());

        TableColumn<SongRow, String> artistCol = new TableColumn<>("Artist");
        artistCol.setCellValueFactory(d -> d.getValue().artistProperty());

        TableColumn<SongRow, Void> actCol = new TableColumn<>("");
        actCol.setMinWidth(50);
        actCol.setMaxWidth(50);
        actCol.setCellFactory(c -> new TableCell<>() {
            private final Button b = flatBtn("✕");

            {
                b.setOnAction(e -> {
                    SongRow row = getTableView().getItems().get(getIndex());
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                            "Remove \"" + row.titleProperty().get() + "\" from playlist?",
                            ButtonType.YES, ButtonType.NO);
                    a.initOwner(owner);
                    a.setHeaderText(null);
                    a.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                        if (libraryService != null) {
                            libraryService.removeSongFromPlaylist(playlist.getId(), row.getId());
                        }
                        rows.remove(row);
                    });
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : b);
            }
        });

        table.getColumns().addAll(titleCol, artistCol, actCol);
        return table;
    }

    // ── Add Song picker (lists MySong library) ────────────────
    private void openAddSongPicker(Stage owner, Playlist playlist, ObservableList<SongRow> rows) {
        if (libraryService == null) {
            alert("LibraryService not available.");
            return;
        }

        Stage dlg = makeDialog(owner, "Add Song to Playlist");

        ObservableList<SongRow> available = FXCollections.observableArrayList();
        for (Song s : libraryService.getLibrary().getMySongs()) {
            // skip songs already in playlist
            boolean alreadyIn = playlist.getSongs().stream()
                    .anyMatch(ps -> ps.getId().equals(s.getId()));
            if (!alreadyIn) {
                available.add(new SongRow(s.getId(), s.getTitle(), s.getArtist(), s.getFilePathMp3()));
            }
        }

        if (available.isEmpty()) {
            alert("No songs available. Add songs in MySong first.");
            return;
        }

        TableView<SongRow> picker = new TableView<>(available);
        picker.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        picker.setPrefHeight(200);

        TableColumn<SongRow, String> tCol = new TableColumn<>("Title");
        tCol.setCellValueFactory(d -> d.getValue().titleProperty());

        TableColumn<SongRow, String> aCol = new TableColumn<>("Artist");
        aCol.setCellValueFactory(d -> d.getValue().artistProperty());

        picker.getColumns().addAll(tCol, aCol);

        Button btnAdd = flatBtn("Add Selected");
        Button btnCancel = flatBtn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());

        btnAdd.setOnAction(e -> {
            SongRow selected = picker.getSelectionModel().getSelectedItem();
            if (selected == null) {
                alert("Please select a song.");
                return;
            }

            boolean added = libraryService.addSongToPlaylist(playlist.getId(), selected.getId());
            if (added) {
                rows.add(selected);
                available.remove(selected);
            }
        });

        VBox body = new VBox(8, new Label("Select a song from your library:"), picker);
        body.setPadding(new Insets(14));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(buildSubBar("Add Song to: " + playlist.getName()));
        root.setCenter(body);
        root.setBottom(footer(btnAdd, btnCancel));

        dlg.setScene(new Scene(root, 400, 320));
        dlg.showAndWait();
    }

    // ── Edit playlist name/desc ───────────────────────────────
    private void openEditDialog(Stage owner, Playlist playlist) {
        Stage dlg = makeDialog(owner, "Edit Playlist");
        TextField tfName = inputField(playlist.getName());
        tfName.setText(playlist.getName());
        TextField tfDesc = inputField(playlist.getDescription());
        tfDesc.setText(playlist.getDescription());

        Button btnSave = flatBtn("Save"), btnCancel = flatBtn("Cancel");
        btnCancel.setOnAction(e -> dlg.close());
        btnSave.setOnAction(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                alert("Playlist name cannot be empty.");
                return;
            }
            if (libraryService != null) {
                libraryService.updatePlaylist(playlist.getId(), name, tfDesc.getText().trim());
            }
            dlg.close();
        });

        VBox body = vstack(lbl("Name:"), tfName, lbl("Description:"), tfDesc);
        body.setPadding(new Insets(14));
        dlg.setScene(new Scene(dialogPane("Edit Playlist", body, footer(btnSave, btnCancel)), 300, 210));
        dlg.showAndWait();
    }

    // ── UI helpers ────────────────────────────────────────────
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
        String on = "-fx-background-color:rgba(0,0,0,0.08);-fx-border-color:transparent;-fx-font-weight:bold;-fx-text-fill:#111111;";
        b.setStyle(off);
        b.setOnMouseEntered(e -> b.setStyle(on));
        b.setOnMouseExited(e -> b.setStyle(off));
        return b;
    }

    private TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(200);
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

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
