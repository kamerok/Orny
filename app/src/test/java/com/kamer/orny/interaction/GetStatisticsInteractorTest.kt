package com.kamer.orny.interaction

import android.text.format.DateUtils
import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.interaction.model.Debt
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.interaction.model.UserStatistics
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


@RunWith(MockitoJUnitRunner::class)
class GetStatisticsInteractorTest {

    @Mock lateinit var pageRepo: PageRepo
    @Mock lateinit var expenseRepo: ExpenseRepo

    private lateinit var interactor: GetStatisticsInteractor

    @Before
    fun setUp() {
        initSettings()
        initAuthors()
        initExpenses()
        interactor = GetStatisticsInteractorImpl(pageRepo, expenseRepo)
    }

    @Test
    fun callBothRepositories() {
        val observer = TestObserver<Statistics>()

        interactor.getStatistics().subscribe(observer)

        verify(pageRepo).getPageSettings()
        verify(expenseRepo).getAllExpenses()

        observer.assertNoErrors()
        observer.assertValueCount(1)
    }

    @Test
    fun calculateTotalDays() {
        val days = 25
        initSettings(period = days)

        assertThat(getStatistics().daysTotal).isEqualTo(days)
    }

    @Test
    fun calculateCurrentDay() {
        testCurrentDay(period = 10, dayDifference = 5, resultDays = 6)
        testCurrentDay(period = 10, dayDifference = 0, resultDays = 1)
        testCurrentDay(period = 10, dayDifference = 11, resultDays = 10)
        testCurrentDay(period = 10, dayDifference = -1, resultDays = 0)
        testCurrentDay(period = 10, dayDifference = -2, resultDays = 0)
    }

    private fun testCurrentDay(period: Int, dayDifference: Int, resultDays: Int) {
        initSettings(startDate = Date(Date().time - DateUtils.DAY_IN_MILLIS * dayDifference), period = period)

        assertThat(getStatistics().currentDay)
                .isEqualTo(resultDays)
    }

    @Test
    fun calculateBudgetLimit() {
        val limit = 100.0
        initSettings(budget = limit)

        assertThat(getStatistics().budgetLimit).isEqualTo(limit)
    }

    @Test
    fun calculateSpendTotal() {
        initExpenses(listOf(expense(values = 0 to 100.0), expense(values = 1 to 200.0)))

        assertThat(getStatistics().spendTotal).isEqualTo(300.0)
    }

    @Test
    fun calculateSpendTotalTwoValuesInSingleExpense() {
        initExpenses(listOf(expense(values = *arrayOf(0 to 100.0, 1 to 100.0))))

        assertThat(getStatistics().spendTotal).isEqualTo(200.0)
    }

    @Test
    fun calculateSpendTotalWithOffBudget() {
        initExpenses(listOf(expense(values = 0 to 200.0), expense(isOffBudget = true, values = 1 to 200.0)))

        assertThat(getStatistics().spendTotal).isEqualTo(400.0)
    }

    @Test
    fun calculateBudgetSpendTotal() {
        initExpenses(listOf(expense(values = 0 to 100.0), expense(values = 1 to 200.0)))

        assertThat(getStatistics().budgetSpendTotal).isEqualTo(300.0)
    }

    @Test
    fun calculateBudgetSpendTotalTwoValuesInSingleExpense() {
        initExpenses(listOf(expense(values = *arrayOf(0 to 100.0, 1 to 100.0))))

        assertThat(getStatistics().budgetSpendTotal).isEqualTo(200.0)
    }

    @Test
    fun calculateBudgetSpendTotalWithOffBudget() {
        initExpenses(listOf(expense(values = 0 to 200.0), expense(isOffBudget = true, values = 1 to 200.0)))

        assertThat(getStatistics().budgetSpendTotal).isEqualTo(200.0)
    }

    @Test
    fun calculateOffBudgetSpendTotal() {
        initExpenses(listOf(expense(values = 0 to 200.0), expense(isOffBudget = true, values = 1 to 100.0)))

        assertThat(getStatistics().offBudgetSpendTotal).isEqualTo(100.0)
    }

