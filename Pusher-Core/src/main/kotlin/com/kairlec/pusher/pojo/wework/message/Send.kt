@file:Suppress("SpellCheckingInspection", "PropertyName", "unused", "ArrayInDataClass")

package com.kairlec.pusher.pojo.wework.message


interface IToAble {
    /**
     * 成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     */
    var touser: String

    /**
     * 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
     */
    var toparty: String

    /**
     * 标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
     */
    var totag: String
}

interface ISaveAble {
    /**
     * 表示是否是保密消息，0表示否，1表示是，默认0
     */
    var safe: Int?
}

interface IEnableIdTransAble {
    /**
     * 表示是否开启id转译，0表示否，1表示是，默认0
     */
    var enable_id_trans: Int?
}

interface IDuplicateCheckAble {
    /**
     * 表示是否开启重复消息检查，0表示否，1表示是，默认0
     */
    var enable_duplicate_check: Int?

    /**
     * 表示是否重复消息检查的时间间隔，默认1800s，最大不超过4小时
     */
    var duplicate_check_interval: Int?
}

data class Content(
        val content: String
)

/**
 * 文本消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E6%96%87%E6%9C%AC%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param text content字段可以支持换行、以及A标签，即可打开自定义的网页（可参考以上示例代码）(注意：换行符请用转义过的\n)
 */
data class Text(
        val agentid: Int,
        val text: Content,
) : IToAble, IDuplicateCheckAble, IEnableIdTransAble, ISaveAble {
    override lateinit var toparty: String
    override lateinit var totag: String
    override lateinit var touser: String
    override var safe: Int? = null
    override var enable_id_trans: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：text
     */
    val msgtype: String = "text"
}

/**
 * @param media_id 媒体文件id，可以调用上传临时素材接口获取
 */
data class Media(
        val media_id: String
)

/**
 * 图片消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E5%9B%BE%E7%89%87%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param image 图像最小5B，最大2MB，支持JPG,PNG格式
 */
data class Image(
        val agentid: Int,
        val image: Media,
) : IToAble, ISaveAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var safe: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：image
     */
    val msgtype: String = "image"
}

/**
 * 语音消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E8%AF%AD%E9%9F%B3%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param voice 音频,最小5B，最大2MB，播放长度不超过60s，仅支持AMR格式
 */
data class Voice(
        val agentid: Int,
        val voice: Media,
) : IToAble, ISaveAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var safe: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：voice
     */
    val msgtype: String = "voice"
}

/**
 * @param media_id 视频媒体文件id，可以调用上传临时素材接口获取
 */
data class VideoMedia(
        val media_id: String
) {
    /**
     * 视频消息的标题，不超过128个字节，超过会自动截断
     */
    var title: String? = null

    /**
     * 视频消息的描述，不超过512个字节，超过会自动截断
     */
    var description: String? = null
}

/**
 * 视频消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E8%A7%86%E9%A2%91%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param video 视频，最小5B，最大10MB，支持MP4格式
 */
data class Video(
        val agentid: Int,
        val video: VideoMedia,
) : IToAble, ISaveAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var safe: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：video
     */
    val msgtype: String = "video"
}

/**
 * 文件消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E6%96%87%E4%BB%B6%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param file 文件，最小5B，最大20MB
 */
data class File(
        val agentid: Int,
        val file: Media,
) : IToAble, ISaveAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var safe: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：file
     */
    val msgtype: String = "file"
}

/**
 * @param title 标题，不超过128个字节，超过会自动截断（支持id转译）
 * @param description 描述，不超过512个字节，超过会自动截断（支持id转译）
 * @param url 点击后跳转的链接。
 */
data class InnerTextCard(
        val title: String,
        val description: String,
        val url: String,
) {
    /**
     * 按钮文字。 默认为“详情”， 不超过4个文字，超过自动截断。
     */
    var btntxt: String? = "详情"
}

/**
 * 文本卡片消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E6%96%87%E6%9C%AC%E5%8D%A1%E7%89%87%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param textcard 文本卡片消息体
 */
data class TextCard(
        val agentid: Int,
        val textcard: InnerTextCard,
) : IToAble, IEnableIdTransAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var enable_id_trans: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：textcard
     */
    val msgtype: String = "textcard"
}

/**
 * @param title 标题，不超过128个字节，超过会自动截断（支持id转译）
 * @param url 点击后跳转的链接。
 */
data class NewsArticles(
        val title: String,
        val url: String,
) {
    /**
     * 描述，不超过512个字节，超过会自动截断（支持id转译）
     */
    var description: String? = null

    /**
     * 图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图 1068*455，小图150*150。
     */
    var picurl: String? = null
}

