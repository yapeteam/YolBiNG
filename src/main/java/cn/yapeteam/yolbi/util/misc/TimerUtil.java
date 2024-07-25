package cn.yapeteam.yolbi.util.misc;

public class TimerUtil {

    private long lastTime;

    public TimerUtil() {
        reset();
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - lastTime;
    }

    public void setTimeElapsed(long time) {
        this.lastTime = System.currentTimeMillis() - time;
    }

    public boolean delay(long time) {
        return getTimeElapsed() >= time;
    }

    public void reset() {
        lastTime = System.currentTimeMillis();
    }

}