import java.util.*;

// SJF is pretty much FCFS but it picks the shortest burst instead of the front of the queue.
// most of this is reused from FCFS.java (which reused the metrics part from RR.java),
// the main thing we changed is pickNextTask() and which task gets removed
public class SJF implements Algorithm {

    //list of tasks waiting to be scheduled
    private List<Task> queue;

    //store scheduling data for calculating performance metrics (same as FCFS.java)
    private List<Integer> originalBurstTime = new ArrayList<>();
    private List<Integer> responseTime = new ArrayList<>();
    private List<Integer> turnaroundTime = new ArrayList<>();

    public SJF(List<Task> queue) {
        this.queue = queue;

        //save each tasks original burst before scheduling modifies it
        //response and turnaround times are null until they are recorded
        //(copied from FCFS.java)
        for (int i = 0; i < queue.size(); i++) {
            originalBurstTime.add(queue.get(i).getBurst());
            responseTime.add(null);
            turnaroundTime.add(null);
        }
    }

    @Override
    public void schedule() {
        //execute tasks from shortest burst to longest
        //this loop is the same as FCFS.java except for the remove at the bottom

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

            //get the tasks burst time
            runtime = task.getBurst();

            //response time is the first time the task gets the CPU
            responseTime.set(task.getTid(), currentTime);

            //SJF is non preemptive so the task runs for its whole burst
            CPU.run(task, runtime);
            currentTime += runtime;
            runtime = 0;
            task.setBurst(runtime);

            turnaroundTime.set(task.getTid(), currentTime);

            //FCFS does queue.remove(0) but the shortest task isnt always at the front,
            //so we remove the actual task that ran (Task.equals() compares by tid)
            queue.remove(task);

            task = pickNextTask();
        }

        //calculate totals after every task has completed (same as FCFS.java and RR.java)
        for (int i = 0; i < responseTime.size(); i++) {
            totalResponseTimes += responseTime.get(i);
            totalTurnaroundTimes += turnaroundTime.get(i);

            // waiting time = turnaround time - original CPU burst
            totalWaitingTimes += turnaroundTime.get(i) - originalBurstTime.get(i);
        }

        //calculate average scheduling performance
        averageTurnaroundTime = (float) totalTurnaroundTimes / turnaroundTime.size();
        averageWaitingTime = (float) totalWaitingTimes / turnaroundTime.size();
        averageResponseTime = (float) totalResponseTimes / responseTime.size();

        System.out.println("The average turnaround time is " + averageTurnaroundTime);
        System.out.println("The average waiting time is " + averageWaitingTime);
        System.out.println("The average response time is " + averageResponseTime);
    }

    @Override
    public Task pickNextTask() {
        //return null after all tasks are completed
        if (queue.isEmpty()) {
            return null;
        }

        //look through the whole queue and grab the task with the shortest burst
        //using < instead of <= so if two tasks tie, the one that came first wins (like FCFS)
        Task shortest = queue.get(0);
        for (Task t : queue) {
            if (t.getBurst() < shortest.getBurst()) {
                shortest = t;
            }
        }

        return shortest;
    }
}
