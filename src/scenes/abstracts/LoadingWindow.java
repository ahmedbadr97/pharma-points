package scenes.abstracts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.Main;
import scenes.main.WindowAbstract;

import java.util.concurrent.locks.ReentrantLock;

public class LoadingWindow extends WindowAbstract<scenes.controller.LoadingWindow> {

    private boolean processCanceled;
    ReentrantLock lock;
    public LoadingWindow(String title) {
        load("LoadingWindow", 380, 170);
        processCanceled = false;
        lock = new ReentrantLock();
        addOnCloseAction(()->setProcessCanceled(true));
        getController().ini(title);

    }

    public void startProcess(Runnable processWork)
    {
        Main.mainThreadsPool.execute(processWork);
    }

    public boolean isProcessCanceled() {
        return processCanceled;
    }

    public void setProcessCanceled(boolean processCanceled) {
        lock.lock();
        this.processCanceled = processCanceled;
        lock.unlock();
    }
}
