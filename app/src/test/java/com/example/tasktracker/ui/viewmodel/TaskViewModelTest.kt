package com.example.tasktracker.ui.viewmodel

import com.example.tasktracker.data.model.Priority
import com.example.tasktracker.data.repository.FakeTaskRepository
import com.example.tasktracker.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeTaskRepository()
        viewModel = TaskViewModel(fakeRepository)
    }

    @Test
    fun initialState_isEmpty() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val state = viewModel.uiState.value
        assertTrue(state.tasks.isEmpty())
        assertTrue(state.categories.isEmpty())
        assertNull(state.selectedCategoryId)
        assertNull(state.selectedPriority)
        assertEquals("", state.searchQuery)
        assertEquals(0f, state.completionRate, 0.001f)
        assertEquals(0, state.activeTasksCount)
        assertEquals(0, state.completedTasksCount)

        collectJob.cancel()
    }

    @Test
    fun selectCategory_updatesUiState() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.selectCategory(5L)
        assertEquals(5L, viewModel.uiState.value.selectedCategoryId)

        viewModel.selectCategory(null)
        assertNull(viewModel.uiState.value.selectedCategoryId)

        collectJob.cancel()
    }

    @Test
    fun selectPriority_updatesUiState() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.selectPriority(Priority.HIGH)
        assertEquals(Priority.HIGH, viewModel.uiState.value.selectedPriority)

        viewModel.selectPriority(null)
        assertNull(viewModel.uiState.value.selectedPriority)

        collectJob.cancel()
    }

    @Test
    fun updateSearchQuery_updatesUiState() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.updateSearchQuery("Zen")
        assertEquals("Zen", viewModel.uiState.value.searchQuery)

        collectJob.cancel()
    }

    @Test
    fun insertTask_addsTaskAndUpdatesMetrics() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.insertTask(
            title = "Morning Meditation",
            description = "Spend 10 minutes breathing in the garden",
            categoryId = 1L,
            priority = Priority.MEDIUM,
            dueDate = null
        )

        val state = viewModel.uiState.value
        assertEquals(1, state.tasks.size)
        assertEquals("Morning Meditation", state.tasks.first().title)
        assertEquals(1, state.activeTasksCount)
        assertEquals(0, state.completedTasksCount)
        assertEquals(0f, state.completionRate, 0.001f)

        collectJob.cancel()
    }

    @Test
    fun completeTask_updatesCompletionMetrics() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.insertTask("Task 1", "Desc 1", 1L, Priority.LOW, null)
        viewModel.insertTask("Task 2", "Desc 2", 1L, Priority.HIGH, null)

        val task1 = viewModel.uiState.value.tasks.first { it.title == "Task 1" }
        viewModel.toggleTaskCompletion(task1)

        val state = viewModel.uiState.value
        assertEquals(2, state.tasks.size)
        assertEquals(1, state.activeTasksCount)
        assertEquals(1, state.completedTasksCount)
        assertEquals(0.5f, state.completionRate, 0.001f)

        val updatedTask1 = state.tasks.first { it.title == "Task 1" }
        viewModel.toggleTaskCompletion(updatedTask1)

        val stateAfterReToggle = viewModel.uiState.value
        assertEquals(2, stateAfterReToggle.activeTasksCount)
        assertEquals(0, stateAfterReToggle.completedTasksCount)
        assertEquals(0f, stateAfterReToggle.completionRate, 0.001f)

        collectJob.cancel()
    }

    @Test
    fun deleteTask_removesTaskAndUpdatesMetrics() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.insertTask("Task to delete", "Desc", null, Priority.LOW, null)
        val task = viewModel.uiState.value.tasks.first()

        viewModel.deleteTask(task)

        val state = viewModel.uiState.value
        assertTrue(state.tasks.isEmpty())
        assertEquals(0, state.activeTasksCount)

        collectJob.cancel()
    }

    @Test
    fun createCategory_addsCategoryToUiState() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.createCategory("Mindfulness", 0xFF00FF, "spa")

        val state = viewModel.uiState.value
        assertEquals(1, state.categories.size)
        assertEquals("Mindfulness", state.categories.first().name)

        collectJob.cancel()
    }

    @Test
    fun filtering_byCategoryPriorityAndQuery_worksCorrectly() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.createCategory("Category A", 1, "spa")
        viewModel.createCategory("Category B", 2, "spa")

        viewModel.insertTask("Water Flowers", "Garden maintenance", 1L, Priority.LOW, null)
        viewModel.insertTask("Study Zen", "Reading book", 2L, Priority.HIGH, null)
        viewModel.insertTask("Water Bonsai", "Watering bonsai tree", 1L, Priority.HIGH, null)

        assertEquals(3, viewModel.uiState.value.tasks.size)

        viewModel.selectCategory(1L)
        assertEquals(2, viewModel.uiState.value.tasks.size)
        assertTrue(viewModel.uiState.value.tasks.all { it.categoryId == 1L })

        viewModel.selectPriority(Priority.HIGH)
        assertEquals(1, viewModel.uiState.value.tasks.size)
        assertEquals("Water Bonsai", viewModel.uiState.value.tasks.first().title)

        viewModel.selectCategory(null)
        assertEquals(2, viewModel.uiState.value.tasks.size)
        assertTrue(viewModel.uiState.value.tasks.all { it.priority == Priority.HIGH })

        viewModel.updateSearchQuery("Zen")
        assertEquals(1, viewModel.uiState.value.tasks.size)
        assertEquals("Study Zen", viewModel.uiState.value.tasks.first().title)

        collectJob.cancel()
    }
}
