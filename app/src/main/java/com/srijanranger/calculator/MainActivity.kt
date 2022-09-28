package com.srijanranger.calculator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val availableOperators = mutableMapOf<String, String>()
    private var tvResult: TextView? = null
    private var tvExpression: TextView? = null
    private var tvPrevAnswer: TextView? = null
    private var isNumeric: Boolean = false
    private var isDot: Boolean = false
    private var result: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        availableOperators[resources.getString(R.string.divide)] = "divide"
        availableOperators[resources.getString(R.string.multiply)] = "multiply"
        availableOperators[resources.getString(R.string.add)] = "add"
        availableOperators[resources.getString(R.string.subtract)] = "subtract"
        availableOperators[resources.getString(R.string.brackets)] = "brackets"
        availableOperators[resources.getString(R.string.equals)] = "equals"

        tvResult = findViewById(R.id.tvResult)
        tvResult?.movementMethod = ScrollingMovementMethod()

        tvExpression = findViewById(R.id.tvSecondary)
        tvExpression?.movementMethod = ScrollingMovementMethod()

        tvPrevAnswer = findViewById(R.id.tvPrevAnswer)
        tvPrevAnswer?.movementMethod = ScrollingMovementMethod()

        tvResult?.visibility = View.GONE

        val numBtnList: List<Button> = listOf(
            findViewById(R.id.btnNum0),
            findViewById(R.id.btnNum1),
            findViewById(R.id.btnNum2),
            findViewById(R.id.btnNum3),
            findViewById(R.id.btnNum4),
            findViewById(R.id.btnNum5),
            findViewById(R.id.btnNum6),
            findViewById(R.id.btnNum7),
            findViewById(R.id.btnNum8),
            findViewById(R.id.btnNum9)
        )

        for (btn in numBtnList) {
            btn.setOnClickListener(::handleNum)
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener(::clear)
        findViewById<Button>(R.id.btnErase).setOnClickListener(::handleDelete)
        findViewById<Button>(R.id.btnDecimal).setOnClickListener(::handleDot)
        findViewById<Button>(R.id.btnOpEquals).setOnClickListener(::handleOp)
        findViewById<Button>(R.id.btnOpDivide).setOnClickListener(::handleOp)
        findViewById<Button>(R.id.btnOpMultiply).setOnClickListener(::handleOp)
        findViewById<Button>(R.id.btnOpPlus).setOnClickListener(::handleOp)
        findViewById<Button>(R.id.btnOpMinus).setOnClickListener(::handleOp)
        findViewById<Button>(R.id.btnOpBrackets).setOnClickListener(::handleOp)
    }

    private fun compute() {
        // evaluate the expression
        result = Calculator.compute(tvExpression?.text.toString()).toString()
        if (result.substringAfterLast(".") == "0") {
            result = result.substringBeforeLast(".")
        }
        tvResult?.visibility = View.VISIBLE
        tvResult?.text = getString(R.string.result_string, result)
    }

    // TODO: currently only supports single level parenthesis
    private fun handleBracket() {
        tvExpression?.let {
            var openBracketPos: Int = -1
            for (i in it.text.length - 1 downTo  0) {
                if (it.text[i] == ')') break
                if (it.text[i] == '(') {
                    openBracketPos = i
                    break
                }
            }

            // there's an open bracket, so close it
            if (openBracketPos != -1) {
                // remove bracket if expression inside bracket is empty
                if (openBracketPos == it.text.length - 1) it.text = it.text.substring(0, it.text.length - 1)

                // do not close the bracket if there's an operator at the end
                else if (!isOperator(it.text.last())) it.append(")")
                return
            }

            it.append("(")
        }
    }

    private fun isOperator(c: Char) : Boolean {
        return c == '+' || c == '-' || c == '/' || c == '*'
    }

    @SuppressLint("SetTextI18n")
    private fun addOperator(op: Char) {
        tvExpression?.let {
            val expr = it.text

            if (expr.isEmpty()) return

            if (expr.last().isDigit() || expr.last() == ')') {
                it.append(op.toString())
            }

            if (isOperator(expr.last())) {
                it.text = it.text.substring(0, it.text.length - 1) + op.toString()
            }
        }
    }

    private fun hideResult() {
        if (result.isEmpty()) return

        tvResult?.visibility = View.GONE
        tvPrevAnswer?.text = getString(R.string.result_string, result)
        clear()
        result = ""
    }

    private fun handleMinus() {
        tvExpression?.let {
            if (it.text.isEmpty() || it.text.last() == '(') it.append("-")
            else addOperator('-')
        }
    }

    private fun applyOp(op: String?) {
        if (!availableOperators.containsValue(op)) return

        hideResult()

        if (op == "brackets") {
            handleBracket()
            return
        }

        when (op) {
            "add" -> addOperator('+')
            "subtract" -> handleMinus()
            "divide" -> addOperator('/')
            "multiply" -> addOperator('*')
        }
    }

    private fun handleOp(view: View) {
        val btn = view as Button
        val op = btn.text.toString()

        isDot = false
        isNumeric = false

        if (availableOperators[op] == "equals") compute()
        else applyOp(availableOperators[op])

    }

    private fun handleNum(view: View) {
        hideResult()

        val btn = view as Button
        val digit = btn.text.toString()
        isNumeric = true
        tvExpression?.append(digit)
    }

    private fun handleDot(view: View) {
        if (isDot) return
        hideResult()
        if (!isNumeric) tvExpression?.append("0")
        tvExpression?.append(".")
        isNumeric = false
        isDot = true
    }

    private fun handleDelete(view: View) {
        tvExpression?.let {
            if (it.text.isEmpty()) return
            if (it.text.last() == '.') isDot = false
            it.text = it.text.substring(0, it.text.length - 1)
        }
    }

    private fun clear() {
        isNumeric = false
        isDot = false
        tvExpression?.let {
            if (it.text.isEmpty()) {
                tvResult?.text = ""
                tvPrevAnswer?.text = ""
            }

            it.text = ""
            tvResult?.visibility = View.GONE
        }
    }

    private fun clear(view: View) {
        this.clear()
    }
}