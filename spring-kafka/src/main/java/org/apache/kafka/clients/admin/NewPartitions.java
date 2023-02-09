package org.apache.kafka.clients.admin;

import java.util.List;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;

@Evolving
public class NewPartitions {
    private int totalCount;

    private List<List<Integer>> newAssignments;

    private NewPartitions(int totalCount, List<List<Integer>> newAssignments) {
        this.totalCount = totalCount;
        this.newAssignments = newAssignments;
    }

    public static NewPartitions increaseTo(int totalCount) {
        return new NewPartitions(totalCount, null);
    }

    public static NewPartitions increaseTo(int totalCount, List<List<Integer>> newAssignments) {
        return new NewPartitions(totalCount, newAssignments);
    }

    public int totalCount() {
        return this.totalCount;
    }

    public List<List<Integer>> assignments() {
        return this.newAssignments;
    }

    public String toString() {
        return "(totalCount=" + totalCount() + ", newAssignments=" + assignments() + ")";
    }
}
