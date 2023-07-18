package cn.jiuyou;

import cn.jiuyou.constant.Payload;
import cn.jiuyou.serviceDiscovery.impl.ZookeeperServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static cn.jiuyou.constant.Constants.HOST;
import static cn.jiuyou.constant.Constants.PORT;

public class ZooKeeperConnectionTest {
    String connectString = "112.124.55.2:2183,39.101.74.23:2183,101.42.8.245:2183";
    //会话超时时间,单位毫秒
    private final int sessionTimeout = 2000;

    private ZooKeeper zooKeeper = null;


    //1.创建客户端
    //@Test
    @Before
    public void initZK() throws Exception {
        //Watcher就是监听节点
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, event -> {
            //监听发生后触发的事
            System.out.println(event.getType() + "--" + event.getPath());

        });
    }

    //2.创建子节点
    @Test
    public void createNode() throws Exception {
        /*
          参数1:创建节点的路径;
          参数2:创建节点存储的数据;
          3.创建节点后节点具有的权限
          4.节点类型  持久的还是短暂的
         */
        String create = zooKeeper.create("/kxj", "kxj".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        System.out.println(create);

    }

    //3.获取某一路径下节点数
    @Test
    public void getChild() throws Exception {
        //第二个参数是要不要监听
        List<String> children = zooKeeper.getChildren("/", false);

        for (String child : children) {
            System.out.println(child);
        }
    }

    //4.判断某一节点是否存在
    @Test
    public void isExists() throws Exception {
        Stat exists = zooKeeper.exists("/kxj", true);

        System.out.println(exists == null ? "不存在" : "存在");

        System.in.read();
    }

    //5.获取某一节点的数据
    @Test
    public void getData() throws Exception {
        byte[] data = zooKeeper.getData("/kxj", false, null);

        System.out.println(new String(data));
    }

    //6.删除某一节点
    @Test
    public void deleteNode() throws Exception {
        //参数2:指定要删除的版本号 -1代表删除所有版本
        zooKeeper.delete("/kxj", -1);
    }

    //7.修改某一节点的数据
    @Test
    public void setData() throws Exception {
        //参数2:指定要修改的版本号 -1代表删除所有版本
        zooKeeper.setData("/kxj", "kxj".getBytes(), -1);
    }

    private ZookeeperServiceDiscovery discovery;

    @Before
    public void setup() throws Exception {
        discovery = ZookeeperServiceDiscovery.getInstance();
    }

    @Test
    public void addService() throws Exception {
        // 构建 ServiceInstance 对象
        ServiceInstance<Payload> serviceInstance = ServiceInstance.<Payload>builder()
                .name("userService")
                .address(HOST)
                .port(PORT)
                .payload(new Payload(10))
                .build();

        try {
            // 注册服务
            discovery.registerService(serviceInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collection<ServiceInstance<Payload>> serviceInstances = discovery.queryForInstances("userService");
        //遍历
        for (ServiceInstance<Payload> instance : serviceInstances) {
            System.out.println(instance.getPayload().getActiveCount());
        }
        Payload payload = serviceInstance.getPayload();
        payload.incrementActiveCount();
        System.out.println(serviceInstance.getPayload().getActiveCount());
        discovery.updateService(serviceInstance);
        Collection<ServiceInstance<Payload>> serviceInstances2 = discovery.queryForInstances("userService");
        for (ServiceInstance<Payload> instance : serviceInstances2) {
            System.out.println(instance.getPayload().getActiveCount());
        }

    }

    @Test
    public void findService() throws Exception {
        // 构建 ServiceInstance 对象
        Collection<ServiceInstance<Payload>> serviceInstances = discovery.queryForInstances("cn.jiuyou.UserService");
        System.out.println(serviceInstances);
    }

}
