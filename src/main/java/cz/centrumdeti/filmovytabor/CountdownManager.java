package cz.centrumdeti.filmovytabor;

import java.util.Timer;
import java.util.TimerTask;

public abstract class CountdownManager {
    int remaining = 60;
    Timer timer;

    abstract void updateSecond(int current);
    abstract void timeIsUp();

    public CountdownManager() {
        timer = new Timer(true);
        timer.schedule(new CountdownTask(),1000,1000);
    }

    public void cancel() {
        timer.cancel();
        timer.purge();
    }

    public class CountdownTask extends TimerTask {
        @Override
        public void run() {
            if(--remaining < 0) {
                timeIsUp();
                CountdownManager.this.cancel();
            } else {
                updateSecond(remaining);
            }
        }
    }
}
