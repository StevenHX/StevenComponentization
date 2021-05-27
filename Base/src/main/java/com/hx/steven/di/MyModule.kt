package com.hx.steven.di

import com.hx.steven.manager.OkGoManager
import org.koin.dsl.module

/**
 * @author Alwyn
 * @Date 2020/8/5
 * @Description 注入的module
 */
val appModule = module {
    single {
        OkGoManager()
    }
}
val factoryModule = module {

}
val allModule = appModule + factoryModule


