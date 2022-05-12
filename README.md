# chain sdk for java

SDK提供了私钥管理、发送交易、数据查询等功能。

> 规范 
>> java类与业务模块的对应关系。比如Wasm模块， 状态数据查询的类名为QueryWasm，构建消息体的TxWasm。其他模块依次推之。

## 1 功能介绍
### 1.1 私钥管理
私钥对象 类`com.glodnet.chain.util.Key`及助记词对象类 `com.glodnet.chain.util.Mnemonic`的管理
> 助记词推出私钥，私钥不可推出助记词。建议助记词进行私钥备份。
- 私钥存储接口类 `com.glodnet.chain.keys.IKeyDAO`
  - 新增私钥 `void write(String name, Key key) `
  - 读取私钥 `Key read(String name)`
  - 删除私钥 `void delete(String name)`
  - 加密 `byte[] encrypt(byte[] privKey, String password)`
  - 解密 `byte[] decrypt(byte[] encrptedPrivKey, String password)`
  ```java
  // 私钥存储接口类 --- 内存Map
  import com.glodnet.chain.keys.impl.DefaultKeyDAOImpl;
  ```
- 私钥管理服务接口类 `com.glodnet.chain.keys.IKeyService`
  - 随机生成私钥返回助记词 `Mnemonic addKey(String name, String password)`
  - 导入助记词恢复私钥 `String recoverKey(String name, String password, String mnemonic, boolean derive, int index, String saltPassword)`- 导入
  - keystore私钥导入 `String importFromKeystore(String name, String keyPassword, String keystorePassword, String keystore)`
  - 导出keystore私钥 `String exportKeystore(String name, String keyPassword, String keystorePassword, File destinationDirectory) `
  - 删除私钥 `void deleteKey(String name, String password)`
  - 获取私钥对象 `Key getKey(String name, String password)`
  - 显示地址`String showAddress(String name)`
  - 签名 `byte[] sign(String name, String password, byte[] signdoc)`
  - 签名交易 `TxOuterClass.Tx signTx(TxOuterClass.Tx tx, String name, String password, String chainID,  Long accountNumber, Long sequence, boolean overwriteSig) `
```java
// secp256k1私钥服务实现类 DefaultKeyServiceImpl(IKeyDAO keyDAO) 
import com.glodnet.chain.keys.impl.DefaultKeyServiceImpl; 

// sm2私钥服务实现类 SM2KeyServiceImpl(IKeyDAO keyDAO) 
import com.glodnet.chain.keys.impl.SM2KeyServiceImpl; 
```

### 1.2 发送交易

使用Tx类`com.glodnet.chain.Tx`对象，可以发送交易

- 发送交易 `String send(String name, String password, BuildTxOptions options)`

```java
		/**
     * 创建Tx对象
     *
     * @param chainID        网络ChainID。 例：gnchain
     * @param gasPrice       gas手续费。自动计算feeAmount时使用。 例： 0.00002ugnc
     * @param gasAdjustment  gas调整系数。自动计算gasLimit时使用。例： 1.1
     * @return Tx对象
     */
    public Tx(HttpClient client, IKeyDAO keyDAO, SignAlgo signAlgo, String chainID, DecCoin gasPrice, BigDecimal gasAdjustment)
```

- 构建交易参数类`com.glodnet.chain.BuildTxOptions`

  > 必选字段
  >
  > > 消息体列表 msgs
  > >
  > > 交易发送者 sender

```java
public class BuildTxOptions {
    private List<Any> msgs;
    private String memo;

    // Optional parameters
    @Builder.Default
    private String sender = "";
    @Builder.Default
    private Long accountNumber = 0L;
    @Builder.Default
    private Long sequence = 0L;
    @Builder.Default
    private Long gasLimit = 0L;
    @Builder.Default
    private CoinOuterClass.Coin feeAmount = null;
    @Builder.Default
    private String feeGranter = "";
    @Builder.Default
    private Long timeoutHeight = 0L;
}
```



### 1.3 数据查询

数据查询根据相关性定义在不同的查询类。

- 节点相关的查询`com.glodnet.chain.Node`
- 账户相关的查询 `com.glodnet.chain.QueryAuth`
- 账户金额的查询 `com.glodnet.chain.QueryBank`
- 合约相关的查询 `com.glodnet.chain.QueryWasm`

