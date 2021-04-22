package cn.edsmall.lib_section.bean;

public class TimeStatistics {
    private String stayClassName;  //停留的
    private Long stayTime;//停留页面的时间
    private long recordTime;//记录的时间

    public TimeStatistics() {
    }

    public String getStayClassName() {
        return stayClassName;
    }

    public void setStayClassName(String stayClassName) {
        this.stayClassName = stayClassName;
    }

    public Long getStayTime() {
        return stayTime;
    }

    public void setStayTime(Long stayTime) {
        this.stayTime = stayTime;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public TimeStatistics(String stayClassName, Long stayTime) {
        this.stayClassName = stayClassName;
        this.stayTime = stayTime;
    }

    @Override
    public String toString() {
        return "TimeStatistics{" +
                "停留界面='" + stayClassName + '\'' +
                ", 停留时间=" + stayTime +
                '}';
    }
}
