package ru.grishagin.gxbtask;

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import ru.grishagin.gxbtask.view.MainView
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.launch
import kotlin.reflect.KClass

@SpringBootApplication
open class GxbTaskApplication: App(MainView::class){

    private lateinit var context: ConfigurableApplicationContext

    override fun init() {
        context = SpringApplication.run(this.javaClass)
        context.autowireCapableBeanFactory.autowireBean(this)
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T = context.getBean(type.java)
            override fun <T : Any> getInstance(type: KClass<T>, name: String): T = context.getBean(name, type.java)
        }
    }
}

fun main(args: Array<String>) {
    launch<GxbTaskApplication>(args)
}
