package com.hx.steven.di

import org.koin.dsl.module

/**
 * @author Alwyn
 * @Date 2020/8/5
 * @Description 注入的module
 */
val appModule = module {
    single() {
        Girl()
    }
}
val factoryModule = module {

}
val allModule = appModule + factoryModule


