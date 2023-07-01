# fox-edge-server-common-entity-base

### 介绍
**fox-edge-server-common-entity-base**模块提供了Entity的基础访问能力


### Redis缓存方案
为了加快数据处理速度，Fox-Edge的数据基本上都采用了Redis作为缓存。但是同样是使用redis，有多种技术方案，
分别用来解决各种的场景问题。

- 生产者/消费者：ProducerRedisService/ConsumerRedisService
``` 
背景：由于redis服务是独立的基础服务，应用服务对redis服务直接访问，会存在进程之间的延迟，大约是几十个毫秒左右。
如果是高频的访问缓存数据，应用服务与redis服务累加的延迟会大的无法接受，然后就有了下列技术方案。

缓冲方案：
数据同时在Local缓存和redis缓存之间各自保存一份数据，并启动一个后台线程维持Local缓存和Redis缓存的数据同步。
那么，应用服务只需要访问Local缓存中的数据即可，此时的延迟基本上忽略为0，可以支持高频的数据处理

数据结构：
缓存方案产生的Redis数据结构包括数据、敏捷数据、同步标识，三个部分
data：真正的数据部分，使用json结构进行保存
sync：时间戳标识，最近更新数据的时间
agile：敏捷数据，实际上是粒度细到每个数据对象的时间戳标识，用于判定到底是哪些数据发生了变化

``` 

- 消费者：HashMapRedisService
``` 
背景：有了ConsumerRedisService之后，数据访问非常迅速，但是ConsumerRedisService的Local缓存的数据结构
是Map<String, BaseEntity>，这样有个问题就是方便Fox-Edge团队对Fox-Edge应用的对BaseEntity使用。
但是有个问题，就是如果是非第三方开发团队，他们并不知道BaseEntity，他们只知道Json对象和Map对象，此时为了方便
两者之间的交互，提供了Map<String, Map<String, Object>> 数据结构作为Local缓存的消费者HashMapRedisService

方案：
方案与ConsumerRedisService相同，但是Local缓存为更通用的Map<String, Map<String, Object>>

``` 

- 直读/直写：RedisWriter/RedisReader
``` 
背景：上面三个种缓存模块虽然速度非常快，但它们是通过空间为代价来换取访问效率。
但是，有的应用它们管理的数据量非常大，Java缓存对象的内存空间效率又很低，导致整个应用占用的内存都非常巨大。
此时，继续采用缓存方案就不合时宜了，这边就提供了直读/直写 Redis的两个类。它们产生的Redis数据结构，依然跟上面的缓存
方案的数据结构一致，所以它们彼此产生的数据，是可以互相操作的。

方案：
直接访问Redis服务的数据，在Redis的数据结构依然为data/agile/sync结构。
因为数据结构一致，所以可以跟上面三种组件互通

``` 

- 消费者：AgileMapRedisService
``` 
背景：
ProducerRedisService/ConsumerRedisService/HashMapRedisService虽然非常快，但是内存消耗很大
RedisWriter/RedisReader虽然内存消耗很小，但是无法快速感知变化Redis数据的变化
但是有些场景需要敏捷的感知数据变化，但是又不想太占用内存空间，然后有了下面这个方案

结合这两种方案，产生了第三种方案，就是只缓存敏捷数据，然后通过敏捷数据感知变化后，再去Redis服务读取具体的数据

方案：
Local缓存了Redis的agile/sync数据，并启动一个后台线程维持Local缓存和Redis缓存的数据同步
当agile/sync感知到变化的时候，业务模块再去Redis读取具体的data数据，然后通知给使用者

``` 