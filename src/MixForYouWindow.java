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

    // ── theme (matches PlaylistWindow) ────────────────────────
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

    // ── state ─────────────────────────────────────────────────
    private final HomeWindow homeWindow;
    private final LibraryService libraryService;
    private ObservableList<Playlist> mixes;

    public MixForYouWindow(HomeWindow homeWindow, LibraryService libraryService) {
        this.homeWindow     = homeWindow;
        this.libraryService = libraryService;
        this.mixes = FXCollections.observableArrayList(
                libraryService.getLibrary().getMixForYou()
        );
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
        bar.setStyle("-fx-background-color:#eeeeee;"
                + "-fx-border-color:#cccccc;"
                + "-fx-border-width:0 0 1 0;");
        return bar;
    }

    // ── center ────────────────────────────────────────────────
    private VBox buildCenter(Stage stage) {
        HBox cardPane = new HBox(12);
        cardPane.setAlignment(Pos.CENTER_LEFT);
        cardPane.setPadding(new Insets(16, 20, 16, 20));
        cardPane.setStyle("-fx-background-color:" + PANEL + ";"
                + "-fx-border-color:" + BORDER + ";"
                + "-fx-border-width:1;"
                + "-fx-border-radius:8;"
                + "-fx-background-radius:8;");

        rebuildCards(cardPane, stage);

        ScrollPane scroll = new ScrollPane(cardPane);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        // ── Generate button ────────────────────────────────────
        Button btnGenerate = new Button("✨  Generate Mix");
        String accentStyle = "-fx-background-color:" + ACCENT + ";"
                + "-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;"
                + "-fx-border-radius:4;-fx-background-radius:4;-fx-padding:5 12 5 12;";
        String accentHover = "-fx-background-color:#355f8a;"
                + "-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;"
                + "-fx-border-radius:4;-fx-background-radius:4;-fx-padding:5 12 5 12;";
        btnGenerate.setStyle(accentStyle);
        btnGenerate.setOnMouseEntered(e -> btnGenerate.setStyle(accentHover));
        btnGenerate.setOnMouseExited(e  -> btnGenerate.setStyle(accentStyle));
        btnGenerate.setOnAction(e -> {
            generateMix();
            rebuildCards(cardPane, stage);
        });

        // ── Clear All button ───────────────────────────────────
        Button btnClear = flatBtn("🗑  Clear All");
        btnClear.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Clear all mixes?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                libraryService.getLibrary().clearMixes();
                mixes.clear();
                rebuildCards(cardPane, stage);
            });
        });

        HBox bar = new HBox(8, btnGenerate, btnClear);
        bar.setPadding(new Insets(10, 4, 4, 4));

        VBox center = new VBox(8, scroll, bar);
        center.setPadding(new Insets(14, 16, 14, 16));
        return center;
    }

    // ── rebuild card row ──────────────────────────────────────
    private void rebuildCards(HBox cardPane, Stage stage) {
        cardPane.getChildren().clear();
        mixes.setAll(libraryService.getLibrary().getMixForYou());

        if (mixes.isEmpty()) {
            Label empty = new Label("No mixes yet. Click ✨ to generate one.");
            empty.setStyle("-fx-text-fill:" + MUTED + ";");
            cardPane.getChildren().add(empty);
            return;
        }

        int idx = 0;
        for (Playlist mix : mixes) {
            String[] palette = PALETTES[idx % PALETTES.length];
            cardPane.getChildren().add(buildCard(mix, palette, stage, cardPane));
            idx++;
        }
    }

    // ── single card ───────────────────────────────────────────
    private VBox buildCard(Playlist mix, String[] palette, Stage stage, HBox cardPane) {
        Label lbl = new Label(mix.getName());
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(104);
        lbl.setStyle("-fx-text-alignment:center;-fx-text-fill:" + TEXT + ";");

        Label sub = new Label(mix.getSongs().size() + " songs");
        sub.setFont(Font.font("Arial", 9));
        sub.setAlignment(Pos.CENTER);
        sub.setMaxWidth(104);
        sub.setStyle("-fx-text-alignment:center;-fx-text-fill:" + MUTED + ";");

        VBox card = new VBox(6, buildVinyl(104, 70, palette), lbl, sub);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(8, 6, 8, 6));
        card.setCursor(Cursor.HAND);

        String sOff = "-fx-background-color:transparent;-fx-border-color:transparent;"
                + "-fx-border-radius:8;-fx-background-radius:8;";
        String sOn  = "-fx-background-color:rgba(0,0,0,0.04);-fx-border-color:#dddddd;"
                + "-fx-border-radius:8;-fx-background-radius:8;";
        card.setStyle(sOff);
        card.setOnMouseEntered(e -> card.setStyle(sOn));
        card.setOnMouseExited(e  -> card.setStyle(sOff));
        card.setOnMouseClicked(e -> {
            openDetailWindow(stage, mix, palette);
            sub.setText(mix.getSongs().size() + " songs");
        });

        return card;
    }

    // ── vinyl record drawing ──────────────────────────────────
    private Pane buildVinyl(double w, double h, String[] palette) {
        Pane p = new Pane();
        p.setPrefSize(w, h);

        double cx = w / 2, cy = h / 2;
        double outerR = Math.min(w, h) * 0.46;

        Circle record = new Circle(cx, cy, outerR);
        record.setFill(Color.web(palette[0]));
        p.getChildren().add(record);

        for (int i = 3; i >= 1; i--) {
            Circle groove = new Circle(cx, cy, outerR * (0.55 + i * 0.13));
            groove.setFill(Color.TRANSPARENT);
            groove.setStroke(Color.web(palette[1], 0.6));
            groove.setStrokeWidth(1);
            p.getChildren().add(groove);
        }

        Circle label = new Circle(cx, cy, outerR * 0.38);
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

        p.getChildren().addAll(label, labelRing, hole, shine);
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
        int count = Math.min(10, shuffled.size());

        String[] moodNames = {
            "Late Night Vibes", "Morning Boost", "Chill Wave",
            "Energy Rush", "Focus Mode", "Sunset Drive",
            "Rainy Day", "Happy Hour", "Deep Focus", "Road Trip"
        };

        int mixNum = libraryService.getLibrary().getMixForYou().size() + 1;
        String name = moodNames[(mixNum - 1) % moodNames.length];

        Playlist mix = new Playlist(name, "Auto-generated mix #" + mixNum);
        for (int i = 0; i < count; i++) {
            mix.addSong(shuffled.get(i));
        }

        libraryService.getLibrary().addMix(mix);
    }

    // ── detail window ─────────────────────────────────────────
    private void openDetailWindow(Stage owner, Playlist mix, String[] palette) {
        Stage win = new Stage();
        win.initModality(Modality.WINDOW_MODAL);
        win.initOwner(owner);
        win.setTitle(mix.getName());
        win.setResizable(false);

        ObservableList<SongRow> rows = FXCollections.observableArrayList();
        for (Song s : mix.getSongs()) {
            rows.add(new SongRow(s.getId(), s.getTitle(), s.getArtist(), s.getFilePathMp3()));
        }

        // ── table ──────────────────────────────────────────────
        TableView<SongRow> table = new TableView<>(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(210);
        table.setStyle("-fx-background-color:" + PANEL + ";-fx-border-color:" + BORDER + ";");

        TableColumn<SongRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(d -> d.getValue().titleProperty());

        TableColumn<SongRow, String> artistCol = new TableColumn<>("Artist");
        artistCol.setCellValueFactory(d -> d.getValue().artistProperty());

        table.getColumns().addAll(titleCol, artistCol);

        // double-click → เล่นเพลงที่เลือก queue = mix นี้
        table.setRowFactory(tv -> {
            TableRow<SongRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    SongRow r = row.getItem();
                    Song song = mix.getSongs().stream()
                            .filter(s -> s.getId().equals(r.getId()))
                            .findFirst().orElse(null);
                    if (song != null && homeWindow != null) {
                        homeWindow.getPlayerService().playLibrary(mix.getSongs(), song);
                        homeWindow.setSongInfo(song, mix);
                    }
                }
            });
            return row;
        });

        // ── Play All ───────────────────────────────────────────
        Button btnPlayAll = new Button("▶  Play All");
        String accentStyle = "-fx-background-color:" + ACCENT + ";"
                + "-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;"
                + "-fx-border-radius:4;-fx-background-radius:4;-fx-padding:5 12 5 12;";
        String accentHover = "-fx-background-color:#355f8a;"
                + "-fx-text-fill:white;-fx-font-weight:bold;-fx-cursor:hand;"
                + "-fx-border-radius:4;-fx-background-radius:4;-fx-padding:5 12 5 12;";
        btnPlayAll.setStyle(accentStyle);
        btnPlayAll.setOnMouseEntered(e -> btnPlayAll.setStyle(accentHover));
        btnPlayAll.setOnMouseExited(e  -> btnPlayAll.setStyle(accentStyle));
        btnPlayAll.setOnAction(e -> {
            if (!mix.getSongs().isEmpty() && homeWindow != null) {
                Song first = mix.getSongs().get(0);
                homeWindow.getPlayerService().playLibrary(mix.getSongs(), first);
                homeWindow.setSongInfo(first, mix);
                win.close();
            }
        });

        // ── Delete Mix ─────────────────────────────────────────
        Button btnDelete = flatBtn("🗑  Delete Mix");
        btnDelete.setStyle("-fx-background-color:transparent;-fx-border-color:transparent;"
                + "-fx-font-weight:bold;-fx-text-fill:#cc3333;-fx-cursor:hand;");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete mix \"" + mix.getName() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.initOwner(win);
            confirm.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                libraryService.getLibrary().clearMixes(); // ลบทั้งหมดแล้ว re-add ยกเว้นตัวนี้
                // วิธีที่ถูกต้อง: Library.getMixForYou() เป็น unmodifiableList
                // ต้อง re-generate จาก list ที่กรองแล้ว — ใช้ findMix + addMix ไม่ได้ลบ
                // จึงใช้วิธี clearMixes แล้ว add กลับทุกตัวยกเว้นตัวที่ลบ
                List<Playlist> remaining = new ArrayList<>(mixes);
                remaining.removeIf(m -> m.getId().equals(mix.getId()));
                for (Playlist keep : remaining) {
                    libraryService.getLibrary().addMix(keep);
                }
                win.close();
            });
        });

        HBox foot = new HBox(8, btnPlayAll, btnDelete);
        foot.setPadding(new Insets(8, 10, 10, 10));
        foot.setStyle("-fx-background-color:" + BG + ";");

        // ── header ─────────────────────────────────────────────
        Pane vinyl = buildVinyl(60, 60, palette);
        Label mixName = new Label(mix.getName());
        mixName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        mixName.setStyle("-fx-text-fill:" + TEXT + ";");
        Label mixDesc = new Label(mix.getDescription() + "  •  " + mix.getSongs().size() + " songs");
        mixDesc.setStyle("-fx-text-fill:" + MUTED + ";-fx-font-size:11px;");
        VBox nameBox = new VBox(3, mixName, mixDesc);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        HBox header = new HBox(12, vinyl, nameBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 14, 10, 14));
        header.setStyle("-fx-background-color:#eeeeee;"
                + "-fx-border-color:#cccccc;"
                + "-fx-border-width:0 0 1 0;");

        VBox center = new VBox(0, table, foot);
        center.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");
        root.setTop(header);
        root.setCenter(center);

        win.setScene(new Scene(root, 500, 340));
        win.showAndWait();
    }

    // ── helpers ───────────────────────────────────────────────
    private Button flatBtn(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        String off = "-fx-background-color:transparent;-fx-border-color:transparent;"
                + "-fx-font-weight:bold;-fx-text-fill:#444444;-fx-cursor:hand;";
        String on  = "-fx-background-color:rgba(0,0,0,0.08);-fx-border-color:transparent;"
                + "-fx-font-weight:bold;-fx-text-fill:#111111;";
        b.setStyle(off);
        b.setOnMouseEntered(e -> b.setStyle(on));
        b.setOnMouseExited(e  -> b.setStyle(off));
        return b;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}