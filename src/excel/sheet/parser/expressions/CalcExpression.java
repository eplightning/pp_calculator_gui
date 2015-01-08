/*
 * The MIT License
 *
 * Copyright 2015 eplightning <eplightning at outlook dot com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package excel.sheet.parser.expressions;

import excel.sheet.CellAccessor;
import excel.sheet.Location;
import excel.sheet.parser.*;
import java.util.HashSet;

/**
 * Najprostsze wyrażenie
 * 
 * @author eplightning <eplightning at outlook dot com>
 */
public class CalcExpression implements Expression {

    protected String expression;
    
    public CalcExpression(String expr)
    {
        expression = expr;
    }

    @Override
    public String evaluate(CellAccessor cells, HashSet<Location> callStack)  throws ParserException
    {
        return expression;
    }

    @Override
    public int evaluateAsInt(CellAccessor cells, HashSet<Location> callStack) throws ParserException
    {
        throw new ParserException("Calculator expression cannot be evaluated as int");
    }

    @Override
    public void relativeMove(int col, int row)
    {
    }

    @Override
    public String evaluateAsFormula()
    {
        return expression;
    }
    
}
