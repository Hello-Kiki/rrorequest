package com.hellokiki.rrorequest.down;

/**
 * Created by 黄麒羽 on 2017/12/20.
 * 下载的信息
 */

public class DownInfo {

    private long id;

    private long readLength;

    private long countLength;

    private String url;

    private String savePath;

    private HttpDownListener listener;

    private int state;

    private boolean isStartMoreThread=false;

    private int downThreadCount=3;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public long getCountLength() {
        return countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public HttpDownListener getListener() {
        return listener;
    }

    public void setListener(HttpDownListener listener) {
        this.listener = listener;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isStartMoreThread() {
        return isStartMoreThread;
    }

    public void setStartMoreThread(boolean startMoreThread) {
        isStartMoreThread = startMoreThread;
    }

    public int getDownThreadCount() {
        return downThreadCount;
    }

    public void setDownThreadCount(int downThreadCount) {
        this.downThreadCount = downThreadCount;
    }

    @Override
    public String toString() {
        return "DownInfo{" +
                "id=" + id +
                ", readLength=" + readLength +
                ", countLength=" + countLength +
                ", url='" + url + '\'' +
                ", savePath='" + savePath + '\'' +
                ", listener=" + listener +
                ", state=" + state +
                ", isStartMoreThread=" + isStartMoreThread +
                ", downThreadCount=" + downThreadCount +
                '}';
    }
}
