/*
 * The MIT License
 *
 * Copyright 2014 eplightning <eplightning at outlook dot com>
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
package excel.calc;

import excel.Logger;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementacja kalkulatora jako programu zewnętrznego
 *
 * @author eplightning <eplightning at outlook dot com>
 */
public class CppCalculator implements Calculator {

    /**
     * Znak nowej linii
     */
    private final String calculatorEol;

    /**
     * Blokada na proces
     */
    private final ReentrantLock calculatorLock;

    /**
     * Ścieżka do kalkulatora
     */
    private final String calculatorPath;

    /**
     * Proces kalkulatora
     */
    private Process calculatorProc;

    /**
     * Konsola logująca
     */
    private Logger logger;

    /**
     * Czy watchdog ma przestać przywracać proces
     */
    private boolean watchdogPleaseDie;

    /**
     * Wątek watchdoga
     */
    private Watchdog watchdogThread;

    public CppCalculator(Logger log) throws IOException
    {
        logger = log;
        watchdogPleaseDie = false;

        if (System.getProperty("os.name").startsWith("Windows")) {
            calculatorPath = "calc/Calc.exe -q";
            calculatorEol = "\r\n";
        } else {
            calculatorPath = "calc/Calc -q";
            calculatorEol = "\n";
        }

        calculatorLock = new ReentrantLock();

        // usuwa proces kalkulatora po zakończeniu programu
        Runtime.getRuntime().addShutdownHook(new ProcessCleaner());

        // zaczynamy proces i watchdoga
        startProcess();
        createProcessWatchdog();
    }

    /**
     * Liczenie wartości
     *
     * @param  input Formuła
     * @return Obliczona formuła
     * @throws ArithmeticException
     */
    @Override
    public String calculateExpression(String input) throws ArithmeticException
    {
        calculatorLock.lock();

        try {
            calculatorProc.getOutputStream().write(input.getBytes());
            calculatorProc.getOutputStream().write(calculatorEol.getBytes());
            calculatorProc.getOutputStream().flush();

            StringBuilder out = new StringBuilder();

            int b;

            while ((b = calculatorProc.getInputStream().read()) != -1) {
                if (b == '\n')
                    break;

                if (b == '\r')
                    continue;

                out.append((char) b);
            }

            if (b != '\n') {
                calculatorLock.unlock();
                throw new ArithmeticException("CalcIO: Unexpected end of stream");
            }

            if (out.length() == 0) {
                while ((b = calculatorProc.getErrorStream().read()) != -1) {
                    if (b == '\n')
                        break;

                    if (b == '\r')
                        continue;

                    out.append((char) b);
                }

                calculatorLock.unlock();

                if (b != '\n' || out.length() == 0) {
                    throw new ArithmeticException("CalcIO: Invalid error reported");
                }

                throw new ArithmeticException(out.toString());
            }

            calculatorLock.unlock();

            return out.toString();
        } catch (IOException e) {
            calculatorLock.unlock();
            throw new ArithmeticException("CalcIO: " + e.getMessage());
        }
    }

    /**
     * Tworzenie wątku watchdoga
     */
    private void createProcessWatchdog()
    {
        watchdogThread = new Watchdog();
        watchdogThread.start();
    }

    /**
     * Tworzenie procesu
     *
     * @throws IOException
     */
    private void startProcess() throws IOException
    {
        calculatorProc = Runtime.getRuntime().exec(calculatorPath);
    }

    /**
     * Watchdog
     */
    private class Watchdog extends Thread {
        @Override
        public void run()
        {
            try {
                while (!watchdogPleaseDie) {
                    calculatorProc.waitFor();

                    if (watchdogPleaseDie)
                        return;

                    logger.addLine("Calculator process died, waiting for lock and spawning it again ...");

                    calculatorLock.lock();

                    try {
                        startProcess();
                    } catch (IOException ex) {
                        System.exit(1);
                    } finally {
                        calculatorLock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                logger.addLine("Something interrupted calculator watchdog thread, creating new one ...");
                createProcessWatchdog();
            }
        }
    }

    /**
     * Usuwa proces kalkulatora
     */
    private class ProcessCleaner extends Thread {
        @Override
        public void run()
        {
            watchdogPleaseDie = true;

            if (calculatorProc != null)
                calculatorProc.destroy();
        }
    }

}
