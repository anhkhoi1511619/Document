
public class WaitingDeposit extends ChargeJob{
    static WaitingDeposit instance = null;
    int amount;
    public WaitingDeposit(int amount) {
        super(amount);
        this.amount = amount;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
