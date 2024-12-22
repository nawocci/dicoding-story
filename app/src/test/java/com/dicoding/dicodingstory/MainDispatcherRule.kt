package com.dicoding.dicodingstory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    // Set the main dispatcher to the test dispatcher before each test
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    // Reset the main dispatcher to the original dispatcher after each test
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}