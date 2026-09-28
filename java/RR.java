import java.util.*;

public class RR implements Algorithm {

    // List of tasks waiting to be scheduled
    private List<Task> queue;

    // Index of the next task to run in the Round Robin queue
    private int currentIndex;

    // Store scheduling data for calculating performance metrics
    private List<Integer> originalBurstTime = new ArrayList<>();
    private List<Integer> responseTime = new ArrayList<>();
    private List<Integer> turnaroundTime = new ArrayList<>();

    public RR(List<Task> queue) {
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
        // Execute tasks using Round Robin scheduling with a 10 ms time quantum

        Task task = pickNextTask();
        int runtime;
        int currentTime = 0;
        float averageResponseTime;
        float averageTurnaroundTime;
        float averageWaitingTime;
        int totalResponseTimes = 0;
        int totalTurnaroundTimes = 0;
        int totalWaitingTimes = 0;

        while(task != null) {

            // Get the task's remaining burst time
            runtime = task.getBurst();

            // Response time is the first time the task receives the CPU
            if (responseTime.get(task.getTid()) == null) {
                responseTime.set(task.getTid(), currentTime);
            }

            // Run for one quantum, or the remaining burst if it is less than 10 ms
            if (runtime > 10) {
                CPU.run(task, 10);
                currentTime += 10;
                runtime -= 10;
                task.setBurst(runtime);
            }
            else {
                CPU.run(task, runtime);
                currentTime += runtime;
                runtime = 0;
                task.setBurst(runtime);
            }

            if (runtime == 0) {
                // A completed task's current time is its turnaround time
                turnaroundTime.set(task.getTid(), currentTime);
                queue.remove(currentIndex);

                // Wrap back to the beginning if the last task was removed
                if (currentIndex == queue.size()) {
                    currentIndex = 0;
                }
            }
            else {
                // Move to the next task if the current task is not finished
                currentIndex += 1;

                // Wrap back to the beginning of the queue
                if (currentIndex == queue.size()) {
                    currentIndex = 0;
                }
            }

            task = pickNextTask();
        }

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

        // Return the task at the current position in the Round Robin queue
        return queue.get(currentIndex);
    }
}