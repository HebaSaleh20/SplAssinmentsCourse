package bguspl.set.ex;

import bguspl.set.Env;
/**
 /* timer countdown task
 */
public class Timer implements Runnable {
    public long currTime;
    public Env env;
    public Thread timerThread;

    Timer(Env env) {
        this.env = env;
        currTime = env.config.turnTimeoutMillis;
    }

    public void run() {
        timerThread=Thread.currentThread();
        while(currTime>=0) {
            if (currTime>env.config.turnTimeoutWarningMillis) {
                env.ui.setCountdown(currTime, false);
                currTime = currTime - 1000;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }else {
                env.ui.setCountdown(currTime, true);
                currTime = currTime - 10;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }
        }
        currTime=env.config.turnTimeoutMillis;

    }
}