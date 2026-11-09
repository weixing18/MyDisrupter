import java.util.concurrent.locks.LockSupport;

public final class SleepingWaitStrategy implements WaitStrategy{

    //默认的自旋次数，自旋就是空转
    private static final int DEFAULT_RETRIES = 200;
    //默认的睡眠时间
    private static final long DEFAULT_SLEEP = 100;
	//自旋次数
    private  int retries;
    //睡眠时间
    private final long sleepTimeNs;

    //无参构造方法
    public SleepingWaitStrategy(){
        this(DEFAULT_RETRIES, DEFAULT_SLEEP);
    }

    //这里多出来一个retries成员变量的set方法，因为线程结束阻塞后要重置retries属性为200
    public void setRetries(int retries) {
        this.retries = retries;
    }

    public SleepingWaitStrategy(int retries, long sleepTimeNs){
        //自旋次数被赋值为200
        this.retries = retries;
        //睡眠时间被赋值为100
        this.sleepTimeNs = sleepTimeNs;
    }

    //while循环是在ArrayBlockingQueue类的put方法中
    //waitFor方法就是在一个while循环中的，这个要理清楚
    //在这个方法中，线程阻塞结束之后需要将retries属性重置为200
    //否则线程下一次需要阻塞的时候，retries不是200，就会少自旋甚至根本不自旋就进入阻塞了
    //但是该怎么在该方法中重置retries属性呢？就在ArrayBlockingQueue类中重置，因为在下面的
    //方法中无法判断是否阻塞结束了，大的while循环在ArrayBlockingQueue类中呢
    @Override
    public void waitFor(){
        
        //这意味着自旋次数是200，也就是线程刚要被阻塞之前
        //所谓自旋，就是让线程空转，就是下面这样，每空转1次，就让自旋次数减1
        if (retries > 100){
            --retries;
        }
        //如果自旋次数小于100，大于0了，说明已经自旋了很多次了
        //但还是不能继续向下工作，这时候尝试让该线程让出CPU
        else if (retries > 0){
            --retries;
            Thread.yield();
        }
        else{//走到这里意味着自旋次数到达200了，这时候就干脆让线程睡一会吧
            //睡的时间就是100纳秒，不能睡得太久，因为生产者可能随时发布新的数据
            LockSupport.parkNanos(sleepTimeNs);
        }
    }
}