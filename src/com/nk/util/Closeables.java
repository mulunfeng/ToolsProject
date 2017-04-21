/**
 *
 */
package com.nk.util;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyuyang
 */
public class Closeables {

    private static Closeables instance = new Closeables();

    /**
     * 关闭资源，不抛出IO异常
     *
     * @param io IO对象
     */
    @Deprecated
    public static Closeables closeQuietly(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException ignored) {
            }
        }
        return instance;
    }

    /**
     * 关闭资源，不抛出IO异常
     *
     * @param io IO对象
     */
    public static Closeables close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException ignored) {
            }
        }
        return instance;
    }

    /**
     * 关闭资源，不抛出异常
     *
     * @param resources 资源对象
     */
    public static Closeables close(Closeable... resources) {
        if (resources != null) {
            for (Closeable resource : resources) {
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return instance;
    }


    /**
     * 关闭连接
     *
     * @param connection 连接
     * @param statement  声明
     * @param resultSet  结果集合
     */
    public static Closeables close(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ignored) {
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ignored) {
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
        return instance;
    }

    /**
     * 关闭服务
     *
     * @param lifeCycle 生命周期对象
     */
    public static Closeables close(LifeCycle lifeCycle) {
        if (lifeCycle != null) {
            lifeCycle.stop();
        }
        return instance;
    }

    /**
     * 关闭生命周期对象，不抛出异常
     *
     * @param lifeCycles 生命周期对象
     */
    public static Closeables close(LifeCycle... lifeCycles) {
        if (lifeCycles != null) {
            for (LifeCycle lifeCycle : lifeCycles) {
                if (lifeCycle != null) {
                    lifeCycle.stop();
                }
            }
        }

        return instance;
    }

    /**
     * 关闭一组服务
     *
     * @param lifeCycles 生命周期对象
     * @return
     */
    public static <T extends LifeCycle> Closeables close(Collection<T> lifeCycles) {
        if (lifeCycles != null && lifeCycles.size() > 0) {
            for (LifeCycle lifeCycle : lifeCycles) {
                close(lifeCycle);
            }
        }
        return instance;
    }

    /**
     * 关闭时钟
     *
     * @param timer 时钟
     * @return
     */
    public static Closeables close(Timer timer) {
        if (timer != null) {
            try {
                timer.cancel();
            } catch (Throwable e) {
            }
        }
        return instance;
    }

    /**
     * 关闭文件锁
     *
     * @param lock 文件锁
     * @return
     */
    public static Closeables close(FileLock lock) {
        if (lock != null) {
            try {
                lock.release();
            } catch (IOException e) {
            }
        }
        return instance;
    }

    /**
     * 关闭文件锁
     *
     * @param thread 文件锁
     * @return
     */
    public static Closeables close(Thread thread) {
        if (thread != null) {
            thread.interrupt();
        }
        return instance;
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return
     */
    public static Closeables delete(File file) {
        if (file != null) {
            try {
                file.delete();
            } catch (Throwable e) {
            }
        }
        return instance;
    }

    /**
     * 立即关闭线程池
     *
     * @param executor 线程池
     */
    public static Closeables close(ExecutorService executor) {
        if (executor != null) {
            executor.shutdownNow();
        }
        return instance;
    }

    /**
     * 关闭连接池
     *
     * @param dataSource 连接池
     */
    public static Closeables close(DataSource dataSource) {
        if (dataSource == null) {
            return instance;
        }
        if (dataSource instanceof Closeable) {
            return close((Closeable) dataSource);
        }
        // 兼容DBCP,反射调用close方法
        Class<?> clazz = dataSource.getClass();
        try {
            Method method = clazz.getDeclaredMethod("close");
            method.invoke(dataSource);
        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (IllegalAccessException ignored) {
        }
        return instance;
    }

    /**
     * 关闭线程池
     *
     * @param executor 线程池
     * @param timeout  超时时间(毫秒)
     */
    public static Closeables close(ExecutorService executor, long timeout) {
        if (executor != null) {
            if (timeout <= 0) {
                executor.shutdownNow();
            } else {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }
        return instance;
    }

}
