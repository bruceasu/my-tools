/*
 * Copyright © 2016 Victor.su<victor.su@gwtsz.net>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package asu.tool.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class RabbitMQTool implements AutoCloseable
{
    private static final Log log = Logs.get();
    private ConnectionFactory factory;
    private Connection conn;
    private Object lock = new Object();
    public static ConnectionFactory factory() {
        return new ConnectionFactory();
    }

    public RabbitMQTool(ConnectionFactory factory) throws IOException, TimeoutException
    {
        if (factory == null)
            throw new NullPointerException("argument should not be null.");
        this.factory = factory;
        factory.setAutomaticRecoveryEnabled(true);
        //        factory.setConnectionTimeout(5000);
        conn = factory.newConnection();
    }

    public Connection connection()
    {
        if (conn == null) {
            synchronized (lock) {
                if (conn == null) {
                    try {
                        conn = factory.newConnection();
                    } catch (IOException | TimeoutException e) {
                        log.error(e);
                    }
                }
            }
        }
        return conn;
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }
    public Connection newConnection() throws IOException, TimeoutException
    {
        return factory.newConnection();
    }

    public void closeQuietly(Connection conn)
    {
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
            }
        }
    }

    public void closeQuietly(Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException |TimeoutException e) {
            }
        }
    }
}
