## Java多线程增强
### 进程
应用程序 jps、ps -ef能看见的

### 线程
进程内部的相对独立的空间，java的Thread

#### 多线程运行的原理
cpu在线程中的时间片的切换，并非同时

#### 两种线程实现方式：extends和implements
##### hadoop_book/ch00-java-adv-features/src/main/MyThreadWithExtends,MyThreadWithImplements

#### java同步关键词synchronized
线程之间的“锁”
1. 获取锁的线程执行完代码块，释放锁
2. 若抛出异常，也会释放锁

#### lock
java.util.concurent.lock
##### TryLock/LockInterruptbly/ReadWriteLock

#### 线程池的5种创建方式
1. Single Thread Executor
2. Cached Thread Pooll
3. Fixed Thread Pool
4. Scheduled Thread Pool
5. Single Thread Scheduled Pool

## Java并发包

## Java JMS技术
### ActiveMQ

## Java动态代理，反射

## JVM技术