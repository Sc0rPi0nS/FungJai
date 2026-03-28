import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.util.*;

public class MixForYouWindow {

    // ── theme ─────────────────────────────────────────────────
    private static final String BG     = "#f5f5f5";
    private static final String PANEL  = "#ffffff";
    private static final String BORDER = "#e0e0e0";
    private static final String TEXT   = "#333333";
    private static final String MUTED  = "#888888";
    private static final String ACCENT = "#4773a1";

    // ── vinyl palettes ────────────────────────────────────────
    private static final String[][] PALETTES = {
        { "#1a1a2e", "#16213e", "#0f3460", "#e94560" },
        { "#2d2016", "#4a3520", "#7a5c38", "#f0c060" },
        { "#0d2b1a", "#1a4a2e", "#2d7a50", "#6ee0a0" },
        { "#2b1a3a", "#4a2060", "#7a3490", "#d060f0" },
        { "#1a2b2b", "#1e4040", "#2a6060", "#60d0d0" },
    };

    private final HomeWindow homeWindow;
    private final LibraryService libraryService;

    public MixForYouWindow(HomeWindow homeWindow, LibraryService libraryService) {
        this.homeWindow     = homeWindow;
        this.libraryService = libraryService;
    }

    // ── entry ─────────────────────────────────────────────────
    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.NONE);
        stage.setTitle("Mix For You");
        stage.setResizable(false);
        stage.setScene(new Scene(buildRoot(stage), 500, 360));
        stage.show();
    }

    // ── root ──────────────────────────────────────────────────
    private BorderPane buildRoot(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(buildTopBar());
        root.setCenter(buildCenter(stage));
        return root;
    }

    private HBox buildTopBar() {
        Label title = new Label("MIX FOR YOU");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill:" + TEXT + ";");
        HBox bar = new HBox(title);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10));
        bar.setStyle("-fx-background-color:#eeeeee;-fx-border-color:#cccccc;-fx-border-width:0 0 1 0;");
        return bar;
    }

    // ── center ────────────────────────────────────────────────
    private VBox buildCenter(Stage stage) {
        HBox cardPane = new HBox(12);
        cardPane.setAlignment(Pos.CENTER_LEFT);
        cardPane.setPadding(new Insets(16, 20, 16, 20));
        cardPane.setStyle("-fx-background-color:" + PANEL + ";"
                + "-fx-border-color:" + BORDER + ";"
                + "-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");

        refreshCards(cardPane, stage);

        ScrollPane scroll = new ScrollPane(cardPane);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        Button btnGenerate = accentBtn("✨  Generate Mix");
        btnGenerate.setOnAction(e -> {
            generateMix();
            refreshCards(cardPane, stage);
        });

        Button btnClear = flatBtn("🗑  Clear All");
        btnClear.setOnAction(e -> {
            if (confirm(stage, "Clear all mixes?")) {
                libraryService.getLibrary().clearMixes();
                refreshCards(cardPane, stage);
            }
        });

        HBox bar = new HBox(8, btnGenerate, btnClear);
        bar.setPadding(new Insets(10, 4, 4, 4));

        VBox center = new VBox(8, scroll, bar);
        center.setPadding(new Insets(14, 16, 14, 16));
        return center;
    }

    // ── rebuild card row ──────────────────────────────────────
    private void refreshCards(HBox cardPane, Stage stage) {
        cardPane.getChildren().clear();
        List<Playlist> mixes = libraryService.getLibrary().getMixForYou();

        if (mixes.isEmpty()) {
            Label empty = new Label("No mixes yet. Click ✨ to generate one.");
            empty.setStyle("-fx-text-fill:" + MUTED + ";");
            cardPane.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < mixes.size(); i++) {
            String[] palette = PALETTES[i % PALETTES.length];
            cardPane.getChildren().add(buildCard(mixes.get(i), palette, stage, cardPane));
        }
    }

    // ── single mix card ───────────────────────────────────────
    private VBox buildCard(Playlist mix, String[] palette, Stage stage, HBox cardPane) {
        Label nameLbl = new Label(mix.getName());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setMaxWidth(104);
        nameLbl.setStyle("-fx-text-alignment:center;-fx-text-fill:" + TEXT + ";");

        Label countLbl = new Label(mix.getSongs().size() + " songs");
        countLbl.setFont(Font.font("Arial", 9));
        countLbl.setAlignment(Pos.CENTER);
        countLbl.setMaxWidth(104);
        countLbl.setStyle("-fx-text-alignment:center;-fx-text-fill:" + MUTED + ";");

        VBox card = new VBox(6, buildVinyl(104, 70, palette), nameLbl, countLbl);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(8, 6, 8, 6));
        card.setCursor(javafx.scene.Cursor.HAND);

        String sOff = "-fx-background-color:transparent;-fx-border-color:transparent;-fx-border-radius:8;-fx-background-radius:8;";
        String sOn  = "-fx-background-color:rgba(0,0,0,0.04);-fx-border-color:#dddddd;-fx-border-radius:8;-fx-background-radius:8;";
        card.setStyle(sOff);
        card.setOnMouseEntered(e -> card.setStyle(sOn));
        card.setOnMouseExited(e  -> card.setStyle(sOff));

        card.setOnMouseClicked(e ->
            openDetailWindow(stage, mix, palette, () -> refreshCards(cardPane, stage))
        );

        return card;
    }

    // ── vinyl graphic ─────────────────────────────────────────
    private Pane buildVinyl(double w, double h, String[] palette) {
        Pane p = new Pane();
        p.setPrefSize(w, h);

        double cx = w / 2, cy = h / 2;
        double outerR = Math.min(w, h) * 0.46;

        Circle disc = new Circle(cx, cy, outerR);
        disc.setFill(Color.web(palette[0]));

        for (int i = 1; i <= 3; i++) {
            Circle groove = new Circle(cx, cy, outerR * (0.9 - i * 0.15));
            groove.setFill(Color.TRANSPARENT);
            groove.setStroke(Color.web(palette[1], 0.5));
            groove.setStrokeWidth(0.8);
            p.getChildren().add(groove);
        }

        Circle label = new Circle(cx, cy, outerR * 0.42);
        label.setFill(Color.web(palette[2]));

        Circle labelRing = new Circle(cx, cy, outerR * 0.40);
        labelRing.setFill(Color.TRANSPARENT);
        labelRing.setStroke(Color.web(palette[3], 0.8));
        labelRing.setStrokeWidth(1.5);

        Circle hole = new Circle(cx, cy, outerR * 0.07);
        hole.setFill(Color.web(palette[0]));

        Ellipse shine = new Ellipse(cx - outerR * 0.15, cy - outerR * 0.35,
                outerR * 0.3, outerR * 0.12);
        shine.setFill(Color.web("#ffffff", 0.10));

        p.getChildren().addAll(disc, label, labelRing, hole, shine);
        return p;
    }

    // ── generate a random mix ─────────────────────────────────
    private void generateMix() {
        List<Song> allSongs = libraryService.getLibrary().getMySongs();
        if (allSongs.isEmpty()) {
            alert("Add some songs in MySong first.");
            return;
        }

        List<Song> shuffled = new ArrayList<>(allSongs);
        Collections.shuffle(shuffled);
        int count  = Math.min(10, shuffled.size());
        int mixNum = libraryService.getLibrary().getMixForYou().size() + 1;

        Playlist mix = new Playlist(
                "Random Mix " + mixNum,
                "Auto-generated mix #" + mixNum
        );
        for (int i = 0; i < count; i++) mix.addSong(shuffled.get(i));

        libraryService.getLibrary().addMix(mix);
    }

    // ── detail window ─────────────────────────────────────────
    private void openDetailWindow(Stage owner, Playlist mix, String[] palette,
                                   Runnable refreshCallback) {
        Stage win = new Stage();
        win.initModality(Modality.WINDOW_MODAL);
        win.initOwner(owner);
        win.setTitle(mix.getName());
        win.setResizable(false);

        ObservableList<SongRow> rows = FXCollections.observableArrayList();
        for (Song s : mix.getSongs()) {
            rows.add(new SongRow(s.getId(), s.getTitle(), s.getArtist(), s.getFilePathMp3()));
        }

        TableView<SongRow> table = new TableView<>(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(210);
        table.setStyle("-fx-background-color:" + PANEL + ";-fx-border-color:" + BORDER + ";");

        TableColumn<SongRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(d -> d.getValue().titleProperty());
        TableColumn<SongRow, String> artistCol = new TableColumn<>("Artist");
        artistCol.setCellValueFactory(d -> d.getValue().artistProperty());
        table.getColumns().addAll(titleCol, artistCol);

        table.setRowFactory(tv -> {
            TableRow<SongRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Song song = mix.getSongs().stream()
                            .filter(s -> s.getId().equals(row.getItem().getId()))
                            .findFirst().orElse(null);
                    if (song != null && homeWindow != null) {
                        homeWindow.getPlayerService().playLibrary(mix.getSongs(), song);
                        homeWindow.setSongInfo(song, mix);
                    }
                }
            });
            return row;
        });

        Button btnPlayAll = accentBtn("▶  Play All");
        btnPlayAll.setOnAction(e -> {
            if (!mix.getSongs().isEmpty() && homeWindow != null) {
                Song first = mix.getSongs().get(0);
                homeWindow.getPlayerService().playLibrary(mix.getSongs(), first);
                homeWindow.setSongInfo(first, mix);
                win.close();
            }
        });

        Button btnDelete = dangerBtn("🗑  Delete Mix");
        btnDelete.setOnAction(e -> {
            if (confirm(win, "Delete mix \"" + mix.getName() + "\"?")) {
                List<Playlist> remaining = new ArrayList<>(libraryService.getLibrary().getMixForYou());
                remaining.removeIf(m -> m.getId().equals(mix.getId()));
                libraryService.getLibrary().clearMixes();
                remaining.forEach(libraryService.getLibrary()::addMix);
                refreshCallback.run();
                win.close();
            }
        });

        HBox foot = new HBox(8, btnPlayAll, btnDelete);
        foot.setPadding(new Insets(8, 10, 10, 10));
        foot.setStyle("-fx-background-color:" + BG + ";");

        Pane vinyl = buildVinyl(60, 60, palette);
        Label mixName = new Label(mix.getName());
        mixName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        mixName.setStyle("-fx-text-fill:" + TEXT + ";");
        Label mixDesc = new Label(mix.getDescription() + "  •  " + mix.getSongs().size() + " songs");
        mixDesc.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:11px;");
        HBox header = new HBox(12, vinyl, new VBox(3, mixName, mixDesc));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 14, 10, 14));
        header.setStyle("-fx-background-color:#eeeeee;-fx-border-color:#cccccc;-fx-border-width:0 0 1 0;");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(header);
        root.setCenter(new VBox(0, table, foot));

        win.setScene(new Scene(root, 500, 340));
        win.showAndWait();
    }

    // ── UI helpers ────────────────────────────────────────────
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

    private Button accentBtn(String text) {
        Button b = new Button(text);
        String off = "-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;-fx-border-radius:4;-fx-background-radius:4;-fx-padding:5 12 5 12;";
        String on  = "-fx-background-color:#355f8a;-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;-fx-border-radius:4;-fx-background-radius:4;-fx-padding:5 12 5 12;";
        b.setStyle(off);
        b.setOnMouseEntered(e -> b.setStyle(on));
        b.setOnMouseExited(e  -> b.setStyle(off));
        return b;
    }

    private Button dangerBtn(String text) {
        Button b = new Button(text);
        String off = "-fx-background-color:transparent;-fx-border-color:transparent;-fx-font-weight:bold;-fx-text-fill:#cc3333;-fx-cursor:hand;";
        String on  = "-fx-background-color:rgba(0,0,0,0.06);-fx-border-color:transparent;-fx-font-weight:bold;-fx-text-fill:#cc3333;";
        b.setStyle(off);
        b.setOnMouseEntered(e -> b.setStyle(on));
        b.setOnMouseExited(e  -> b.setStyle(off));
        return b;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.show();
    }

    private boolean confirm(Stage owner, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.initOwner(owner);
        a.setHeaderText(null);
        return a.showAndWait().filter(r -> r == ButtonType.YES).isPresent();
    }
}
