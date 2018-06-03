package cn.itcast.bigdata.zk.zkdist;

public class Test {

    public static void main(String[] args) {
        /*ArrayList<String> al = new ArrayList<>();
        al.add("a");
        al.add("b");
        System.out.println(al);*/

        System.out.println("主线程开始啦");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程开始啦");
                while (true) {
                    // do something
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}
