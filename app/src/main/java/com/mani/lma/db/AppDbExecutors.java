package com.mani.lma.db;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppDbExecutors {
    private static final Object LOCK = new Object();
    private static AppDbExecutors instance;
    private final Executor diskIo;

    public Executor getDiskIo() {
        return diskIo;
    }

    private final Executor mainThread;
    private final Executor networkIo;

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkIo() {
        return networkIo;
    }

    private AppDbExecutors(Executor diskIo, Executor mainThread, Executor networkIo) {
        this.diskIo = diskIo;
        this.mainThread = mainThread;
        this.networkIo = networkIo;
    }

    public static AppDbExecutors getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new AppDbExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return instance;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }
}

