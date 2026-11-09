public class ArrayBlockingQueue<E>{

    //存放数据的数组
    final Object[] items;
	//队列中存放数据的个数
    int count;

    //读指针
    int takeIndex;

    //写指针
    int putIndex;

    //等待策略
    WaitStrategy wait = new SleepingWaitStrategy();

    //队列的构造方法
    public ArrayBlockingQueue(int capacity, WaitStrategy waitStrategy) {
        //初始化数组
        this.items = new Object[capacity];
        this.wait = waitStrategy;
    }


    //存放数据到队列的方法，不限时阻塞，直到能够放入数据
    public void put(E e) throws InterruptedException {
        //队列存放数据的个数等于容器数组容量，就意味着队列满了
         while(count == items.length) {
             System.out.println("如果队列满了，生产者线程就在此阻塞");
             wait.waitFor();
         }
         System.out.println("队列有位置了，生产者线程被唤醒，继续执行把数据放入数组中");
         //把数据放入队列中
         enqueue(e);
    }
    
    
    //取出数据的方法，不限时阻塞
    public E take() throws InterruptedException {
        //队列存放数据的个数等于0，就意味着队列空了
         while(count == 0) {
             System.out.println("如果队列空了，消费者线程就在此阻塞");
             wait.waitFor();
         }
         System.out.println("队列有数据了，消费者线程被唤醒，继续执行把数据取走的操作");
        return dequeue();
    }

    //把数据添加到数组的方法，按顺序从前往后添加
    private void enqueue(E e) {
        //得到容器数组
        final Object[] items = this.items;
        //把数据放到写指针的索引位置
        items[putIndex] = e;
        //写指针加1，然后判断写指针是否等于容器长度
        if(++putIndex == items.length) {
            //等于就重置写指针为0
            putIndex = 0;
        }
        //容器存储数据的个数加1
        count++;
    }

    //把数据从队列容器中拿走的方法，按顺序从前往后拿走
    private E dequeue() {
        //得到容器数组
        final Object[] items = this.items;
        //根据读指针得到要取走的数据
        E e = (E) items[takeIndex];
        //把数组中的对应位置置为null
        items[takeIndex] = null;
        //判断读指针是否等于数组长度，如果等于数组长度，说明读到数组结尾了
        if(++takeIndex == items.length) {
            //重置读指针，可以从数组头部重新读取数据了
            takeIndex = 0;
        }
        //数组存储数据的个数减1
        count--;
        //返回要取走的数据
        return e;
    }
}