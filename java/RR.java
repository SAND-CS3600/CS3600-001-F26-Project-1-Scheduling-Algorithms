import java.util.*;

public class RR implements Algorithm {

    private List<Task> queue;
    private int currentIndex;

    private List<Integer> originalBurstTime = new ArrayList<>();
    private List<Integer> responseTime = new ArrayList<>();
    private List<Integer> turnaroundTime = new ArrayList<>();

    public RR(List<Task> queue) {
        this.queue = queue;
        this.currentIndex = 0;

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

            runtime = task.getBurst();

            if (responseTime.get(task.getTid()) == null) {
                responseTime.set(task.getTid(), currentTime);
            }

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
                turnaroundTime.set(task.getTid(), currentTime);
                queue.remove(currentIndex);

                if (currentIndex == queue.size()) {
                    currentIndex = 0;
                }
            }
            else {
                currentIndex += 1;

                if (currentIndex == queue.size()) {
                    currentIndex = 0;
                }
            }

            task = pickNextTask();
        }

        for (int i = 0; i < responseTime.size(); i++) {
            totalResponseTimes += responseTime.get(i);
            totalTurnaroundTimes += turnaroundTime.get(i);
            totalWaitingTimes += turnaroundTime.get(i) - originalBurstTime.get(i);
        }

        averageTurnaroundTime = (float) totalTurnaroundTimes / turnaroundTime.size();
        averageWaitingTime = (float) totalWaitingTimes / turnaroundTime.size();
        averageResponseTime = (float) totalResponseTimes / responseTime.size();

        System.out.println("The average turnaround time is " + averageTurnaroundTime);
        System.out.println("The average waiting time is " + averageWaitingTime);
        System.out.println("The average response time is " + averageResponseTime);
    }

    @Override
    public Task pickNextTask() {
        // Selects the next task for the scheduler

        if (queue.isEmpty()) {
            return null;
        }

        return queue.get(currentIndex);
    }
}