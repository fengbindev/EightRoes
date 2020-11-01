package com.ssrs.qiniuyun.util;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.ssrs.qiniuyun.config.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ssrs
 */
public class QiniuConfigUtil {

    /**
     * 得到上传凭证和资源下载路径
     *
     * @param bocket 指定是在共有仓库还是私有仓库中，0为私有仓库，非0为共有仓库
     * @param key    资源的key
     * @return 一个存有上传凭证
     */
    public static Map<String, String> getUploadToken(int bocket, String key) {
        Map<String, String> data = new HashMap<>();

        // 将上传凭证放入map中
        data.put("token", getuplodTokenByKey(key));

        // 图片地址
        data.put("url", getUrl(bocket, key));
        return data;
    }

    /**
     * 得到资源的访问地址，也是下载地址
     *
     * @param bocket 指定是在共有仓库还是私有仓库中，0为私有仓库，非0为共有仓库
     * @param key    资源的key
     * @return 资源的访问地址
     */
    private static String getUrl(int bocket, String key) {
        if (bocket == 0) {
            return getstaticDownloadToken(QiniuPriviteURL.getValue() + key);
        } else {
            return QiniuPublicURL.getValue() + key;
        }
    }


    public static String getuplodTokenByKey(String key) {


        // 第一步： 先生成一个Auth验证
        Auth auth = getQiniuAuth();


        // 第二步： 使用StringMap工具类拼装参数
        StringMap putPolicy = new StringMap();

        /*
         * 指定上传的目标资源空间 Bucket 和资源键 Key（最大为 750 字节）。有三种格式：
         *   1. <bucket>，表示允许用户上传文件到指定的 bucket。在这种格式下文件只能新增
         *       （分片上传需要指定insertOnly为1才是新增，否则也为覆盖上传），若已存在同名资源（且文件内容/etag不一致），
         *       上传会失败；若已存在资源的内容/etag一致，则上传会返回成功。
         *
         *
         *   2. <bucket>:<key>，表示只允许用户上传指定 key 的文件。
         *       在这种格式下文件默认允许修改，若已存在同名资源则会被覆盖。
         *       如果只希望上传指定 key 的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1。
         *
         *
         *   3. <bucket>:<keyPrefix>，表示只允许用户上传指定以 keyPrefix 为前缀的文件，
         *       当且仅当 isPrefixalScope 字段为 1 时生效，isPrefixalScope 为 1 时无法覆盖上传。
         *
         * type : String
         * */
        putPolicy.put("scope", QiniuPublicBucket.getValue());


        // 上传凭证有效截止时间。Unix时间戳，单位为秒。该截止时间为上传完成后，在七牛空间生成文件的校验时间，
        // 而非上传的开始时间，一般建议设置为上传开始时间 + 3600s
        // type : uint32
        putPolicy.put("deadline", (System.currentTimeMillis() / 1000L + 3600));


        // Web 端文件上传成功后，浏览器执行 303 跳转的 URL。通常用于表单上传。文件上传成功后会跳转到
        // <returnUrl>?upload_ret=<queryString>，<queryString>包含 returnBody 内容。
        // 如不设置 returnUrl，则直接将 returnUrlnBody 的内容返回给客户端。
        // type : String
//        putPolicy.put("returnUrl","");



        /*
         *上传成功后，自定义七牛云最终返回給上传端（在指定 returnUrl 时是携带在跳转路径参数中）的数据。
         * 支持魔法变量和自定义变量。returnBody 要求是合法的 JSON 文本。
         * 例如 {"key": $(key), "hash": $(etag), "w": $(imageInfo.width), "h": $(imageInfo.height)}。
         */
        // type : String
        putPolicy.put("returnBody", "{\"key\": $(key), \"hash\": $(etag), \"w\": $(imageInfo.width), \"h\": $(imageInfo.height)}");




        /*
         *
         * 上传成功后，七牛云向业务服务器发送 Content-Type: application/x-www-form-urlencoded 的 POST 请求。
         * 业务服务器可以通过直接读取请求的 query 来获得该字段，支持魔法变量和自定义变量。
         * callbackBody 要求是合法的 url query string。
         * 例如key=$(key)&hash=$(etag)&w=$(imageInfo.width)&h=$(imageInfo.height)。
         * 如果callbackBodyType指定为application/json，则callbackBody应为json格式，
         * 例如:{"key":"$(key)","hash":"$(etag)","w":"$(imageInfo.width)","h":"$(imageInfo.height)"}。
         * */
        // type : String
        putPolicy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"w\":\"$(imageInfo.width)\",\"h\":\"$(imageInfo.height)\"}");


