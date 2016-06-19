package org.yggd.spring

import org.springframework.beans.factory.BeanNameAware
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.yggd.kotlin.forSlf4j
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@SpringBootApplication
open class LazyBeanDefApplication: CommandLineRunner {

    @Bean()
    @Scope(scopeName = "prototype")
    open fun sampleBean() = SampleBean(createDate = Date()).apply {
        TimeUnit.SECONDS.sleep(1)
    }

    @Autowired
    lateinit var component: RegisterBeanDefinitionComponent

    override fun run(vararg args: String) {
        val createNum = 10
        component.registerCustomBeanDefinitions(createNum)
        component.verification(createNum)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(LazyBeanDefApplication::class.java, *args)
}

@Component
open class RegisterBeanDefinitionComponent @Autowired constructor(val ctx: ApplicationContext) {

    val logger = forSlf4j()

    fun registerCustomBeanDefinitions(num : Int) {
        if (ctx !is BeanDefinitionRegistry) {
            throw UnsupportedOperationException(
                    "ApplicationContext is not implement BeanDefinitionRegistry")
        }
        val def = BeanDefinitionBuilder
                .rootBeanDefinition(CustomBean::class.java)
                .addConstructorArgReference("sampleBean")
                .beanDefinition
        (1..num).forEach { i ->
            ctx.registerBeanDefinition("customBean_$i", def)
        }
    }

    fun verification(num: Int) {
        if (ctx !is BeanDefinitionRegistry) {
            throw UnsupportedOperationException(
                    "ApplicationContext is not implement BeanDefinitionRegistry")
        }
        (1..num).forEach { i ->
            val beanName = "customBean_$i"
            val customBean = ctx.getBean(beanName) as CustomBean
            val beanDef = ctx.getBeanDefinition(beanName)
            logger.info("customBean's name: $beanName customBean's hash: ${customBean.hashCode()}" +
                    " sampleBean's hash: ${customBean.sampleBean.hashCode()}" +
                    " create Date: ${customBean.sampleBean.createDate} isSingleton: ${beanDef.isSingleton}")
        }
    }
}

class SampleBean(val createDate: Date) : InitializingBean {

    val logger = forSlf4j()

    override fun afterPropertiesSet() {
        logger.info("afterPropertiesSet is called.")
    }

}

class CustomBean(val sampleBean: SampleBean) : BeanNameAware {

    override fun setBeanName(name: String?) {
        _beanName = name
    }

    private var _beanName : String? = null

    val logger = forSlf4j()

    @PostConstruct
    fun postConstruct() {
        logger.info("postConstruct() is called. beanName $_beanName")
    }

    @PreDestroy
    fun preDestroy() {
        logger.info("preDestroy() is called. $_beanName")
    }
}
