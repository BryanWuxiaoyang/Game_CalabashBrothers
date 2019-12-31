# 葫芦娃大战妖精
吴晓阳  171860622
## 运行指南
点击 **空格** 开始游戏，wsad控制爷爷的移动，按f键发射子弹。
完成一局游戏后，记录会被写入名字为log的文件。
点击 L键 读取记录，名字为record的文件是预先录制的游戏记录。
## 实验概述
本次实验我设计了一个2D游戏设计框架，并在此框架下实现了一个满足实验要求（即每个生物一个线程，保证位置不冲突、棋盘状地图）的葫芦娃游戏；以及一个人物位置任意，敌人随机出现、随机选择路径的塔防游戏，作为该游戏框架的可重用性、可扩展性的演示。
## 游戏操作指南
进入游戏后，会出现一个地图：
![pic1](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic1.png?raw=true)

地图中有格子，生物只能在格子中存在，且只能从一个格子移动到相邻的格子。

点击 **空格** 后，会分别在地图的上下部分出现敌人和葫芦娃（还有爷爷），随后敌人和葫芦娃会自动地朝着敌人的移动方向行进，在进入射程范围时开始发射子弹：

![pic2](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic2.png?raw=true)

不同的葫芦娃的移动速度、生命都不同，可以发射不同的子弹，且子弹的图像、速度、伤害也不相同。例如六娃的移动速度最快，是其它葫芦娃的两倍；一般生物的射程是方圆3格，而七娃可以达到8格；大娃的战斗伤害是其它生物的2倍，生命值也更高等等。

当生物死亡后，血条会变为全红，且在地图中留下图像。此时子弹可以穿过生物的身体，但其它生物不能（也即两个生物不能存在于同一个格子中）。

主角一方除了葫芦娃外还有爷爷。玩家可以按wsad操控爷爷进行移动，寻找敌人，躲避子弹。此外，按f键可以向四周发射硬币，具有0.5秒的冷却时间。当硬币击中敌人时，会造成小幅度伤害，击中葫芦娃时，可以进行一定的生命值恢复。由于葫芦娃在妖精众面前处于劣势，因此需要爷爷从旁辅助：
![pic3](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic3.png?raw=true)

需要注意的是，由于爷爷的生命值比较低，且敌军在被爷爷击中后会直接转移目标至爷爷，因此需要被敌人围攻导致暴毙。

此外，在游戏中会每隔一定时间从天而降**天火**，被它砸到的生物会损失大量生命值，因此需要防止被砸到：

![pic4](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic4.png?raw=true)

当有一方被全歼时，游戏停止：
![pic5](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic5.png?raw=true)

结束的游戏记录会被保存在命名为“log“的文件中。

通过点击L键可以打开文件选项，选择一个记录文件即可运行游戏记录。命名为”record“的文件是已经录制好的游戏记录：
![pic6](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic6.png?raw=true)

## 程序设计说明
### 游戏框架
#### 物件
游戏中所有的可控制物件继承自Item接口，不同的物件具有不同的能力，分别实现了其它继承自Item的接口。

例如：具有主动交互能力的Item，需实现Interactor接口， 而被动接受交互的Item则需实现Interacted接口。

![pic7](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic7.png?raw=true)

每个Item物件都有一个remove标记，当标记为true时，代表它应当被删掉了。

若该Item绑定了一个GameConsole，则会自动从其中移除自身。

外界也可以通过添加监听器来进行额外操作（例如log记录即是这样检测删除状况的）：
![pic8](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic8.png?raw=true)

Item的坐标由屏幕坐标确定，每个Item都具有一个特有的Node，负责让GameConsole进行显示。
![pic9](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic9.png?raw=true)

实现了BasicItem，以及基于前者的BasicCreature，它继承了Emitter和HasLife接口，拥有生命值、可以发射Item。
![pic14](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic14.png?raw=true)

这两个接口的功能都通过独立设计的构件封装，BasicCreature将其聚合在一起。
![pic15](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic15.png?raw=true)


#### 游戏控制台
游戏的整体布局包括一个背景地图和一个游戏活动图。


背景地图是GameMap，它储存静态的地图状况。
![pic10](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic10.png?raw=true)

GameMap具有许多的格子（Grid），既可以是矩阵类型的，也可以任意放置。
![pic11](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic11.png?raw=true)

Item的坐标定位既可以通过格子的坐标确定，也可以自己任意指定。

活动图是所有具有可操控性的Item的储存位置，所有的Item的添加删除都在这个Group中。

背景地图和游戏活动图被整合在统一的GameConsole里。
![pic12](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic12.png?raw=true)

