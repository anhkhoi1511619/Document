
public class WaitingExecution extends ChargeJob{
    public WaitingExecution(int amount) {
        super(amount);
    }

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