    @Test
    fun budgetLeftShouldIgnoreOffBudget() {
        initSettings(budget = 1000.0)
        initExpenses(listOf(expense(isOffBudget = true, values = 0 to 200.0)))

        assertThat(getStatistics().budgetLeft).isEqualTo(1000.0)
    }

    @Test
    fun calculateBudgetLeft() {
        initSettings(budget = 1000.0)
        initExpenses(listOf(expense(values = 0 to 200.0)))

        assertThat(getStatistics().budgetLeft).isEqualTo(800.0)
    }

    @Test
    fun calculateBudgetLeftWhenMoreSpent() {
        initSettings(budget = 0.0)
        initExpenses(listOf(expense(values = 0 to 200.0)))

        assertThat(getStatistics().budgetLeft).isEqualTo(0.0)
    }

    @Test
    fun calculateBudgetDifference() {
        initSettings(budget = 1000.0, period = 4)

        assertThat(getStatistics().budgetDifference).isEqualTo(250.0)
    }

    @Test
    fun calculateBudgetDifferenceWithSpend() {
        initSettings(budget = 1000.0, period = 4)
        initExpenses(listOf(expense(values = 0 to 150.0)))

        assertThat(getStatistics().budgetDifference).isEqualTo(100.0)
    }

    @Test
    fun calculateBudgetDifferenceWithHugeSpend() {
        initSettings(budget = 1000.0, period = 4)
        initExpenses(listOf(expense(values = 0 to 750.0)))

        assertThat(getStatistics().budgetDifference).isEqualTo(-500.0)
    }

    @Test
    fun budgetDifferenceShouldIgnoreOffBudget() {
        initSettings(budget = 1000.0, period = 4)
        initExpenses(listOf(expense(isOffBudget = true, values = 0 to 750.0)))

        assertThat(getStatistics().budgetDifference).isEqualTo(250.0)
    }

    @Test
    fun calculateBudgetDifferenceSecondDay() {
        initSettings(budget = 1000.0, period = 4, startDate = yesterday())

        assertThat(getStatistics().budgetDifference).isEqualTo(500.0)
    }

    @Test
    fun calculateSpendTodayNoSpent() {
        initSettings(budget = 333.0, period = 3)

        assertThat(getStatistics().toSpendToday).isEqualTo(111.0)
    }

    @Test
    fun calculateSpendTodayNoTodaySpent() {
        initSettings(budget = 433.0, period = 3)
        initExpenses(listOf(expense(date = yesterday(), values = 0 to 100.0)))

        assertThat(getStatistics().toSpendToday).isEqualTo(111.0)
    }

    @Test
    fun calculateSpendTodayWithTodaySpent() {
        initSettings(budget = 333.0, period = 3)
        initExpenses(listOf(expense(values = 0 to 11.0)))

        assertThat(getStatistics().toSpendToday).isEqualTo(100.0)
    }

    @Test
    fun spendTodayCanNotBeNegative() {
        initSettings(budget = 333.0, period = 3)
        initExpenses(listOf(expense(values = 0 to 300.0)))

        assertThat(getStatistics().toSpendToday).isEqualTo(0.0)
    }

    @Test
    fun calculateAverageSpendPerDay() {
        initSettings(budget = 333.0, period = 3)
        initExpenses(listOf(expense(values = 0 to 200.0)))

        assertThat(getStatistics().averageSpendPerDay).isEqualTo(111.0)
    }

    @Test
    fun calculateAverageSpendPerDayAccordingBudgetLeft() {
        initSettings(budget = 533.0, period = 3)
        initExpenses(listOf(expense(date = yesterday(), values = 1 to 200.0)))

        assertThat(getStatistics().averageSpendPerDayAccordingBudgetLeft).isEqualTo(111.0)
    }

    @Test
    fun averageSpendPerDayAccordingBudgetLeftShouldIgnoreOffBudget() {
        initSettings(budget = 333.0, period = 3)
        initExpenses(listOf(expense(isOffBudget = true, date = yesterday(), values = 1 to 200.0)))

        assertThat(getStatistics().averageSpendPerDayAccordingBudgetLeft).isEqualTo(111.0)
    }

