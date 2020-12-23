package by.bsuir.converter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertTest {

    companion object {
        private const val LAUNCH_TIMEOUT = 5000L
        private const val PACKAGE_NAME = "by.bsuir.converter"
    }

    private lateinit var mDevice: UiDevice

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Start from the home screen
        mDevice.pressHome()

        // Wait for launcher
        val launcherPackage = getLauncherPackageName()
        Assert.assertThat(launcherPackage, CoreMatchers.notNullValue())
        mDevice.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        // Launch the app
        val context = ApplicationProvider.getApplicationContext<Context>()

        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT)
    }

    @Test
    fun checkPreconditions() {
        Assert.assertThat(mDevice, CoreMatchers.notNullValue())
    }

    @Test
    // Test conversion from 24m to 240dm
    fun checkConversion() {
        // Open length converter
        val lengthButton = mDevice.findObject(By.res(PACKAGE_NAME, "categories_recyclerView"))
            .children[1].findObject(By.res(PACKAGE_NAME, "category_button"))
        lengthButton.click()

        // Input "24"
        mDevice.findObject(By.res(PACKAGE_NAME, "button_2")).click()
        mDevice.findObject(By.res(PACKAGE_NAME, "button_4")).click()

        // Select cm as target measurement
        mDevice.findObject(By.res(PACKAGE_NAME, "converted_unit_spinner")).click()
        val cmButton = mDevice.findObject(UiSelector().className("android.widget.CheckedTextView").text("cm"))
        cmButton.click()

        // Check results
        val originalText = mDevice.findObject(By.res(PACKAGE_NAME, "original_value_editText"))
        val convertedText = mDevice.findObject(By.res(PACKAGE_NAME, "converted_value_textView"))

        Assert.assertThat(originalText.text, `is`(equalTo("24")))
        Assert.assertThat(convertedText.text, `is`(equalTo("2400")))
    }

    private fun getLauncherPackageName(): String? {
        // Create launcher Intent
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        // Use PackageManager to get the launcher package name
        val pm = ApplicationProvider.getApplicationContext<Context>().packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName
    }
}