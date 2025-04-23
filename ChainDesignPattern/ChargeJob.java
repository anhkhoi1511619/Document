
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class ChargeJob {
    public enum Status {
        QUEUED,         // job is determined to run
        PENDING,        // job is pending
        WORKING,        // job is running
        FAILED,         // job is finished and failed
        SKIPPED,        // job was not run but skipped instead
        DONE,           // job is done successfully
    }
    public enum ChainCondition {
        RUN_IF_STRICTLY_SUCCESS,
        RUN_IF_SUCCESS,
        RUN_IF_FAILED,
        RUN_ALWAYS,
    }

    int amount = 0;
    public ChargeJob(int amount)
    {
        this.amount = amount;
    }

    ChainCondition condition = ChainCondition.RUN_ALWAYS;
    public Status status = Status.PENDING;
    static final ScheduledThreadPoolExecutor executor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(30);
    List<Runnable> onFinished = new ArrayList<>();
    List<Runnable> onStarted = new ArrayList<>();

    static final String TAG = "CHARGE_PROCEDURE";

    ChargeJob next;
    ChargeJob previous;

    public void clear() {
    }

    public ChargeJob chain(ChargeJob other, ChainCondition condition) {
        this.next = other;
        other.previous = this;
        this.condition = condition;
        return other;
    }
    public static ChargeJob runAll(ChargeJob... jobs) {
        return new ChargeJob(0) {
            @Override
            protected void doRun() {
                if(jobs.length == 0) {
                    setStatus(Status.DONE);
                    return;
                }
                setStatus(Status.WORKING);
                ChargeJob first = null;
                ChargeJob last = null;
                for(var job: jobs) {
                    if(last == null) {
                        first = last = job;
                        continue;
                    }
                    last = last.chain(job, ChainCondition.RUN_ALWAYS);
                }
                last.then(() -> {
                    var success = Arrays.stream(jobs).allMatch(job -> job.done());
                    setStatus(success?Status.DONE:Status.FAILED);
                });
                first.run();
            }
        };
    }
    protected abstract void doRun();
    public void run() {
        System.out.println( "Task "+this.getClass().getSimpleName()+" is starting...");
        System.out.println( "Performing onStarted event for "+this.getClass().getSimpleName()+"...");
        for (var callback: onStarted) {
            callback.run();
        }
        if(finished()) {
            System.out.println( "Task "+this.getClass().getSimpleName()+" is prematurely finished, calling stop using thread pool "
                    +executor.getActiveCount()+"/"+executor.getCorePoolSize());
            executor.execute(() -> stop());
        } else {
            System.out.println( "Start "+this.getClass().getSimpleName()+" using thread pool "+
                    executor.getActiveCount()+"/"+executor.getCorePoolSize());
            setStatus(Status.WORKING);
            executor.execute(() -> doRun());
        }
    }
    public ChargeJob then(Runnable runnable) {
        this.onFinished.add(runnable);
        return this;
    }
    public ChargeJob before(Runnable callback) {
        this.onStarted.add(callback);
        return this;
    }
    void setStatus(Status status) {
        this.status = status;
        if(finish(status)) {
            System.out.println( "schedule task, thread pool active count = "+executor.getActiveCount()+"/"+executor.getPoolSize());
            executor.execute(()->stop());
        }
    }

    void stop() {
        if(!finished()) {
            //throw new RuntimeException();
            System.err.println( "Task "+this.getClass().getSimpleName()+" finished with invalid result "+status);
            return;
        }
        for (var callback: onFinished) {
            callback.run();
        }
        clear();
        if(!finished()) {
            return;
        }
        System.out.println( "Task "+this.getClass().getSimpleName()+" finished with result "+status);
        var canRunNext = next != null;
        if(canRunNext) {
            var shouldRunNext =
                    (condition == ChainCondition.RUN_IF_SUCCESS && done()) ||
                            (condition == ChainCondition.RUN_IF_FAILED && !done()) ||
                            (condition == ChainCondition.RUN_IF_STRICTLY_SUCCESS && status == Status.DONE) ||
                            (condition == ChainCondition.RUN_ALWAYS && finished());
                            System.out.println( "schedule task at Job, thread pool active count = "+executor.getActiveCount()+"/"+executor.getPoolSize());
            if(shouldRunNext) {
                executor.execute(()->next.doRun());
            } else {
                executor.execute(() -> next.byPassWith(status));
            }
        }

    }

    public boolean done() {
        return done(status);
    }

    boolean done(Status status){
        return status == Status.DONE ||
                status == Status.SKIPPED;
    }

    public void byPassWith(Status status) {
        this.status = status;
        stop();
    }

    boolean finished(){
        return finish(status);
    }

    boolean finish(Status status){
        return status == Status.DONE||
                status == Status.FAILED||
                status == Status.SKIPPED;
    }

    public void resetStatus() {
        setStatus(Status.PENDING);
    }
}
