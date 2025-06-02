package pt.isec.pa.chess.ui.res;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SoundManager {
    private SoundManager() { }
    private static boolean muted = false;
    private static MediaPlayer mp;
    private static final Queue<String> queue = new LinkedList<>();

    public static void playSequence(List<String> files) {
        if (muted)
            return;

        queue.clear();
        queue.addAll(files);
        playNext();
    }

    private static void playNext() {
        String f = queue.poll();
        if (f == null) return;

        try {
            URL url = SoundManager.class.getResource("/sounds/" + f);
            if (url == null) { playNext(); return; }

            Media m = new Media(url.toExternalForm());
            if (mp != null) mp.stop();

            mp = new MediaPlayer(m);
            mp.setStartTime(Duration.ZERO);
            mp.setStopTime(m.getDuration());
            mp.setAutoPlay(true);
            mp.setMute(false);
            mp.setOnEndOfMedia(SoundManager::playNext);
        } catch (Exception e) {
            playNext();
        }
    }

    public static void setMuted(boolean value) {
        muted = value;
        if (mp != null) // está a tocar ou o playSequence nunca foi chamado
            mp.setMute(muted);
        if (muted) stop();
    }

    public static void stop() {
        queue.clear();
        if (mp != null) mp.stop();
    }
}
