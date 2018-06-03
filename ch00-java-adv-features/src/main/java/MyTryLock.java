import java.util.ArrayList;

import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.ReentrantLock;



public class MyTryLock {

    private static ArrayList<Integer> arrayList = new ArrayList<Integer>();

    //注意这个地方

    static Lock lock = new ReentrantLock();



    public static void main(String[] args) {

        new Thread() {

            @Override

            public void run() {

                Thread thread = Thread.currentThread();

                //

                boolean tryLock = lock.tryLock();

                System.out.println(thread.getName() + " " + tryLock);

                if (tryLock) {

                    try {

                        System.out.println(thread.getName() + "得到了锁");

                        for (int i = 0; i < 5; i++) {

                            arrayList.add(i);

                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                    } finally {

                        System.out.println(thread.getName() + "释放了锁");

                        lock.unlock();

                    }

                }

            }

        }.start();



        new Thread() {

            public void run() {

                Thread thread = Thread.currentThread();

                boolean tryLock = lock.tryLock();

                System.out.println(thread.getName() + "  "  + tryLock);

                if (tryLock) {

                    try {

                        System.out.println(thread.getName() + "得到了锁");

                        for (int i = 0; i < 5; i++) {

                            arrayList.add(i);

                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                    } finally {

                        System.out.println(thread.getName() + " 释放了锁");

                        lock.unlock();

                    }

                }

            }

        }.start();

    }

}