        //上传成功后，七牛云向业务服务器发送回调通知 callbackBody 的 Content-Type。
        // 默认为 application/x-www-form-urlencoded，也可设置为 application/json。
        // type : String
        putPolicy.put("callbackBodyType", "application/json");


        // 第三步： 生成一个上传凭证

        return auth.uploadToken(QiniuPublicBucket.getValue(), key, (System.currentTimeMillis() / 1000L + 3600), putPolicy);
    }


    /**
     * 获得一个私有空间下载的凭证,传入的参数是按照公开仓库访问方式生成的URL
     *
     * @param baseUrl 按照公开仓库生成URL的方式生成的URL
     * @return
     */
    public static String getstaticDownloadToken(String baseUrl) {
        Auth auth = getQiniuAuth();
        return auth.privateDownloadUrl(baseUrl);
    }


    /**
     * 指定在七牛云上面的存储路径
     *
     * @return
     */
    public String getDynamicUpToken(String resporties) {

        Auth auth = getQiniuAuth();

        StringMap putPolicy = new StringMap();

        putPolicy.put("scope", resporties);

        putPolicy.put("deadline", (System.currentTimeMillis() / 1000L + 3600));

        putPolicy.put("returnBody", "{\"key\": $(key), \"hash\": $(etag), \"w\": $(imageInfo.width), \"h\": $(imageInfo.height)}");

        putPolicy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"w\":\"$(imageInfo.width)\",\"h\":\"$(imageInfo.height)\"}");

        putPolicy.put("callbackBodyType", "application/json");

        return auth.uploadToken(QiniuPublicBucket.getValue(), null, (System.currentTimeMillis() / 1000L + 3600), putPolicy);
    }


    /**
     * 可以根据需求自己封装要生成的凭证
     * 默认得到一个6分钟后过期的动态上传凭证，这个凭证可以让文件上传到默认仓库中
     * <p>
     * 这个是所有上传凭证的原型，不能动
     *
     * @return
     */
    public static String getDefaultDynamicUpToken() {

        // 第一步： 先生成一个Auth验证
        Auth auth = getQiniuAuth();


        // 第二步： 使用StringMap工具类拼装参数
        StringMap putPolicy = new StringMap();

        /*
         * 指定上传的目标资源空间 Bucket 和资源键 Key（最大为 750 字节）。有三种格式：
         *   1. <bucket>，表示允许用户上传文件到指定的 bucket。在这种格式下文件只能新增
         *       （分片上传需要指定insertOnly为1才是新增，否则也为覆盖上传），若已存在同名资源（且文件内容/etag不一致），
         *       上传会失败；若已存在资源的内容/etag一致，则上传会返回成功。
         *
         *
         *   2. <bucket>:<key>，表示只允许用户上传指定 key 的文件。
         *       在这种格式下文件默认允许修改，若已存在同名资源则会被覆盖。
         *       如果只希望上传指定 key 的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1。
         *
         *
         *   3. <bucket>:<keyPrefix>，表示只允许用户上传指定以 keyPrefix 为前缀的文件，
         *       当且仅当 isPrefixalScope 字段为 1 时生效，isPrefixalScope 为 1 时无法覆盖上传。
         *
         * type : String
         * */
        putPolicy.put("scope", QiniuPublicBucket.getValue());


        // 若为 1，表示允许用户上传以 scope 的 keyPrefix 为前缀的文件。
        // type : int
//        putPolicy.put("isPrefixalScope",1);


        // 上传凭证有效截止时间。Unix时间戳，单位为秒。该截止时间为上传完成后，在七牛空间生成文件的校验时间，
        // 而非上传的开始时间，一般建议设置为上传开始时间 + 3600s
        // type : uint32
        putPolicy.put("deadline", (System.currentTimeMillis() / 1000L + 3600));


        // 限定为新增语意。如果设置为非 0 值，则无论 scope 设置为什么形式，仅能以新增模式上传文件。
        // type : int
//        putPolicy.put("insertOnly",0);


        // 唯一属主标识。特殊场景下非常有用，例如根据 App-Client 标识给图片或视频打水印。
        // type : String
//        putPolicy.put("endUser","");


        // Web 端文件上传成功后，浏览器执行 303 跳转的 URL。通常用于表单上传。文件上传成功后会跳转到
        // <returnUrl>?upload_ret=<queryString>，<queryString>包含 returnBody 内容。
        // 如不设置 returnUrl，则直接将 returnUrlnBody 的内容返回给客户端。
        // type : String
//        putPolicy.put("returnUrl","");



        /*
         *上传成功后，自定义七牛云最终返回給上传端（在指定 returnUrl 时是携带在跳转路径参数中）的数据。
         * 支持魔法变量和自定义变量。returnBody 要求是合法的 JSON 文本。
         * 例如 {"key": $(key), "hash": $(etag), "w": $(imageInfo.width), "h": $(imageInfo.height)}。
         */
        // type : String
        putPolicy.put("returnBody", "{\"key\": $(key), \"hash\": $(etag), \"w\": $(imageInfo.width), \"h\": $(imageInfo.height)}");



        /*
         * 上传成功后，七牛云向业务服务器发送 POST 请求的 URL。
         * 必须是公网上可以正常进行 POST 请求并能响应 HTTP/1.1 200 OK 的有效 URL。
         * 另外，为了给客户端有一致的体验，我们要求 callbackUrl 返回包 Content-Type 为 "application/json",
         * 即返回的内容必须是合法的 JSON 文本。出于高可用的考虑，本字段允许设置多个 callbackUrl（用英文符号 ; 分隔），
         * 在前一个 callbackUrl 请求失败的时候会依次重试下一个 callbackUrl。
         *
         * 一个典型例子是：http://<ip1>/callback;http://<ip2>/callback，并同时指定下面的 callbackHost 字段。
         * 在 callbackUrl 中使用 ip 的好处是减少对 dns 解析的依赖，可改善回调的性能和稳定性。
         * 指定 callbackUrl，必须指定 callbackbody，且值不能为空。
         * */
        // type : String
//        putPolicy.put("callbackUrl","");


        //上传成功后，七牛云向业务服务器发送回调通知时的 Host 值。与 callbackUrl 配合使用，仅当设置了 callbackUrl 时才有效。
//        putPolicy.put("callbackHost","");




        /*
         *
         * 上传成功后，七牛云向业务服务器发送 Content-Type: application/x-www-form-urlencoded 的 POST 请求。
         * 业务服务器可以通过直接读取请求的 query 来获得该字段，支持魔法变量和自定义变量。
         * callbackBody 要求是合法的 url query string。
         * 例如key=$(key)&hash=$(etag)&w=$(imageInfo.width)&h=$(imageInfo.height)。
         * 如果callbackBodyType指定为application/json，则callbackBody应为json格式，
         * 例如:{"key":"$(key)","hash":"$(etag)","w":"$(imageInfo.width)","h":"$(imageInfo.height)"}。
         * */
        // type : String
        putPolicy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"w\":\"$(imageInfo.width)\",\"h\":\"$(imageInfo.height)\"}");


        //上传成功后，七牛云向业务服务器发送回调通知 callbackBody 的 Content-Type。
        // 默认为 application/x-www-form-urlencoded，也可设置为 application/json。
        // type : String
        putPolicy.put("callbackBodyType", "application/json");



        /*
         * 资源上传成功后触发执行的预转持久化处理指令列表。支持魔法变量和自定义变量。
         * 每个指令是一个 API 规格字符串，多个指令用;分隔。请参阅persistenOps详解与示例。
         * 同时添加 persistentPipeline 字段，使用专用队列处理，请参阅persistentPipeline。
         * */
        // type : String
