# ToolsProject

Java工具项目集合，提供各类常用工具类和代码示例。

## 项目简介

ToolsProject 是一个综合性的Java工具项目，包含数据库操作、Excel处理、网络通信、分布式技术、文件处理、安全加密等多个领域的工具类和示例代码。同时包含一个Python编写的批量远程命令执行工具。

## 技术栈

- **Java 1.6+**
- **Maven**
- **主要依赖**：
  - Apache POI (Excel处理)
  - Spring Framework
  - Hibernate
  - Kafka
  - HBase
  - Redis (Jedis)
  - Netty
  - WebMagic (爬虫)
  - Jython (Python集成)
  - HttpClient

## 项目结构

```
src/com/nk/
├── baidu/           # 百度相关工具（Excel读取）
├── classbasic/      # Java基础类示例
├── common/          # 通用工具
├── db/              # 数据库工具（Oracle、MySQL）
│   ├── util/        # 数据库操作工具
│   ├── entity/      # 实体类
│   └── constant/    # 常量定义
├── designpatterns/  # 设计模式示例
│   └── future/      # Future模式
├── email/           # 邮件发送
├── excel/           # Excel读写处理
│   ├── util/        # Excel工具
│   ├── handler/     # 处理器
│   ├── entity/      # 实体类
│   ├── annotation/  # 注解
│   └── service/     # 服务接口
├── file/            # 文件操作
├── hbase/           # HBase操作
├── image/           # 图片处理
├── interviews/      # 面试题示例
├── jarfile/         # Jar文件操作
├── jython/          # Python集成
├── kafka/           # Kafka操作
│   ├── producer/    # 生产者
│   ├── consumer/    # 消费者
│   └── utils/       # 工具类
├── lock/            # 锁机制
│   └── staticlock/  # 静态锁
├── log/             # 日志工具
├── netty/           # Netty网络编程
│   └── codec/       # 编解码器
├── redis/           # Redis操作
├── runtime/         # 运行时
├── security/        # 安全加密
├── securityfile/    # 文件安全
├── sort/            # 排序算法
├── ticket/          # 票务系统
├── util/            # 通用工具
├── webmagic/        # 网页爬虫
├── wechat/          # 微信相关
└── xml/             # XML处理

batch_remote_exec/   # Python批量远程命令执行工具
```

## 模块说明

### 数据库 (db)

- `DbToVoUtils` - 数据库转VO对象
- `DaoUtils` - DAO工具类
- `DataSource` - 数据源管理
- `DeleteTablesUtil` - 删除表工具
- `SearchDatabaseUtil` - 数据库搜索工具

### Excel处理 (excel)

- 支持Excel 2003和2007格式
- 注解方式定义导入导出
- 灵活的处理器机制

### Kafka

- 生产者/消费者示例
- 自定义分区器
- 回调机制

### Netty

- 基于Jackson的JSON编解码
- 服务端/客户端示例

### 锁机制 (lock)

- Redis分布式锁
- 读写锁
- 静态锁

### 邮件 (email)

- 支持SMTP发送
- HTML邮件
- 附件支持

### 安全 (security)

- 加密/解密工具
- 文件安全处理

### 批量远程执行 (batch_remote_exec)

Python编写的批量SSH命令执行工具。

#### 安装

```bash
cd batch_remote_exec
pip install -r requirements.txt
```

#### 配置

1. 复制示例配置文件：
```bash
cp nodes.yaml.example nodes.yaml
```

2. 编辑 `nodes.yaml` 配置服务器信息

#### 使用

```bash
# 执行命令
python batch_exec.py -c nodes.yaml -x "uptime"

# 执行脚本
python batch_exec.py -c nodes.yaml -s ./scripts/deploy.sh

# 指定节点
python batch_exec.py -c nodes.yaml -x "df -h" --node web-server-1
```

详细使用方法见 [batch_remote_exec/README.md](batch_remote_exec/README.md)

## 编译运行

```bash
# 编译项目
mvn compile

# 打包
mvn package

# 运行测试
mvn test
```

## 环境要求

- JDK 1.6+
- Maven 3.0+
- Python 3.6+ (for batch_remote_exec)

## License

MIT