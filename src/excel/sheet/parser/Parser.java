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
package excel.sheet.parser;

import excel.sheet.parser.expressions.*;
import excel.sheet.parser.expressions.function.*;
import excel.sheet.token.Token;
import excel.sheet.token.TokenType;
import excel.sheet.token.tokens.*;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Lista tokenów -> Lista wyrażeń
 *
 * @author eplightning <eplightning at outlook dot com>
 */
public class Parser {

    protected ListIterator<Token> iterator;

    public ArrayList<Expression> parse(ArrayList<Token> tokens) throws ParserException
    {
        ArrayList<Expression> expressions = new ArrayList<>();

        iterator = tokens.listIterator();

        while (iterator.hasNext()) {
            Token tok = iterator.next();

            if (tok.type() == TokenType.CALCULATOR_PORTION) {
                CalcToken ctok = (CalcToken) tok;

                expressions.add(new CalcExpression(ctok.getExpr()));
            } else if (tok.type() == TokenType.FUNCTION_START) {
                FunctionToken ftok = (FunctionToken) tok;

                if (ftok.getName().equals("$")) {
                    expressions.add(readAddress(ftok));
                } else {
                    expressions.add(readFunction(ftok));
                }
            } else {
                throw new ParserException("Invalid token outside of function");
            }
        }

        iterator = null;

        return expressions;
    }

    protected FunctionExpression createFunction(AddressExpression addr, String name) throws ParserException
    {
        if (name.equals("sum") || name.equals("suma"))
            return new SumFunction(addr);

        if (name.equals("avg") || name.equals("average"))
            return new AvgFunction(addr);

        if (name.equals("variance") || name.equals("var"))
            return new VarFunction(addr);

        if (name.equals("std_dev") || name.equals("stddev"))
            return new StdDevFunction(addr);

        throw new ParserException(String.format("Unknown function %s", name));
    }

    protected AddressExpression readAddress(FunctionToken start) throws ParserException
    {
        Expression col = readAddressParam(false);
        Expression row = readAddressParam(true);

        // poprzedni elem. jako że readAddressParam odczytuje też koniec funkcji, a nam potrzebny ten token!
        // też mamy gwarancje że to FunctionEndToken bo by readAddressParam wywalił wyjątek
        FunctionEndToken tok = (FunctionEndToken) iterator.previous();
        iterator.next(); // fajnie by było gdyby java po prostu miała ludzkei current()

        return new AddressExpression(col, row, start.isClosed(), tok.isClosed());
    }

    protected Expression readAddressParam(boolean row) throws ParserException
    {
        // first part of the param
        if (!iterator.hasNext())
            throw new ParserException("Unexpected EOL while reading address param #1");
        Token tok = iterator.next();

        Expression left = readAddressParamPart(tok);

        // optionally second part of the param, or end
        if (!iterator.hasNext())
            throw new ParserException("Unexpected EOL while reading address param #1");
        tok = iterator.next();

        if ((tok.type() == TokenType.FUNCTION_END && row) || (tok.type() == TokenType.COMMA && !row)) {
            return left;
        } else if (tok.type() != TokenType.RANGE) {
            throw new ParserException("Unexpected token while reading address param #2, expected range token");
        }

        // second part of the param
        if (!iterator.hasNext())
            throw new ParserException("Unexpected EOL while reading address param #2");
        tok = iterator.next();

        Expression right = readAddressParamPart(tok);

        // let's check if it's the end again
        if (!iterator.hasNext())
            throw new ParserException("Unexpected EOL while reading address param #2");
        tok = iterator.next();

        if ((tok.type() == TokenType.FUNCTION_END && row) || (tok.type() == TokenType.COMMA && !row)) {
            return new RangeExpression(left, right);
        }

        throw new ParserException("Unexpected token while reading address param #2, expected comma or func end");
    }

    protected Expression readAddressParamPart(Token tok) throws ParserException
    {
        if (tok.type() == TokenType.FUNCTION_START) {
            FunctionToken tok2 = (FunctionToken) tok;

            if (!tok2.getName().equals("$"))
                throw new ParserException("Unexpected function while reading address param, expected address");

            return readAddress(tok2);
        } else if (tok.type() == TokenType.INTEGER) {
            return new NumExpression((IntegerToken) tok);
        }

        throw new ParserException("Unexpected token while reading address param, expected address or integer");
    }

    protected FunctionExpression readFunction(FunctionToken name) throws ParserException
    {
        if (!iterator.hasNext())
            throw new ParserException("Unexpected EOL while reading function, expected address");

        Token tok = iterator.next();

        if (tok.type() != TokenType.FUNCTION_START)
            throw new ParserException("Unexpected token while reading function, expected address");

        FunctionToken tok2 = (FunctionToken) tok;

        if (!tok2.getName().equals("$"))
            throw new ParserException("Unexpected function while reading function, expected address");

        AddressExpression addr = readAddress(tok2);

        if (!iterator.hasNext())
            throw new ParserException("Unexpected EOL while reading function, expected function end");

        tok = iterator.next();

        if (tok.type() != TokenType.FUNCTION_END)
            throw new ParserException("Unexpected token while reading function, expected function end");

        return createFunction(addr, name.getName());
    }
}