//        putPolicy.put("persistentOps","");




        /*
         * 接收持久化处理结果通知的 URL。必须是公网上可以正常进行 POST 请求并能响应 HTTP/1.1 200 OK 的有效 URL。
         * 该 URL 获取的内容和持久化处理状态查询的处理结果一致。
         * 发送 body 格式是 Content-Type 为 application/json 的 POST 请求，
         * 需要按照读取流的形式读取请求的body才能获取
         * */
        // type : String
//        putPolicy.put("persistentNotifyUrl","");


        //转码队列名。资源上传成功后，触发转码时指定独立的队列进行转码。为空则表示使用公用队列，处理速度比较慢。建议使用专用队列。
        // type : String
//        putPolicy.put("persistentPipeline","");


        //saveKey的优先级设置。为 true 时，saveKey不能为空，会忽略客户端指定的key，强制使用saveKey进行文件命名。参数不设置时，默认值为false
        // type : String
//        putPolicy.put("forceSaveKey",false);


        //自定义资源名。支持魔法变量和自定义变量。forceSaveKey 为false时，这个字段仅当用户上传的时候没有主动指定 key 时起作用;
        // forceSaveKey 为true时，将强制按这个字段的格式命名。
        // type : String
//        putPolicy.put("saveKey","");


        // 限定上传文件大小最小值，单位Byte。
        // type : int64
