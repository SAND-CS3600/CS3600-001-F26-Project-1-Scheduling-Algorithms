import java.util.*;

public class RR implements Algorithm {

    private List<Task> queue;
    private int currentIndex;

    public RR(List<Task> queue) {
        this.queue = queue;
        this.currentIndex = 0;
    }

    @Override
    public void schedule() {
        // Round Robin scheduling logic will go here
    }

    @Override
    public Task pickNextTask() {
        // Task-selection logic will go here
        return null;
    }
}