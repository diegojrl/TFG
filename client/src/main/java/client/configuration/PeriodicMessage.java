package client.configuration;

public class PeriodicMessage {
    private String topic;
    private String content;
    private Integer qos;
    private Integer interval;

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public Integer getInterval() {
        return interval;
    }

    public Integer getQos() {
        return qos;
    }

}
