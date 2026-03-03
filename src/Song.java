public class Song extends MediaItem implements Playable {

    private String filePathMp4;
    private int durationSec;

    // ===== Constructor =====
    public Song(String title, String artist,
                String filePathMp4,
                int durationSec) {

        super(title, artist);
        this.filePathMp4 = filePathMp4;
        this.durationSec = durationSec;
    }

    // ===== Getter / Setter =====
    public String getFilePathMp4() {
        return filePathMp4;
    }

    public void setFilePathMp4(String filePathMp4) {
        this.filePathMp4 = filePathMp4;
    }

    public void setDurationSec(int durationSec) {
        this.durationSec = durationSec;
    }

    // ===== Implement Playable =====
    @Override
    public void play() {
        System.out.println("Playing: " + getTitle());
    }

    @Override
    public void pause() {
        System.out.println("Paused: " + getTitle());
    }

    @Override
    public void stop() {
        System.out.println("Stopped: " + getTitle());
    }

    @Override
    public int getDurationSrc() {
        return durationSec;
    }
}