//        putPolicy.put("fsizeMin",1024);


        // 限定上传文件大小最大值，单位Byte。超过限制上传文件大小的最大值会被判为上传失败，返回 413 状态码。
        // type : int64
//        putPolicy.put("fsizeLimit",2048);




        /*
         * 开启 MimeType 侦测功能。设为非 0 值，则忽略上传端传递的文件 MimeType 信息，使用七牛服务器侦测内容后的判断结果。
         * 默认设为 0 值，如上传端指定了 MimeType 则直接使用该值，否则按如下顺序侦测 MimeType 值：
         * 1. 检查文件扩展名；
         * 2. 检查 Key 扩展名；
         * 3. 侦测内容。
         * 如不能侦测出正确的值，会默认使用 application/octet-stream。
         */
        // type : int
//        putPolicy.put("detectMime",1);





        /*
         * 限定用户上传的文件类型。指定本字段值，七牛服务器会侦测文件内容以判断 MimeType，再用判断值跟指定值进行匹配，匹配成功则允许上传，匹配失败则返回 403 状态码。示例：
         * image/*表示只允许上传图片类型
         * image/jpeg;image/png表示只允许上传jpg和png类型的图片
         * !application/json;text/plain表示禁止上传json文本和纯文本。注意最前面的感叹号！
         */
        // type : String
//        putPolicy.put("mimeLimit","");


        // 文件存储类型。0 为普通存储（默认），1 为低频存储。
        // type : int
//        putPolicy.put("fileType",0);


        // 第三步： 生成一个上传凭证

        return auth.uploadToken(QiniuPublicBucket.getValue(), null, (System.currentTimeMillis() / 1000L + 3600), putPolicy);
    }


    public static Auth getQiniuAuth() {
        return Auth.create(QiniuAccessKey.getValue(), QiniuSecretKey.getValue());
    }

    public static Configuration getQiniuConfiguration() {
        return new Configuration(QiniuRegion.getValue());
    }

    public static BucketManager getQiniuBucketManager() {
        return new BucketManager(getQiniuAuth(), getQiniuConfiguration());
    }

    public static UploadManager getQiniuUploadManager() {
        return new UploadManager(getQiniuConfiguration());
    }


    public static void main(String[] args) {
        System.out.println(getUploadToken(1, "test"));
    }
}
