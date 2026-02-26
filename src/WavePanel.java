import javax.swing.*;
import java.awt.*;

public class WavePanel extends JPanel {

    private float[] spectrum;

    public void updateSpectrum(float[] spectrum) {
        this.spectrum = spectrum.clone();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spectrum == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.GREEN);

        int width = getWidth();
        int height = getHeight();
        int bars = spectrum.length;
        int barWidth = Math.max(1, width / bars);

        for (int i = 0; i < bars; i++) {
            float value = spectrum[i];      // ค่าเสียงจริง
            int barHeight = (int) ((value + 60) * 2);

            barHeight = Math.min(barHeight, height);

            g2.fillRect(
                i * barWidth,
                height - barHeight,
                barWidth - 1,
                barHeight
            );
        }
    }
}