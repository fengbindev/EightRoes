# 阿里云OSS

阿里云对象存储服务（Object Storage Service，简称 OSS），是阿里云提供的海量、安全、低成本、高可靠的云存储服务。其数据设计持久性不低于 99.9999999999%（12 个 9），服务设计可用性（或业务连续性）不低于 99.995%。

OSS 具有与平台无关的 RESTful API 接口，您可以在任何应用、任何时间、任何地点存储和访问任意类型的数据。
 
您可以使用阿里云提供的 API、SDK 接口或者 OSS 迁移工具轻松地将海量数据移入或移出阿里云 OSS。数据存储到阿里云 OSS 以后，您可以选择标准存储（Standard）作为移动应用、大型网站、图片分享或热点音视频的主要存储方式，也可以选择成本更低、存储期限更长的低频访问存储（Infrequent Access）和归档存储（Archive）作为不经常访问数据的存储方式。
  
  
## OSS可以做什么？

它可以搭建自己的床图（配合PicGO），备份网站，数据库（配合宝塔）等。也可以在开发过程中充当文件服务器，阿里云OSS宣称99.9999999999%的可靠性，另外加上价格相对低廉，是作为图片/文件服务器的不二之选。

### 存储空间（Bucket）

存储空间是用户用于存储对象（Object）的容器，所有的对象都必须隶属于某个存储空间。存储空间具有各种配置属性，包括地域、访问权限、存储类型等。用户可以根据实际需求，创建不同类型的存储空间来存储不同的数据。

- 同一个存储空间的内部是扁平的，没有文件系统的目录等概念，所有的对象都直接隶属于其对应的存储空间。
- 每个用户可以拥有多个存储空间。
- 存储空间的名称在 OSS 范围内必须是全局唯一的，一旦创建之后无法修改名称。
- 存储空间内部的对象数目没有限制。

存储空间的命名规范如下：

- 只能包括小写字母、数字和短横线（-）
- 必须以小写字母或者数字开头和结尾
- 长度必须在 3–63 字节之间

### 对象/文件（Object）

对象是 OSS 存储数据的基本单元，也被称为 OSS 的文件。对象由元信息（Object Meta），用户数据（Data）和文件名（Key）组成。对象由存储空间内部唯一的 Key 来标识。对象元信息是一组键值对，表示了对象的一些属性，比如最后修改时间、大小等信息，同时用户也可以在元信息中存储一些自定义的信息。

对象的生命周期是从上传成功到被删除为止。在整个生命周期内，只有通过追加上传的 Object 可以继续通过追加上传写入数据，其他上传方式上传的 Object 内容无法编辑，您可以通过重复上传同名的对象来覆盖之前的对象

对象的命名规范如下：

- 使用 UTF-8 编码
- 长度必须在 1–1023 字节之间
- 不能以正斜线（/）或者反斜线（\）开头

### Endpoint（访问域名）

Endpoint 表示 OSS 对外服务的访问域名。OSS 以 HTTP RESTful API 的形式对外提供服务，当访问不同的 Region 的时候，需要不同的域名。通过内网和外网访问同一个 Region 所需要的 Endpoint 也是不同的。例如杭州 Region 的外网 Endpoint 是 oss-cn-hangzhou.aliyuncs.com，内网 Endpoint 是 oss-cn-hangzhou-internal.aliyuncs.com。

### AccessKey（访问密钥）

AccessKey（简称 AK）指的是访问身份验证中用到的 AccessKeyId 和 AccessKeySecret。OSS 通过使用 AccessKeyId 和 AccessKeySecret 对称加密的方法来验证某个请求的发送者身份。AccessKeyId 用于标识用户；AccessKeySecret 是用户用于加密签名字符串和 OSS 用来验证签名字符串的密钥，必须保密。对于 OSS 来说，AccessKey 的来源有：

- Bucket 的拥有者申请的 AccessKey
- 被 Bucket 的拥有者通过 RAM 授权给第三方请求者的 AccessKey
- 被 Bucket 的拥有者通过 STS 授权给第三方请求者的 AccessKey


## AliyunOSSUtil.java 工具类代码 参数介绍

| 参数名 | 含义 |
|  ----  | ----  |
| oranName | 代表用户传过来未经处理的文件名 例如/img/a.jpg |
| objectName | 代表去掉前面/ 加上uuid后的文件名 如img/330ddd7feb6d456f8ee97092d7675c90a.jgp |
| realName | 指的是存放在OSS中的全路径 |

### 属性介绍

| 属性名 | 介绍 |
|  ----  | ----  |
| accessKeyId | 阿里云AccessKey |
| accessKeySecret| 阿里云AccessKeySecret |
| endpoint | 阿里云oss Endpoint |
| bucketName | 阿里云oss BucketName|

### 方法介绍 

| 方法名 | 介绍 |
|  ----  | ----  |
| getURLHead() | 返回url头信息,即https://+bucketName+endpoint |
| String getObjectName(String fileURL)|	通过文件URL反向解析文件名 |
| List getObjectNames(List fileURLs)	| 批量获取 objectName |
| String getRealName(String oranName)	| 获取存放在OSS中的全路径 |
| void printUploadSuccessInfo(String fileURL)	| 上传成功后打印文件的存储地址，测试方法 |
| void printDeleteSuccessInfo(String fileURL)	| 删除成功后打印文件的存储地址，测试方法 |
| String getRandomImageName(String oranName)	| 在oranName之前加入一个uuid并返回新的oranName |
| String createBucket(String bucket)	| 创建一个新的Bucket |
| String getBucketName(String fileURL)	| 根据url获取bucketName |
| String upLoadTextFile(String oranFileName, String content)	| 上传一个文本文件到服务器上，获取realName |
| String uploadBytesFile(String oranFileName, byte[] content)	| 上传一个byte数组到服务器上，可用于web中的图片提交，获取realName |
| String uploadNetworkFlows(String oranFileName, String url)	| 上传网络流，获取realName |
| String uploadFileInputSteam(String oranFileName, File file)	| 上传文件流，获取realName |
| String uploadFileInputSteam(String oranFileName, InputStream inputStream)	| 上传文件流，获取realName |
| String uploadFileInputSteam(String oranFileName, MultipartFile file)	| 上传文件流，获取realName |
| String uploadLocalFile(String oranFileName, String localFileName)	| 上传一个本地文件，获取realName |
| void deleteFile(String fileURL)	| 删除指定路径下的一个文件 |
| void deleteFile(List fileURL)	| 删除指定路径下的多个文件 |
| boolean exists(String fileURL)	| 存在为true，不存在为false |
| void downloadFileToLoacal(String fileURL, String localFileName)	| 从OSS中下载一个文件到本地 |
| StringBuffer downloadStream(String fileURL)	| 以流的方式读取一个文件 并打印 返回读取到的内容 |
| Object getCloudPropertiesGetValue(String fileName, String key)	| 以流的方式读取一个云端properties文件的key对应的value 并打印 |