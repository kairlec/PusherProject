package com.kairlec.pusher.config

import com.kairlec.pusher.annotation.condition.PluginsCondition
import com.kairlec.pusher.config.properties.PluginsProperties
import com.kairlec.pusher.openapi.API
import com.kairlec.pusher.openapi.Event
import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.openapi.pojo.PusherUserPushConfig
import com.kairlec.pusher.pojo.Plugin
import com.kairlec.pusher.receiver.reply.ReplyService
import com.kairlec.pusher.service.UserService
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import java.io.File
import java.net.URLClassLoader
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.annotation.PostConstruct

@Configuration
@Conditional(PluginsCondition::class)
class APIImpl : API {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var replyService: ReplyService

    override fun getAdminUser(): List<PusherUser> {
        return userService.getAllAdmin()
    }

    override fun getUser(openUserId: String): PusherUser? {
        return userService.match(openid = openUserId).let { if (it.isEmpty()) null else it[0] }
    }

    override fun getUserWithConfig(openUserId: String): PusherUser? {
        return userService.getUserWithConfigByOpenid(openUserId)
    }

    override fun getUserByUserid(userid: String): List<PusherUser> {
        return userService.match(userid = userid)
    }

    override fun getUserWithConfigByUserid(userid: String): List<PusherUser> {
        return userService.matchWithConfig(userid)
    }

    override fun saveWithoutPushConfig(user: PusherUser): PusherUser {
        return userService.addOrUpdateWithoutPushConfig(user)
    }

    override fun save(user: PusherUser): PusherUser {
        return userService.addOrUpdate(user)
    }

    override fun savePushConfig(userPushConfig: PusherUserPushConfig): PusherUserPushConfig {
        return userService.addOrUpdatePushConfig(userPushConfig)
    }

    override fun getReplyService(): ReplyService {
        return replyService
    }

}

@Configuration
@EnableConfigurationProperties(PluginsProperties::class)
@Conditional(PluginsCondition::class)
class PluginsAutoConfiguration(private val pluginsProperties: PluginsProperties) {
    private val logger = LoggerFactory.getLogger(PluginsAutoConfiguration::class.java)

    @Autowired
    private lateinit var apiImpl: APIImpl

    @PostConstruct
    fun loadAll() {
        loadInnerPlugin()
        File(pluginsProperties.pluginsDir).apply {
            parentFile?.let {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }
        }.listFiles { _, name ->
            name.endsWith(".jar")
        }?.forEach(::loadJar)

    }

    private fun loadInnerPlugin() {
        try {
            val builder = ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage("com.kairlec.pusher.plugin.inner"))
                    .setScanners(SubTypesScanner(), TypeAnnotationsScanner())
                    .filterInputsBy(FilterBuilder().includePackage("com.kairlec.pusher.plugin.inner"))
            val reflections = Reflections(builder)
            val classes = reflections.getSubTypesOf(Event::class.java)
            classes.forEach { clazz ->
                try {
                    if (clazz.isAnnotationPresent(com.kairlec.pusher.openapi.Plugin::class.java)) {
                        val plugin = Plugin(clazz.name, clazz.getConstructor().newInstance())
                        plugin.event.onCreated(apiImpl)
                        Plugin.Invoker.plugins.add(plugin)
                        logger.info("Load inner plugin ${clazz.name} success")
                    }
                } catch (e: Throwable) {
                    logger.error("Load inner plugin class ${clazz.name} failed", e)
                }
            }
        } catch (e: Throwable) {
            logger.error("Load inner plugin failed", e)
        }
    }

    /**
     * @param pathToJar 传入[File]表示的jar文件
     */
    private fun loadJar(pathToJar: File) {
        try {
            logger.info("Loading plugin file:${pathToJar.nameWithoutExtension} ...")
            val filterPackageDirName = pluginsProperties.pluginPackageName.replace(".", "/")
            val jarFile = JarFile(pathToJar)
            val e: Enumeration<JarEntry> = jarFile.entries()
            val fileUrl = pathToJar.toURI().toURL()
            val child = URLClassLoader(arrayOf(fileUrl), PluginsAutoConfiguration::class.java.classLoader)
            while (e.hasMoreElements()) {
                val je: JarEntry = e.nextElement()
                val name = je.name.removePrefix("/")
                if (je.isDirectory || !name.endsWith(".class") || !name.startsWith(filterPackageDirName)) {
                    continue
                }
                // -6 because of .class
                var className: String = name.substring(0, name.length - 6)
                className = className.replace('/', '.')
                val clazz = Class.forName(className, true, child)
                if (Event::class.java.isAssignableFrom(clazz)) {
                    if (clazz.isAnnotationPresent(com.kairlec.pusher.openapi.Plugin::class.java)) {
                        val plugin = Plugin(clazz.name, clazz.getConstructor().newInstance() as Event)
                        plugin.event.onCreated(apiImpl)
                        Plugin.Invoker.plugins.add(plugin)
                        logger.info("Load plugin ${clazz.name} success")
                    }
                }
                logger.info("load class:${className}")
            }
            logger.info("Load plugin file ${pathToJar.nameWithoutExtension} success")
        } catch (e: Throwable) {
            logger.error("Load plugin file ${pathToJar.nameWithoutExtension} failed:${e.message}", e)
        }
    }
}