package club.charliefeng.kafkademoappengine.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TopicRequest {

    private String name;
    private int partitionNumber;
    private short replicationFactor;

    public TopicRequest() {}

    public TopicRequest(String name, int partitionNumber, short replicationFactor) {
        this.name = name;
        this.partitionNumber = partitionNumber;
        this.replicationFactor = replicationFactor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartitionNumber() {
        return partitionNumber;
    }

    public void setPartitionNumber(int partitionNumber) {
        this.partitionNumber = partitionNumber;
    }

    public short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
}
