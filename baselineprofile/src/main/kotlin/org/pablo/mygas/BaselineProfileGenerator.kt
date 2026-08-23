package org.pablo.mygas

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startupAndHomeFlow() {
        baselineProfileRule.collect(packageName = "org.pablo.mygas") {
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(By.text("Encuentra mejor precio")), 5_000)
            device.wait(Until.hasObject(By.text("Selecciona una provincia")), 5_000)
        }
    }
}