最基本的GameConsole提供了添加删除Item、越界检查、延迟插入等功能，通过插入自定义的任务Task、动画Animation，可以拓展它的功能。

在此基础上，设计了多个装饰器，实现了更多的功能：
**InteractionGameConsole**实现了Item的交互检测和交互优化

**OverloadCheckGameConsole**实现了控制同一时刻活动图中的Item数量和密度的功能。

**ChessboardGameConsole**实现了二维矩阵式的地图。
![image](2B19EBAFEAE74F37B8EE67A651ECD19A)

#### 游戏运行处理
人物（Creature）在UI上的移动是通过Animation实现的，在逻辑内存上的移动则通过GameConsole的move函数实现。

每个Creature都有一个线程，它会检测当前的目标，接近目标，在进入射程范围后发射子弹。

当自己受到攻击时，Creature会将目标转移到发射者；若一定时间没有目标，则游戏系统会自动为其选择目标。

存在一个单独的线程，它会检测地图上的生物的生存状况，在某一方全灭后调用stop函数结束游戏。

#### 单元测试
在单元测试中，测试了游戏控制台GameConsole最核心的对物件的添加和删除功能：
```java
    @Test
    public void testAdd();

    @Test
    public void testRemove();
```

#### 游戏记录方式
游戏过程的记录包括几个部分，分别是 移动记录、插入记录、创造记录、生命值变化记录和死亡记录。通过记录这些内容，并写入文件，可以在读取文件后，动态地运行游戏并执行游戏记录：
```java
public static class Movement implements Serializable {
        long time;
        int creatureId;
        int x;
        int y;
    }

    public static class Emitting implements Serializable{
        long time;
        int creatureId;
        double degree;
    }

    public static class Insertion implements Serializable{
        long time;
        CreatureType creatureType;
        int creatureId;
        int x, y;
    }

    public static class Death implements Serializable{
        long time;
        int creatureId;
    }

    public static class LifeChange implements Serializable{
        long time;
        int creatureId;
        double value;
    }

    public static class LoggedCreator implements Serializable{
        long time;
        Creator<? extends Item> creator;
    }
```

### 游戏设计方法
#### 异常处理
本次实验中的异常处理主要出现在线程切换和唤醒部分。
当在sleep过程中被interrupt时，可以忽略掉该异常：
```java
try{Thread.sleep(timeMillis);}catch (InterruptedException ignore){}
```
当线程或任务运行时出现异常时，需要输出其调用栈，然后重新生成运行时异常RuntimeError：
```java
try{
    if(!assignment.call()) removeSet.add(assignment);
    }catch (Exception e){
        runTag = false; 
        e.printStackTrace(); 
        throw new RuntimeException();
    }
```

#### 集合类型
本次实验中使用到了包括List, Set, Map在内的java内置的集合类型，此外还运用了不同的面向集合的同步方法，例如Collections类中的synchronized相关方法，CopyOnWriteArrayList、ConcurrentLinkedQueue等内置的同步集合，以尽量提高并发效率。

#### 泛型
本次实验的泛型主要出现在工厂类中：
```java
public interface Creator<T> extends Serializable {
    T create();
}
```
```java
public class BasicCreator<T> implements Creator<T>{

    private final Class<? extends T> cls;

    public BasicCreator(Class<? extends T> cls){
        this.cls = cls;
    }

    public T create(){
        T t;
        try {
            t = cls.newInstance();
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
        return t;
    }
}
```
不同的形参T可以构造不同的类型的工厂。

#### 注解
在java中，有的注解只是提示符，没有实际的运行代码，如@Override 只是作静态检查：
```java
 @Override
public boolean isUp() {
    return true;
}
```

而另外一些注解则具有实际的作用，例如@Before和@After会在每个非静态方法的前后运行，@BeforeClass会在类被调用，或是静态方法运行前运行：
```java
    @BeforeClass
    public static void before(){
        console = new BasicGameConsole();
    }
```

#### 输入输出
本次实验的输入输出都是从文件中输入输出，因此用到了FileInputStream和FileOutputStream。
而读取和写入的元素又都是Serializable对象，因此用到了ObjectInputStream和ObjectOutputStream：
```java
closeOutputStream();
outputStream = new ObjectOutputStream(new FileOutputStream(filename));

ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
```

