package com.elab.data.dts.common;

import java.io.Closeable;


public class WorkThread<T extends Runnable & Closeable> {
    private final T r;
    private final Thread worker;

    public WorkThread(T r) {
        this.r = r;
        worker = new Thread(r);
    }

    public void start() {
        worker.start();
    }

    public void stop() {
        Util.swallowErrorClose(r);
        try {
            worker.join(10000, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}