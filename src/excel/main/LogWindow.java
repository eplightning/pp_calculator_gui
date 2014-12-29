/*
 * The MIT License
 *
 * Copyright 2014 eplightning.
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
package excel.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author eplightning
 */
public class LogWindow extends JFrame {

    protected JTextArea area;

    public LogWindow()
    {
        setTitle("Logi aplikacji");
        setSize(600, 600);

        area = new JTextArea();

        area.setPreferredSize(new Dimension(-1, -1));
        area.setEditable(false);

        add(area);
    }

    public void addLine(final String text)
    {
        Runnable op = new Runnable() {
            @Override
            public void run()
            {
                DateFormat format = DateFormat.getDateTimeInstance();

                area.append(format.format(new Date()));
                area.append(": ");
                area.append(text);
                area.append("\n");
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            op.run();
        } else {
            SwingUtilities.invokeLater(op);
        }
    }
}
