# simplerpc-java
## 介绍
一个由Java编写的RPC项目，使用简单，支持同步以及异步调用。<br>
程序使用Netty作为通讯基础组件，支持可替换序列化组件（暂时支持Kryo）。<br>
## Build
本程序在 JDK8(Oracle JDK 8 build 1.8.0_211-b12) 平台编写和测试，使用Gradle Wrapper构建。
```bash
./gradlew simplerpc:clean simplerpc:jar
````
## How to use 
###### 演示文件
```
--com.Jelipo.test 
----Main.java 
----People.java
----ProxyInterface.java
----ProxyInterfaceImpl.java
```
###### People.java 普通的传输对象
```java
public class People {
    private String name;
    private int age;
    //如果RPC要传输一个对象，这么此对象必须要有个空构造方法。
    public People() {}

    public People(String name, int age) {
        this.name = name;
        this.age = age;
    }
    ...getter and setter...
}
```
###### ProxyInterface.java 接口
```java
@Rpc //在RPC接口上添加@Rpc注解
public interface ProxyInterface {
    String getUserData(People people);
    CompletableFuture<People> getUserDataAsysn(String info);
}
```
###### ProxyInterfaceImpl.java 实现类
```java
@RpcImpl //在实现类上添加@RpcImpl注解
public class ProxyInterfaceImpl implements ProxyInterface {
    @Override
    public String getUserData(People people) {
        return "Sync: Name:" + people.getName() + ". Age:" + people.getAge();
    }
    @Override
    public CompletableFuture<People> getUserDataAsysn(String info) {
        return CompletableFuture.supplyAsync(() -> {
            String[] split = info.split(",");
            return new People(split[0], Integer.parseInt(split[1]));
        });
    }
}
```
###### Main.java 主要的测试类。
```java
public class Main {
    public static void main(String[] args) throws Exception {
        //设置端口
        int port = 18080;
        //启动RPC服务器
        RpcServer rpcServer = new RpcServer()
                .port(port)
                //扫描被注解的类的路径
                .classesPath("com.Jelipo.test")
                .start();

        //启动RPC客户端
        RpcClient rpcClient = new RpcClient()
                //连接RPC服务
                .hostAndPort("localhost", port)
                .classesPath("com.Jelipo.test")
                .connect();
        
        //从client中获取RPC接口的代理类。
        ProxyInterface proxyInterfaceImpl = rpcClient.getRpcImpl(ProxyInterface.class);

        //同步调用RPC方法
        People people = new People("小丽", 18);
        //支持的参数和返回类型包括Java的基本类型、String、只包含基本类型(可嵌套)且有空构造方法的POJO类.
        String result = proxyInterfaceImpl.getUserData(people);
        System.out.println(result);

        //异步调用RPC方法
        CompletableFuture<People> completableFutureResult = proxyInterfaceImpl.getUserDataAsysn("老王,35");
        completableFutureResult.whenComplete((people1, throwable) -> {
            System.out.println("Async: Name " + people1.getName() + " Age " + people1.getAge());
        });

        //当异步调用远程方法，远程方法抛出异常时的处理。
        CompletableFuture<People> exceptionResult = proxyInterfaceImpl.getUserDataAsysn("不知道老王几岁");
        exceptionResult.whenComplete((people2, throwable) -> {
            System.out.println(people2);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

    }
}

```
###### 输出结果
```bash 
Sync: Name:小丽. Age:18
Async: Name 老王 Age 35
null
java.util.concurrent.CompletionException: com.Jelipo.simplerpc.exception.RemoteCallException: An exception occurred when calling a remote method.
	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:292)
	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:308)
	at java.util.concurrent.CompletableFuture.uniWhenComplete(CompletableFuture.java:769)
...省略...
```
