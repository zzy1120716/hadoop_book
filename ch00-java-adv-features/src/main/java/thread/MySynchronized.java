package thread;

public class MySynchronized {

    public static void main(String[] args) {

        final MySynchronized mySynchronized1 = new MySynchronized();
        final MySynchronized mySynchronized2 = new MySynchronized();

        new Thread("thread1") {
            public void run() {
                synchronized (mySynchronized1) {
                    try {
                        System.out.println(this.getName() + " start");
                        int i = 1 / 0;   // 如果发生异常，jvm会将锁释放

                        Thread.sleep(5000);
                        System.out.println(this.getName() + "醒了");
                        System.out.println(this.getName() + " end");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread("thread2") {
            public void run() {
                synchronized (mySynchronized1) {  // 争抢同一把锁
                //synchronized (mySynchronized2) {    // 不是一把锁
                    System.out.println(this.getName() + " start");
                    System.out.println(this.getName() + " end");
                }
            }
        }.start();
    }
}
