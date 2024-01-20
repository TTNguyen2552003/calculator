package app.kotlin.calculator.ui.viewmodel

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

    //The length of expression
    private var lenOfEx = _uiState.value.expression.length

    //Used to prevent from inputting invalidly (like: //,**,++,--)
    private var theLastChar = _uiState.value.expression.last()

    //Find the length of the precision part
    private var lenOfPrecision = if (_uiState.value.result.indexOf(char = '.') == -1) {
        0
    } else {
        _uiState.value.result.length - _uiState.value.result.indexOf('.') - 1
    }

    //Used to prevent from inputting invalidly (like: //,**,++,--)
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
        if (theChar in ('0'..'9') && _uiState.value.expression == "0") {
            _uiState.update { currentState ->
                currentState.copy(
                    expression = theChar.toString(),
                    isCompleted = false
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    expression = _uiState.value.expression + theChar,
                    isCompleted = false
                )
            }
            lenOfEx++
        }
        theLastChar = theChar
        updateResult()
    }

    private fun Double.roundResult(lenOfPrecision: Int): Double {
        return this
            .toBigDecimal()
            .setScale(lenOfPrecision - 1, RoundingMode.HALF_UP)
            .toDouble()
    }

    //Split the expression to used Polish Notation to calculate
    private fun splitTheExpression(expression: String): MutableList<String> {
        var result = ""
        for (i: Int in expression.indices) {
            val c: Char = expression[i]
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
        lenOfPrecision = if (_uiState.value.result.indexOf(char = '.') == -1) {
            0
        } else {
            _uiState.value.result.length - _uiState.value.result.indexOf(char = '.') - 1
        }
    }

    private fun calculateExpression(): String {
        val inputList: MutableList<String> = splitTheExpression(_uiState.value.expression)
        inputList.formatList()
        val numberList: MutableList<String> = mutableListOf()
        val operatorList: MutableList<String> = mutableListOf()
        val priorityOperator: Map<String, Int> = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)
        val operators = "+-*/"
        for (i: String in inputList) {
            if (i in operators) {
                if (operatorList.isEmpty())
                    operatorList.add(i)
                else {
                    if (priorityOperator.getValue(i) > priorityOperator.getValue(operatorList.last()))
                        operatorList.add(i)
                    else {
                        val operator: String = operatorList.removeLast()
                        val operand2: String = numberList.removeLast()
                        val operand1: String = numberList.removeLast()
                        val numberAdded: String = calculate(operand1, operand2, operator)
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
            val operator: String = operatorList.removeFirst()
            val operand1: String = numberList.removeFirst()
            val operand2: String = numberList.removeFirst()
            val numberAdded: String = calculate(operand1, operand2, operator)
            if (numberAdded == "Divided by zero") {
                return "Divided by zero"
            } else {
                numberList.add(index = 0, numberAdded)
            }
        }
        return numberList.removeLast().removeSuffix(suffix = ".0")
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
            appendExpressionAction(theChar = '%')
        }
    }
    private val appendDivide: () -> Unit = {
        if (theLastChar in "+-*") {
            removeLastChar()
            appendExpressionAction(theChar = '/')
        } else if (theLastChar !in "./") {
            appendExpressionAction(theChar = '/')
        }
    }
    private val append7: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '7')
    }
    private val append8: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '8')
    }
    private val append9: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '9')
    }
    private val appendMulti: () -> Unit = {
        if (theLastChar in "+-/") {
            removeLastChar()
            appendExpressionAction(theChar = '*')
        } else if (theLastChar !in ".*") {
            appendExpressionAction(theChar = '*')
        }
    }
    private val append4: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '4')
    }
    private val append5: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '5')
    }
    private val append6: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '6')
    }
    private val appendMinus: () -> Unit = {
        if (theLastChar in "+*/") {
            removeLastChar()
            appendExpressionAction(theChar = '-')
        } else if (theLastChar !in ".-") {
            appendExpressionAction(theChar = '-')
        }
    }
    private val append1: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '1')
    }
    private val append2: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '2')
    }
    private val append3: () -> Unit = {
        if (theLastChar != '%')
            appendExpressionAction(theChar = '3')
    }
    private val appendPlus: () -> Unit = {
        if (theLastChar in "-*/") {
            removeLastChar()
            appendExpressionAction(theChar = '+')
        } else if (theLastChar !in ".+") {
            appendExpressionAction(theChar = '+')
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
        if (_uiState.value.result.endsWith(suffix = ".0")) {
            _uiState.update { currentState ->
                currentState.copy(result = _uiState.value.result.removeSuffix(suffix = ".0"))
            }
        }
    }
    private val append0: () -> Unit = {
        appendExpressionAction(theChar = '0')
    }
    private val appendDecimal: () -> Unit = {
        if (theLastChar !in "+-*/%") {
            if (_uiState.value.expression.indexOfLast { it in "/+-*" } >= _uiState.value.expression.indexOfLast { it == '.' })
                appendExpressionAction(theChar = '.')
        }
    }
    private val showResultAction: () -> Unit = {
        _uiState.update { currentState ->
            currentState.copy(isCompleted = !_uiState.value.isCompleted)
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