import java.util.*;

public class FCFS implements Algorithm { 

    //list of tasks waiting to be scheduled 
    private List<Task> queue;

    //Store scheduling data for calculating performance metrics 
    private List<Integer> originalBurstTime = new ArrayList<>();
    private List<Integer> responseTime = new ArrayList<>();
    private List<Integer> turnaroundTime = new ArrayList<>();
    
    public FCFS(List<Task> queue) {
        this.queue = queue;

        //Save each tasks original burst before scheduling modifies it
        //Response and turnaround times are null until they are recorded
        for (int i = 0; i < queue.size(); i++) {
            originalBurstTime.add(queue.get(i).getBurst());
            responseTime.add(null);
            turnaroundTime.add(null);

        }
    }
    
    @Override
    public void schedule() {
        //Execute tasks in the order they arrived

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

            //Get the tasks burst time
            runtime = task.getBurst();

            //response time is the first time the task receives the CPU
            responseTime.set(task.getTid(), currentTime);

            //FCFS runs the task for its entire burst
            CPU.run(task, runtime);
            currentTime += runtime;
            runtime = 0;
            task.setBurst(runtime);

            turnaroundTime.set(task.getTid(), currentTime);
            queue.remove(0);

            task = pickNextTask();
        }

        //calculate totals after every task has completed
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

        System.out.println("the average turnaround time is " + averageTurnaroundTime);
        System.out.println("the average waiting time is " + averageWaitingTime);
        System.out.println("the average response time is " + averageResponseTime);
    }

    @Override
    public Task pickNextTask() {
        //return null after all tasks are completed
        if (queue.isEmpty()) {
            return null;
        }

        //FCFS always picks the task at the front of the queue
        return queue.get(0);
    }


}