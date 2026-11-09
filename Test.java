public class Test {

    public static void main(String[] args) {

        //创建我自己定义的任务队列
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(16, new SleepingWaitStrategy());
        
        //启动一个生产者线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    //在一个循环中一直向队列中放入数据
                    try {
                        queue.put(i++);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //启动一个消费者线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //在一个循环中不断地从队列中取出数据
                    try {
                        queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
    }
}