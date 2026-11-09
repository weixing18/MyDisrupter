import java.util.concurrent.locks.LockSupport;

public final class SleepingWaitStrategy implements WaitStrategy{

    //睡眠时间
    private final long SLEEP_TIME_NS = 100;
    //无参构造方法
    public SleepingWaitStrategy(){
        
    }

    @Override
    public void waitFor(){
        //如果队列已满或者队列已空，就让生产者或消费者线程自己睡100纳秒
        LockSupport.parkNanos(SLEEP_TIME_NS);
    }
}