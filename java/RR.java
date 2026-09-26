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

        Task task = pickNextTask();
        int runtime;

        while(task != null) {

            runtime = task.getBurst();

            if (runtime > 10) {
                CPU.run(task, 10);
                runtime -= 10;
                task.setBurst(runtime);
            }
            else {
                CPU.run(task, runtime);
                runtime = 0;
                task.setBurst(runtime);
            }

            if (runtime == 0) {
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