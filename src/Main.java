import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main extends JFrame {

    private MusicPlayer player = new MusicPlayer();
    private Playlist playlist = new Playlist();

    private JLabel songName = new JLabel("Song Name");
    private JLabel artist = new JLabel("Artist");

    public Main() {
        setTitle("FungJai");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP =====
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("FungJai");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        topPanel.add(title, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER =====
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(songName, BorderLayout.WEST);
        infoPanel.add(artist, BorderLayout.EAST);

        WavePanel wave = new WavePanel();
        wave.setPreferredSize(new Dimension(380, 80));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton playBtn = new JButton("▶");
        JButton pauseBtn = new JButton("⏸");
        JButton stopBtn = new JButton("⟲");

        // ▶ เล่นจากโฟลเดอร์ music/
        playBtn.addActionListener(e -> {
            if (playlist.isEmpty()) return;

            Song current = playlist.getCurrentSong();
            player.load(new File(current.getPath()));
            player.play();
            player.setOnSpectrum(wave::updateSpectrum);

            songName.setText(current.getTitle());
            artist.setText(current.getArtist());

            player.setOnFinish(() -> {
                Song next = playlist.next();
                player.load(new File(next.getPath()));
                player.play();
                songName.setText(next.getTitle());
            });
        });

        pauseBtn.addActionListener(e -> player.pause());
        stopBtn.addActionListener(e -> player.stop());

        controls.add(stopBtn);
        controls.add(pauseBtn);
        controls.add(playBtn);

        center.add(infoPanel);
        center.add(Box.createVerticalStrut(20));
        center.add(wave);
        center.add(Box.createVerticalStrut(20));
        center.add(controls);

        add(center, BorderLayout.CENTER);

        // 🔥 โหลดเพลงอัตโนมัติ
        loadMusicFromFolder();
    }

    // โหลดไฟล์ .wav จากโฟลเดอร์ music/
    private void loadMusicFromFolder() {
        File folder = new File("assets/music");

        if (!folder.exists()) return;

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3"));
        if (files == null) return;

        for (File f : files) {
            playlist.addSong(new Song(f.getName(), "Unknown Artist", f.getPath()));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}