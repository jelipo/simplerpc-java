package com.springmarker.simplerpc.core.client;

import com.springmarker.simplerpc.enums.FailedType;

import java.util.concurrent.TimeUnit;

/**
 * 具有 同步等待执行完成的Callback Class。
 *
 * @author: Springmarker
 * @date: 2019/3/2 1:30
 */
public abstract class SyncCallback {

    /**
     * RPC远程调用成功后的结果。
     */
    private Object result = null;

    private final PrivateLockFlag privateLockFlag = new PrivateLockFlag(false);


    /**
     * 成功远程调用完毕的时候 会执行此方法。
     *
     * @param object
     */
    public abstract void onSuccess(Object object);

    /**
     * 当 RPC远程调用出错/调用成功但是远程方法报错 的时候 会执行此方法。
     *
     * @param throwable
     * @param type
     */
    public abstract void onFailed(Throwable throwable, FailedType type);

    /**
     * 给RPC内部所调用的方法，当RPC调用成功时给内部调用的方法。
     *
     * @param object RPC调用的结果
     */
    protected void successFinished(Object object) {
        this.result = object;
        this.onSuccess(object);
        finished();
    }

    protected void failedFinished(Throwable throwable, FailedType type) {
        this.onFailed(throwable, type);
        finished();
    }

    /**
     * 回调后最后被执行的方法，主要用于放开锁
     */
    private void finished() {
        if (privateLockFlag.isFinished()) {
            return;
        }
        synchronized (privateLockFlag) {
            privateLockFlag.notifyAll();
            privateLockFlag.setFinished(true);
        }
    }

    /**
     * @param timeOut  超时的时间
     * @param timeUnit
     * @return
     */
    public boolean waitingSuccess(int timeOut, TimeUnit timeUnit) {
        if (privateLockFlag.isFinished()) {
            return true;
        }
        synchronized (privateLockFlag) {
            if (privateLockFlag.isFinished()) {
                return true;
            }
            try {
                long l = timeUnit.toMillis(timeOut);
                if (l != 0) {
                    privateLockFlag.wait(timeUnit.toMillis(timeOut));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return privateLockFlag.isFinished();
        }

    }


    private class PrivateLockFlag {
        private boolean finished = false;

        PrivateLockFlag(boolean finished) {
            this.finished = finished;
        }

        boolean isFinished() {
            return finished;
        }

        void setFinished(boolean finished) {
            this.finished = finished;
        }
    }
}
