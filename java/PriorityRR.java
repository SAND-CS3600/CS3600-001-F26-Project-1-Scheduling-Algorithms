import java.util.*;

public class PriorityRR implements Algorithm {

    // List of tasks waiting to be scheduled
    private List<Task> queue;

    private int currentIndex;

    // Store scheduling data for calculating performance metrics
    private List<Integer> originalBurstTime = new ArrayList<>();
    private List<Integer> responseTime = new ArrayList<>();
    private List<Integer> turnaroundTime = new ArrayList<>();

    public PriorityRR(List<Task> queue) {
        this.queue = queue;
        this.currentIndex = 0;

        // Save each task's original burst before scheduling modifies it.
        // Response and turnaround times are null until they are recorded.
        for (int i = 0; i < queue.size(); i++) {
            originalBurstTime.add(queue.get(i).getBurst());
            responseTime.add(null);
            turnaroundTime.add(null);
        }
    }

    @Override
    public void schedule() {

        Task task = pickNextTask();
        int runtime;
        int currentTime = 0;
        float averageResponseTime;
        float averageTurnaroundTime;
        float averageWaitingTime;
        int totalResponseTimes = 0;
        int totalTurnaroundTimes = 0;
        int totalWaitingTimes = 0;

        // Calculate totals after every task has completed
        for (int i = 0; i < responseTime.size(); i++) {
            totalResponseTimes += responseTime.get(i);
            totalTurnaroundTimes += turnaroundTime.get(i);

            // Waiting time = turnaround time - original CPU burst
            totalWaitingTimes += turnaroundTime.get(i) - originalBurstTime.get(i);
        }

        // Calculate average scheduling performance
        averageTurnaroundTime = (float) totalTurnaroundTimes / turnaroundTime.size();
        averageWaitingTime = (float) totalWaitingTimes / turnaroundTime.size();
        averageResponseTime = (float) totalResponseTimes / responseTime.size();

        System.out.println("The average turnaround time is " + averageTurnaroundTime);
        System.out.println("The average waiting time is " + averageWaitingTime);
        System.out.println("The average response time is " + averageResponseTime);
    }

    @Override
    public Task pickNextTask() {
        // Return null after all tasks have completed
        if (queue.isEmpty()) {
            return null;
        }

        return queue.get(currentIndex);
    }
}