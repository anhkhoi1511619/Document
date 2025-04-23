
public class WaitingAuthentication extends ChargeJob{
    public WaitingAuthentication(int amount) {
        super(amount);
    }

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