    @Test
    fun averageSpendPerDayAccordingBudgetLeftShouldIgnoreToday() {
        initSettings(budget = 533.0, period = 3)
        initExpenses(listOf(expense(date = yesterday(), values = 1 to 200.0), expense(values = 0 to 100.0)))

        assertThat(getStatistics().averageSpendPerDayAccordingBudgetLeft).isEqualTo(111.0)
    }

    @Test
    fun averageSpendPerDayAccordingBudgetLeftShouldBePositive() {
        initSettings(budget = 333.0, period = 3)
        initExpenses(listOf(expense(date = yesterday(), values = 1 to 500.0)))

        assertThat(getStatistics().averageSpendPerDayAccordingBudgetLeft).isEqualTo(0.0)
    }

    @Test
    fun averageSpendPerDayAccordingBudgetLeftShouldExpand() {
        initSettings(budget = 200.0, period = 3, startDate = yesterday())

        assertThat(getStatistics().averageSpendPerDayAccordingBudgetLeft).isEqualTo(100.0)
    }

    @Test
    fun calculateUsersStatistics() {
        initExpenses(listOf(
                expense(values = 0 to 200.0),
                expense(isOffBudget = true, values = 1 to 100.0),
                expense(isOffBudget = true, values = 0 to 150.0),
                expense(values = 1 to 50.0)
        ))

        val userStatistics = getStatistics().usersStatistics

        assertThat(userStatistics).containsOnly(
                UserStatistics("0", 350.0, 200.0, 150.0),
                UserStatistics("1", 150.0, 50.0, 100.0)
        )
    }

    @Test
    fun calculateUsersStatisticsNoSecondUserSpend() {
        initExpenses(listOf(
                expense(values = 0 to 200.0),
                expense(isOffBudget = true, values = 0 to 150.0)
        ))

        val userStatistics = getStatistics().usersStatistics

        assertThat(userStatistics).containsOnly(
                UserStatistics("0", 350.0, 200.0, 150.0),
                UserStatistics("1", 0.0, 0.0, 0.0)
        )
    }

    @Test
    fun calculateUsersStatisticsSingleUser() {
        initAuthors(1)
        initExpenses(listOf(
                expense(values = 0 to 100.0),
                expense(isOffBudget = true, values = 0 to 100.0)
        ))

        val userStatistics = getStatistics().usersStatistics

        assertThat(userStatistics).containsOnly(UserStatistics("0", 200.0, 100.0, 100.0))
    }

    @Test
    fun calculateDebts() {
        initExpenses(listOf(expense(values = 0 to 200.0), expense(isOffBudget = true, values = 1 to 100.0)))

        val debts = getStatistics().debts

        assertThat(debts).containsOnly(Debt("1", "0", 50.0))
    }

    @Test
    fun calculateDebtsSingleUserSpend() {
        initExpenses(listOf(expense(values = 0 to 200.0)))

        val debts = getStatistics().debts

        assertThat(debts).containsOnly(Debt("1", "0", 100.0))
    }

    private fun initSettings(budget: Double = 0.0, startDate: Date = Date(), period: Int = 0) {
        `when`(pageRepo.getPageSettings()).thenReturn(Observable.just(PageSettings(budget, startDate, period)))
    }

    private fun initExpenses(list: List<Expense> = emptyList()) {
        `when`(expenseRepo.getAllExpenses()).thenReturn(Observable.just(list))
    }

    private fun initAuthors(usersCont: Int = 2) {
        val list = (0..usersCont - 1).map { author(it) }
        `when`(pageRepo.getPageAuthors()).thenReturn(Observable.just(list))
    }

    private fun getStatistics(): Statistics {
        val observer = TestObserver<Statistics>()

        interactor.getStatistics().subscribe(observer)

        return observer.values().first()
    }

    private fun expense(date: Date = Date(), isOffBudget: Boolean = false, vararg values: Pair<Int, Double>): Expense = Expense(
            date = date,
            isOffBudget = isOffBudget,
            values = values.map { (first, second) -> Pair(author(first), second) }.toMap()
    )

    private fun author(id: Int) = Author("$id", id, "$id", "")

    private fun yesterday(): Date = Date().apply { time -= DateUtils.DAY_IN_MILLIS }
}