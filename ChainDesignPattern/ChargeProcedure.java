public class ChargeProcedure extends ChargeJob {
    WaitingAuthentication waitingAuthentication;
    WaitingDeposit waitingDeposit;
    WaitingExecution waitingExecution;
    public ChargeProcedure(int amount) {
        super(amount);
        waitingAuthentication = new WaitingAuthentication(amount);
        waitingDeposit = new WaitingDeposit(amount);
        waitingExecution = new WaitingExecution(amount);

        waitingAuthentication.chain(waitingDeposit, ChainCondition.RUN_ALWAYS)
                                .chain(waitingExecution, ChainCondition.RUN_ALWAYS)
                                    .then(this::clear)
                                        .then(this::report);
    }

    @Override
    public void clear() {
        super.clear();
        waitingAuthentication.clear();
        waitingDeposit.clear();
        waitingExecution.clear();
    }



    @Override
    protected void doRun() {
        waitingAuthentication.run();
    }

    void report() {
        System.out.println("waitingAuthentication:  " + waitingAuthentication.status);
        System.out.println( "waitingDeposit:  " + waitingDeposit.status);
        System.out.println( "waitingExecution:  " + waitingExecution.status);
        var success = waitingAuthentication.done() &&
                waitingDeposit.done() &&
                waitingExecution.done();
        setStatus(success ? Status.DONE : Status.FAILED);
    }
    
}
