package app.kotlin.calculator.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.RoundingMode

data class AppUiState(
    val expression: String = "0",
    val result: String = "0",
    val isCompleted: Boolean = false
)

class AppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    private var lenOfEx = _uiState.value.expression.length
    private var theLastChar = _uiState.value.expression.last()
    private var lenOfPrecision = if (_uiState.value.result.indexOf('.') == -1) {
        0
    } else {
        _uiState.value.result.length - _uiState.value.result.indexOf('.') - 1
    }

    private fun removeLastChar() {
        if (!_uiState.value.isCompleted) {
            if (lenOfEx > 1) {
                _uiState.update { currentState
                    ->
                    currentState.copy(
                        expression = _uiState.value.expression.substring(0, lenOfEx - 1)
                    )
                }
                lenOfEx--
                theLastChar = _uiState.value.expression.last()
                updateResult()
            } else if (lenOfEx == 1 && _uiState.value.expression.last() in ('1'..'9')) {
                _uiState.update { currentState ->
                    currentState.copy(expression = "0")
                }
                theLastChar = '0'
                updateResult()
            }
        }
    }

    private fun appendExpressionAction(theChar: Char) {
        if (!_uiState.value.isCompleted) {
            if (theChar in ('0'..'9') && _uiState.value.expression == "0") {
                _uiState.update { currentState ->
                    currentState.copy(expression = theChar.toString())
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(expression = _uiState.value.expression + theChar)
                }
                lenOfEx++
            }
            theLastChar = theChar
            updateResult()
        }
    }

    private fun Double.roundResult(lenOfPrecision: Int): Double {
        return this
            .toBigDecimal()
            .setScale(lenOfPrecision - 1, RoundingMode.HALF_UP)
            .toDouble()
    }

    private fun splitTheExpression(expression: String): MutableList<String> {
        var result = ""
        for (i in expression.indices) {
            val c = expression[i]
            if (c in "+-*/%") {
                result += " $c "
            } else {
                result += c
            }
        }
        return result.split(" ").toMutableList()
    }

    //Function to remove all %
    private fun MutableList<String>.formatList() {
        var i = 0
        while (i <= this.size - 1) {
            if (this[i] == "%") {
                this[i - 1] = (this[i - 1].toDouble() / 100).toString()
                this.removeAt(i)
                i--
            }
            i++
            this.removeAll(listOf(""))
        }
        if (this.last() in "+-*/") {
            this.removeLast()
            this.removeAll(listOf(""))
        }
    }

    private fun updateResult() {
        _uiState.update { currentState ->
            currentState.copy(result = calculateExpression())
        }
        lenOfPrecision = if (_uiState.value.result.indexOf('.') == -1) {
            0
        } else {
            _uiState.value.result.length - _uiState.value.result.indexOf('.') - 1
        }
    }

    private fun calculateExpression(): String {
        val inputList = splitTheExpression(_uiState.value.expression)
        inputList.formatList()
        val numberList = mutableListOf<String>()
        val operatorList = mutableListOf<String>()
        val priorityOperator: Map<String, Int> = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)
        val operators = "+-*/"
        for (i in inputList) {
            if (i in operators) {
                if (operatorList.isEmpty())
                    operatorList.add(i)
                else {
                    if (priorityOperator.getValue(i) > priorityOperator.getValue(operatorList.last()))
                        operatorList.add(i)
                    else {
                        val operator = operatorList.removeLast()
                        val operand2 = numberList.removeLast()
                        val operand1 = numberList.removeLast()
                        val numberAdded = calculate(operand1, operand2, operator)
                        if (numberAdded == "Divided by zero") {
                            return "Divided by zero"
                        } else {
                            numberList.add(numberAdded)
                        }
                        operatorList.add(i)
                    }
                }
            } else {
                numberList.add(i)
            }
        }
        while (operatorList.isNotEmpty()) {
            val operator = operatorList.removeFirst()
            val operand1 = numberList.removeFirst()
            val operand2 = numberList.removeFirst()
            val numberAdded = calculate(operand1, operand2, operator)
            if (numberAdded == "Divided by zero") {
                return "Divided by zero"
            } else {
                numberList.add(0, numberAdded)
            }
        }
        return numberList.removeLast().removeSuffix(".0")
    }

    private fun calculate(operand1: String, operand2: String, operator: String): String {
        return when (operator) {
            "+" -> (operand1.toDouble() + operand2.toDouble()).toString()
            "-" -> (operand1.toDouble() - operand2.toDouble()).toString()
            "*" -> (operand1.toDouble() * operand2.toDouble()).toString()
            "/" -> {
                if (operand2.toDouble() == 0.0)
                    "Divided by zero"
                else
                    (operand1.toDouble() / operand2.toDouble()).toString()
            }

            else -> ""
        }
    }


    private val acAction: () -> Unit = {
        _uiState.update { currentState ->
            currentState.copy(
                expression = "0",
                result = "0",
                isCompleted = false
            )
        }
        lenOfEx = 1
        theLastChar = _uiState.value.expression.last()
        lenOfPrecision = 0
    }

    private val delAction: () -> Unit = { removeLastChar() }
    private val appendPercentage: () -> Unit = {
        if (theLastChar !in ".+-*/") {
            appendExpressionAction('%')
        }
    }
    private val appendDivide: () -> Unit = {
        if (theLastChar in "+-*") {
            removeLastChar()
            appendExpressionAction('/')
        } else if (theLastChar !in "./") {
            appendExpressionAction('/')
        }
    }
    private val append7: () -> Unit = {
        appendExpressionAction('7')
    }
    private val append8: () -> Unit = {
        appendExpressionAction('8')
    }
    private val append9: () -> Unit = {
        appendExpressionAction('9')
    }
    private val appendMulti: () -> Unit = {
        if (theLastChar in "+-/") {
            removeLastChar()
            appendExpressionAction('*')
        } else if (theLastChar !in ".*") {
            appendExpressionAction('*')
        }
    }
    private val append4: () -> Unit = {
        appendExpressionAction('4')
    }
    private val append5: () -> Unit = {
        appendExpressionAction('5')
    }
    private val append6: () -> Unit = {
        appendExpressionAction('6')
    }
    private val appendMinus: () -> Unit = {
        if (theLastChar in "+*/") {
            removeLastChar()
            appendExpressionAction('-')
        } else if (theLastChar !in ".-") {
            appendExpressionAction('-')
        }
    }
    private val append1: () -> Unit = {
        appendExpressionAction('1')
    }
    private val append2: () -> Unit = {
        appendExpressionAction('2')
    }
    private val append3: () -> Unit = {
        appendExpressionAction('3')
    }
    private val appendPlus: () -> Unit = {
        if (theLastChar in "-*/") {
            removeLastChar()
            appendExpressionAction('+')
        } else if (theLastChar !in ".+") {
            appendExpressionAction('+')
        }
    }
    private val roundResultAction: () -> Unit = {
        if (lenOfPrecision > 0) {
            _uiState.update { currentState ->
                currentState.copy(
                    result = _uiState.value.result
                        .toDouble()
                        .roundResult(
                            lenOfPrecision = lenOfPrecision
                        )
                        .toString(),
                )
            }
            lenOfPrecision--
        }
        if (_uiState.value.result.endsWith(".0")) {
            _uiState.update { currentState ->
                currentState.copy(result = _uiState.value.result.removeSuffix(".0"))
            }
        }
    }
    private val append0: () -> Unit = {
        appendExpressionAction('0')
    }
    private val appendDecimal: () -> Unit = {
        if (theLastChar !in "+-*/%") {
            if (_uiState.value.result.indexOfLast { it in "+-*/" } >= _uiState.value.result.indexOfLast { it == '.' })
                appendExpressionAction('.')
        }
    }
    private val showResultAction: () -> Unit = {
        _uiState.update { currentState
            ->
            currentState.copy(isCompleted = true)
        }
    }

    val listOfAction = listOf(
        acAction,
        delAction,
        appendPercentage,
        appendDivide,
        append7,
        append8,
        append9,
        appendMulti,
        append4,
        append5,
        append6,
        appendMinus,
        append1,
        append2,
        append3,
        appendPlus,
        roundResultAction,
        append0,
        appendDecimal,
        showResultAction
    )
}