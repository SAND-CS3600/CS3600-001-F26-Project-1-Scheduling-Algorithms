import java.util.*;

public class PriorityRR implements Algorithm {

    // List of tasks waiting to be scheduled
    private List<Task> queue;

    // Index where the circular search begins for the next eligible task
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
        boolean hasSamePriority = false;
        int taskIndex;

        while (task != null) {
            // Save the selected task's actual queue index.
            // currentIndex may differ because pickNextTask() searches circularly.
            taskIndex = queue.indexOf(task);
        
            // Check whether another remaining task shares this priority.
            // If so, tasks at this priority must use Round Robin.
            for (Task t : queue) {
                if (t != task && t.getPriority() == task.getPriority()) {
                    hasSamePriority = true;
                    break;
                }    
            }

            // Get the task's remaining burst time
            runtime = task.getBurst();

            // Response time is the first time the task receives the CPU
            if (responseTime.get(task.getTid()) == null) {
                responseTime.set(task.getTid(), currentTime);
            }

            if (hasSamePriority == true) {

                // Run for one quantum, or the remaining burst if it is less than 10 ms
                if (runtime > 10) {
                    CPU.run(task, 10);
                    currentTime += 10;
                    runtime -= 10;
                    task.setBurst(runtime);
                }
                else {
                    // No other task shares this priority, so run the task to completion
                    CPU.run(task, runtime);
                    currentTime += runtime;
                    runtime = 0;
                    task.setBurst(runtime);
                }
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
                queue.remove(task);

                // After removal, the next task shifts into the completed task's old index     
                currentIndex = taskIndex;
            }
            else {
                // The task remains in the queue, so begin the next search after it
                currentIndex = taskIndex + 1;
            }

            // Wrap back to the beginning if currentIndex is past the end
            if (!queue.isEmpty() && currentIndex >= queue.size()) {
                currentIndex = 0;
            }

            // Select the next task using priority and the current Round Robin position
            task = pickNextTask();
            hasSamePriority = false;
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

        // Find the highest priority currently remaining in the queue
        int highest = 0;
        
        for (Task t : queue) {
            if (t.getPriority() > highest) {
                highest = t.getPriority();
            }
        }

        // Search circularly from currentIndex for the next task
        // belonging to the highest-priority group
        for (int i = 0; i < queue.size(); i++) {
            int index = (currentIndex + i) % queue.size();
            Task t = queue.get(index);
            if (t.getPriority() == highest) {
                return t;
            }
        }
        
        return null;
    }
}