/**
 * @param articles 图文消息，一个图文消息支持1到8条图文
 */
data class InnerNews(
        val articles: Array<NewsArticles>
)

/**
 * 图文消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E5%9B%BE%E6%96%87%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param news 图文消息体
 */
data class News(
        val agentid: Int,
        val news: InnerNews,
) : IToAble, IEnableIdTransAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var enable_id_trans: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：news
     */
    val msgtype: String = "news"
}

/**
 * @param title 标题，不超过128个字节，超过会自动截断（支持id转译）
 * @param thumb_media_id 图文消息缩略图的media_id, 可以通过素材管理接口获得。此处thumb_media_id即上传接口返回的media_id
 * @param content 图文消息的内容，支持html标签，不超过666 K个字节（支持id转译）
 */
data class MpNewsArticles(
        val title: String,
        val thumb_media_id: String,
        val content: String,
) {
    /**
     * 图文消息的作者，不超过64个字节
     */
    var author: String? = null

    /**
     * 图文消息点击“阅读原文”之后的页面链接
     */
    var content_source_url: String? = null

    /**
     * 图文消息的描述，不超过512个字节，超过会自动截断（支持id转译）
     */
    var digest: String? = null
}

/**
 * @param articles 图文消息，一个图文消息支持1到8条图文
 */
data class InnerMpNews(
        val articles: Array<MpNewsArticles>
)

/**
 * 图文消息（mpnews）
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E5%9B%BE%E6%96%87%E6%B6%88%E6%81%AF%EF%BC%88mpnews%EF%BC%89
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param mpnews 图文消息体
 */
data class MpNews(
        val agentid: Int,
        val mpnews: InnerMpNews,
) : IToAble, ISaveAble, IEnableIdTransAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var safe: Int? = null
    override var enable_id_trans: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：mpnews
     */
    val msgtype: String = "mpnews"
}

/**
 * Markdown消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#markdown%E6%B6%88%E6%81%AF
 *
 * 支持的语法: https://work.weixin.qq.com/api/doc/90000/90135/90236#%E6%94%AF%E6%8C%81%E7%9A%84markdown%E8%AF%AD%E6%B3%95
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param markdown 图文消息的内容，支持html标签，不超过666 K个字节（支持id转译）
 */
data class Markdown(
        val agentid: Int,
        val markdown: Content,
) : IToAble, IDuplicateCheckAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：markdown
     */
    val msgtype: String = "markdown"
}

/**
 * TaskCard按钮
 * @param key 按钮key值，用户点击后，会产生任务卡片回调事件，回调事件会带上该key值，只能由数字、字母和“_-@”组成，最长支持128字节
 * @param name 按钮名称
 * @since WeWork 2.8.2
 */
data class Btn(
        val key: String,
        val name: String,
) {
    /**
     * 点击按钮后显示的名称，默认为“已处理”
     */
    var replace_name: String? = "已处理"

    /**
     * 按钮字体颜色，可选“red”或者“blue”,默认为“blue”
     */
    var color: String? = "blue"

    /**
     * 按钮字体是否加粗，默认false
     */
    var is_bold: Boolean? = false
}

/**
 * TaskCard内容体
 * @param title 标题，不超过128个字节，超过会自动截断（支持id转译）
 * @param description 描述，不超过512个字节，超过会自动截断（支持id转译）
 * @param task_id 任务id，同一个应用发送的任务卡片消息的任务id不能重复，只能由数字、字母和“_-@”组成，最长支持128字节
 * @param btn 按钮[Btn]列表,按钮个数为1~2个
 * @since WeWork 2.8.2
 */
data class InnerTaskCard(
        val title: String,
        val description: String,
        val task_id: String,
        val btn: Array<Btn>
) {
    /**
     * 点击后跳转的链接。最长2048字节，请确保包含了协议头(http/https)
     */
    var url: String? = null
}

/**
 * 任务卡片消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90372#%E4%BB%BB%E5%8A%A1%E5%8D%A1%E7%89%87%E6%B6%88%E6%81%AF
 * @param agentid 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
 * @param taskcard TaskCard
 * @since WeWork 2.8.2
 */
data class TaskCard(
        val agentid: Int,
        val taskcard: InnerTaskCard
) : IToAble, IDuplicateCheckAble, IEnableIdTransAble {
    override lateinit var touser: String
    override lateinit var toparty: String
    override lateinit var totag: String
    override var enable_id_trans: Int? = null
    override var enable_duplicate_check: Int? = null
    override var duplicate_check_interval: Int? = null

    /**
     * 消息类型，此时固定为：taskcard
     */
    val msgtype: String = "taskcard"
}