## 2 模块介绍

### 2.1 合约模块wasm

- 构建消息体 `com.glodnet.chain.TxWasm`, 用于填充交易构建参数`com.glodnet.chain.BuildTxOptions`

    	- 合约代码 `NewMsgStoreCode(String sender, String wasmFile, String instantiateByAddress)`

  ```java
  		/**
       * 创建消息体---上传合约代码
       *
       * @param sender        用户地址
       * @param wasmFile      编译文件 *.wasm
       * @param instantiateByAddress  实例化用户地址, 为空任何用户可实例化
       * @return MsgStoreCode对象
       */
  ```

     - 部署合约 `NewMsgInstantiateContract(String sender, Long codeID, String initArgs, String label, String admin, List<Coin> amounts)`

  ```java
   		/**
       *  创建消息体--- 部署合约
       *
       * @param sender   用户地址
       * @param codeID   合约代码ID
       * @param initArgs 实例参数
       * @param label    标签
       * @param admin    管理员
       * @param amounts  金额
       * @return MsgInstantiateContract对象
       */
  ```

     - 执行合约`NewMsgExecuteContract(String sender, String contractAddr, String execArgs, List<Coin> amounts)`

  ```java
  		/**
       *  创建消息体--- 执行合约
       *
       * @param sender        用户地址
       * @param contractAddr  合约地址
       * @param execArgs      执行参数
       * @param amounts  金额
       * @return MsgExecuteContract对象
       */
  ```

     - 升级合约`NewMsgMigrateContract(String sender, String contractAddr, Long codeID, String migrateArgs)`

  ```java
  		/**
       *  创建消息体--- 升级合约
       *
       * @param sender        用户地址
       * @param contractAddr  合约地址
       * @param codeID   合约代码ID
       * @param migrateArgs 升级参数
       * @return MsgMigrateContract
       */
  ```

- 数据查询 `com.glodnet.chain.QueryWasm`
  - 合约代码列表 `getCodes(HttpClient client, String nextKey)`
  - 合约代码详情 `getCode(HttpClient client, Long codeID)`
  - 查询部署合约详情 `getContractInfo(HttpClient client, String address) `

## 3 用例演示

- 上传合约代码

 ```java
 HttpClient client = new HttpClient("http://127.0.0.1:1317") // 节点api访问地址
 Tx tx = new Tx(client, null, SignAlgo.SM2,"gnchain", CoinOuterClass.DecCoin.newBuilder().build(), new BigDecimal(1.1));
 
 String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";
 
 // 导入助记词到私钥管理服务
 IKeyService keyService = tx.getKeyService();
 keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
 String address = keyService.showAddress("alice");
 
 List<Any> msgs = new ArrayList<>();
 // 上传合约消息体构建
 msgs.add(TxWasm.NewMsgStoreCode(address, "cw_nameservice.wasm", ""));
 
 // 发送交易
 String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                       .sender(address)
                       .msgs(msgs)
                       .memo("")
                       .build());
 System.out.println(hash);
 ```

- 部署合约

```java
HttpClient client = new HttpClient("http://127.0.0.1:1317") // 节点api访问地址
Tx tx = new Tx(client, null, SignAlgo.SM2,"gnchain", CoinOuterClass.DecCoin.newBuilder().build(), new BigDecimal(1.1));

String mnemonic = "apology false junior asset sphere puppy upset dirt miracle rice horn spell ring vast wrist crisp snake oak give cement pause swallow barely clever";

// 导入助记词到私钥管理服务
IKeyService keyService = tx.getKeyService();
keyService.recoverKey("alice", "123456", mnemonic, true, 0, "");
String address = keyService.showAddress("alice");

List<Any> msgs = new ArrayList<>();
// 上传合约消息体构建
String initArgs = "{\"purchase_price\":{\"amount\":\"100\",\"denom\":\"ugnc\"},\"transfer_price\":{\"amount\":\"999\",\"denom\":\"ugnc\"}}";
msgs.add(TxWasm.NewMsgInstantiateContract(address, 15L, initArgs, "label", "", new ArrayList<>()));

// 发送交易
String hash = tx.send("alice", "123456", BuildTxOptions.builder()
                      .sender(address)
                      .msgs(msgs)
                      .memo("")
                      .build());
System.out.println(hash);
```
