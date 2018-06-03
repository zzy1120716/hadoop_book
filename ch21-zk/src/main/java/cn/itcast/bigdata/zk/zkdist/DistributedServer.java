package cn.itcast.bigdata.zk.zkdist;

import org.apache.zookeeper.*;

public class DistributedServer {

    //private static final String connectString="mini1:2181,mini2:2181,mini3:2181";
    private static final String connectString="localhost:2181";
    private static final int sessionTimeout = 2000;
    private static final String parentNode = "/servers";

    private ZooKeeper zk = null;

    /**
     * 创建到zk的客户端连接
     * @throws Exception
     */
    public void getConnect() throws Exception {

        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 收到事件通知后的回调函数（应该是我们自己的事件处理逻辑）
                System.out.println(event.getType() + "---" + event.getPath());
                try {
                    // 重新启动监听
                    zk.getChildren("/", true);
                } catch (Exception e) {
                    //
                }
            }
        });

    }

    /**
     * 向zk集群注册服务器信息
     * @param hostname
     * @throws Exception
     */
    public void registerServer(String hostname) throws Exception {

        String create = zk.create(parentNode + "/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online.. " + create);

    }

    /**
     * 业务功能
     * @param hostname
     */
    public void handleBusiness(String hostname) throws Exception {
        System.out.println(hostname + " is working");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception {

        // 获取zk连接
        DistributedServer server = new DistributedServer();
        server.getConnect();

        // 利用zk连接注册服务信息
        server.registerServer(args[0]);

        // 启动业务功能
        server.handleBusiness(args[0]);
    }
}
