//package com.zjlab.dataservice.modules.storage;
//
//public class SpeedLimiterUtil {
//
//    /** 速度上限(KB/s), 0=不限速 */
//    private int maxRate = 1024;
//    private long getMaxRateBytes(){
//        return this.maxRate * 1024L;
//    }
//    private long getLessCountBytes() {
//        long lcb = getMaxRateBytes() / 10;
//        if (lcb < 10240) lcb = 10240;
//        return lcb;
//    }
//    public SpeedLimiter(int maxRate) {
//        this.setMaxRate(maxRate);
//    }
//    public synchronized void setMaxRate(int maxRate){
//        this.maxRate = Math.max(maxRate, 0);
//    }
//    private long totalBytes = 0;
//    private long tmpCountBytes = 0;
//    private final long lastTime = System.currentTimeMillis();
//    public synchronized void delayNextBytes(int len) {
//        if (maxRate <= 0) return;
//        totalBytes += len;
//        tmpCountBytes += len;
//        //未达到指定字节数跳过...
//        if (tmpCountBytes < getLessCountBytes()) {
//            return;
//        }
//        long nowTime = System.currentTimeMillis();
//        long sendTime = nowTime - lastTime;
//        long workTime = (totalBytes * 1000) / getMaxRateBytes();
//        long delayTime = workTime - sendTime;
//        if (delayTime > 0) {
//            try {
//                Thread.sleep(delayTime);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            tmpCountBytes = 0;
//        }
//    }
//}