#### 设计模式
##### 工厂模式
在实现子弹的发射时，实验代码采用了工厂模式。首先具有一个专门的负责生成子弹的类EmitComponent，它会获取一个Creator的参数，在合适的时间，它会自动调用该Creator生成对象，并添加进游戏控制台:
```java
Emission emission = creator.create();
if (emission == null) return;
emission.setConsole(getConsole());
emission.setEmitter(emitter);
```
对不同的生成物，只需要传递不同的工厂对象即可实现，例如对于葫芦娃，首先定义一个工厂类：
```java
private class CalabashBulletCreator implements Creator<Bullet> {
        public Bullet create() {
            Point2D center = Utils.getCenter(getCurrentBounds());
            Bullet bullet = new CalabashBullet(ItemTypeId.BULLET, Bullet.bulletImage, 700, damage, Calabash.this.getDegree(), center.getX(), center.getY());
            bullet.setLoading(Calabash.this.isLoading());
            return bullet;
        }
    }
```

然后把它的一个实例当做参数传递给对应的发射器EmitComponent：
```java
setEmissionCreator(new CalabashBulletCreator(), Duration.millis(300));
```
这样它就会自动地每隔一定时间生成该物件了。

##### 监听器
本次实验在多个地方采用了监听器的设计模式，例如在生物死亡、生命值变化、发射物件时，都可以在外界设置监听器，从而实现不同的功能，例如在进行游戏记录时，通过添加监听器可以在不改变代码内部结构的情况下进行记录：
```java
                enemy.setOnEmit(new EventHandler<EmitEvent>() {
                    public void handle(EmitEvent event) {
                        if (loggingState) logger.logEmitting(enemy, enemy.getDegree());
                    }
                });
                enemy.setOnMove(new EventHandler<MoveEvent>() {
                    public void handle(MoveEvent event) {
                        if (loggingState) logger.logMovement(enemy, event.getX(), event.getY());
                    }
                });
                enemy.setOnRemove(new EventHandler<RemoveEvent>() {
                    public void handle(RemoveEvent event) {
                        console.remove(enemy);
                        console.add(new DeadCreature(enemy.getImage(), enemy.getX(), enemy.getY()));
                        if (loggingState) logger.logDeath(enemy);
                    }
                });
                enemy.setOnLifeChanged(new EventHandler<LifeChangeEvent>() {
                    public void handle(LifeChangeEvent event) {
                        if (loggingState) logger.logLifeChange(enemy, event.getDstLife() - event.getSrcLife());
                    }
                });

```

##### 装饰器模式
在工厂类以及游戏控制台的实现上，我采用了装饰器模式，使得通过不同的装饰类的装配，可以实现不同的功能，例如工厂实现了定时、多次生成等功能，而控制台则实现了碰撞检测、过载优化等功能：
![pic13](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic13.png?raw=true)

![pic20](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic20.png?raw=true)

##### 物件交互
物件间的交互利用了一个Effect类。

伤害效果是DamageEffect，它需要由interactor生成并传递数值、由interactee接收并修改数值，然后调用make方法来形成效果。
![pic19](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic19.png?raw=true)

##### 碰撞检测算法
Item的碰撞（交互）检测，最基本的是n^2遍历。

优化算法将整个地图划分成多个区块，在相同的区块中的Item可进行交互。

若Item的影响范围跨越了多个区块，则会对受影响的所有区块的Item进行碰撞检测。

优化后可以将100w次的检测次数减少到2w次左右。

如下图，其总物件数达到了4000，若按照1对1检测，则需每30ms检测4000^2 = 16,000,000次，而通过优化后，可减少到少于1000,000次：
![pic17](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic17.png?raw=true)

##### 线程设计
线程的生成方式有许多种，包括Timeline、Platfrom.runLater、Thread等。

许多线程的功能都是每隔相同时间进行某一操作。

这样的线程被统一设计成Task接口。

其中，AssignmentPoolTask的任务运行在一个AssignmentPool中。

一个AssignmentPool里面包含了许多具有相同间隔时间的任务。它有一个线程，会每隔固定的时间执行所有这些任务。

通过AssignmentPool，可以让发射器线程统一放在同一个（或几个）线程中，极大提高效率。

例如，倘若每个子弹的移动都需要一个线程，那么它会消耗大量的内存和运算资源，而通过将它们的任务集中到一个线程中，进行定时运行，则可以大大减少开销。

### 扩展游戏展示
基于现有的游戏框架，我实现了另一个塔防游戏，体现出了代码的可扩展性和可重用性。
![pic16](https://github.com/BryanWuxiaoyang/hello-world/blob/master/pic16.png?raw=true)
该游戏具有商店，在金币足够时，可以选择商店中的人物，添加到地图中，人物会自动向邻近的敌人发射子弹。

敌人会从出发点自动生成，随机选择一条到目的地的路径并进行移动。

目的地有一个我方基地，当基地生命值归0时，游戏结束。