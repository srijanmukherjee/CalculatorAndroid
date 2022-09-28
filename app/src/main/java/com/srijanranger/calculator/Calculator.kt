package com.srijanranger.calculator

class Calculator {
    companion object {
        private fun precedenceOf(op: Char): Int {
            return when (op) {
                '+', '-' -> 1
                '*', '/' -> 2
                else -> 0
            }
        }
        private fun infixToPostfix(infix: String): MutableList<String> {
            val postfix = mutableListOf<String>()
            val normalized = normalizeInfix(infix)

            val opStack = ArrayDeque<Char>()
            var canBeNegative = true
            var isReadingDigit = false
            var curr = ""

            for (c in normalized) {
                if (!isReadingDigit && c == ' ') continue

                if (canBeNegative && !isReadingDigit && c == '-') {
                    curr += '-'
                    canBeNegative = false
                    continue
                }

                if (c.isDigit() || c == '.') {
                    isReadingDigit = true
                    curr += c
                    continue
                }

                if (isReadingDigit && c == ' ') {
                    postfix.add(curr)
                    curr = ""
                    continue
                }

                isReadingDigit = false
                canBeNegative = false

                when (c) {
                    '(' -> {
                        opStack.addLast('(')
                        canBeNegative = true
                    }
                    ')' -> {

                        if (curr.isNotEmpty()) postfix.add(curr)
                        curr = ""

                        while (opStack.last() != '(') {
                            postfix.add(opStack.removeLast().toString())
                        }

                        opStack.removeLast()
                    }
                    else -> {
                        while (opStack.isNotEmpty() && precedenceOf(opStack.last()) >= precedenceOf(c)) {
                            postfix.add(opStack.removeLast().toString())
                        }

                        opStack.addLast(c)
                    }
                }
            }

            if (curr.isNotEmpty()) postfix.add(curr)

            while (opStack.isNotEmpty()) {
                postfix.add(opStack.removeLast().toString())
            }

            return postfix
        }

        private fun isOperator(c: Char): Boolean {
            return c == '+' || c == '-' || c == '*' || c == '/'
        }

        private fun normalizeInfix(infix: String): String {
            var normalizedInfix = ""

            // append * before ( is there's not operator before it
            for (i in infix.indices) {
                if (isOperator(infix[i])) normalizedInfix += " "
                normalizedInfix += "${infix[i]}"
                if (isOperator(infix[i])) normalizedInfix += " "
                if ((infix[i].isDigit() || infix[i] == ')') && i + 1 < infix.length && infix[i + 1] == '(')
                    normalizedInfix += " * "

            }

            return normalizedInfix
        }

        private fun evaluatePostfix(postfix: List<String>): Double {
            val stack = ArrayDeque<Double>()

            for (value in postfix) {
                if (value.length == 1 && isOperator(value[0])) {
                    val op = value[0]
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    var result = 0.0

                    when(op) {
                        '+' -> result = a + b
                        '-' -> result = a - b
                        '*' -> result = a * b
                        '/' -> result = a / b
                    }

                    stack.addLast(result)
                } else {
                    stack.addLast(value.toDouble())
                }
            }

            return stack.last()
        }

        fun compute(infix: String): Double {
            if (infix.isEmpty()) return 0.0
            val postfix = infixToPostfix(infix)
            return evaluatePostfix(postfix)
        }
    }
}