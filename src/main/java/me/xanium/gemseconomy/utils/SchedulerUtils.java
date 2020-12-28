package me.xanium.gemseconomy.utils;

import me.xanium.gemseconomy.GemsEconomy;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

public class SchedulerUtils {

    public static void runLater(long delay, Runnable runnable)
    {
        Bukkit.getScheduler().runTaskLater(GemsEconomy.getInstance(), runnable, delay);
    }

    public static void runLaterAsync(long delay, Runnable runnable)
    {
        Bukkit.getScheduler().runTaskLaterAsynchronously(GemsEconomy.getInstance(), runnable, delay);
    }

    /**
     * Runs a task on another thread immediately.
     * @param runnable - Task to perform.
     */
    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(GemsEconomy.getInstance(), runnable);
    }

    /**
     * Runs a task on the main thread immediately
     * @param runnable - Task to perform
     */
    public static void run(Runnable runnable){
        Bukkit.getScheduler().runTask(GemsEconomy.getInstance(), runnable);
    }

    public static void runAtInterval(long interval, Runnable... tasks)
    {
        runAtInterval(0L, interval, tasks);
    }

    public static void runAtInterval(long delay, long interval, Runnable... tasks)
    {
        new BukkitRunnable()
        {
            private int index;

            @Override
            public void run()
            {
                if (this.index >= tasks.length)
                {
                    this.cancel();
                    return;
                }

                tasks[index].run();
                index++;
            }
        }.runTaskTimer(GemsEconomy.getInstance(), delay, interval);
    }

    public static void repeat(int repetitions, long interval, Runnable task, Runnable onComplete)
    {
        new BukkitRunnable()
        {
            private int index;

            @Override
            public void run()
            {
                index++;
                if (this.index >= repetitions)
                {
                    this.cancel();
                    if (onComplete == null)
                    {
                        return;
                    }

                    onComplete.run();
                    return;
                }

                task.run();
            }
        }.runTaskTimer(GemsEconomy.getInstance(), 0L, interval);
    }

    public static void repeatWhile(long interval, Callable<Boolean> predicate, Runnable task, Runnable onComplete)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (!predicate.call())
                    {
                        this.cancel();
                        if (onComplete == null)
                        {
                            return;
                        }

                        onComplete.run();
                        return;
                    }

                    task.run();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(GemsEconomy.getInstance(), 0L, interval);
    }

    public interface Task
    {
        void start(Runnable onComplete);
    }

    public static class TaskBuilder
    {
        private Queue<Task> taskList;

        public TaskBuilder()
        {
            this.taskList = new LinkedList<>();
        }

        public TaskBuilder append(TaskBuilder builder)
        {
            this.taskList.addAll(builder.taskList);
            return this;
        }

        public TaskBuilder appendDelay(long delay)
        {
            this.taskList.add(onComplete -> SchedulerUtils.runLater(delay, onComplete));
            return this;
        }

        public TaskBuilder appendTask(Runnable task)
        {
            this.taskList.add(onComplete ->
            {
                task.run();
                onComplete.run();
            });

            return this;
        }

        public TaskBuilder appendTask(Task task)
        {
            this.taskList.add(task);
            return this;
        }

        public TaskBuilder appendDelayedTask(long delay, Runnable task)
        {
            this.taskList.add(onComplete -> SchedulerUtils.runLater(delay, () ->
            {
                task.run();
                onComplete.run();
            }));

            return this;
        }

        public TaskBuilder appendTasks(long delay, long interval, Runnable... tasks)
        {
            this.taskList.add(onComplete ->
            {
                Runnable[] runnables = Arrays.copyOf(tasks, tasks.length + 1);
                runnables[runnables.length - 1] = onComplete;
                SchedulerUtils.runAtInterval(delay, interval, runnables);
            });

            return this;
        }

        public TaskBuilder appendRepeatingTask(int repetitions, long interval, Runnable task)
        {
            this.taskList.add(onComplete -> SchedulerUtils.repeat(repetitions, interval, task, onComplete));
            return this;
        }

        public TaskBuilder appendConditionalRepeatingTask(long interval, Callable<Boolean> predicate, Runnable task)
        {
            this.taskList.add(onComplete -> SchedulerUtils.repeatWhile(interval, predicate, task, onComplete));
            return this;
        }

        public TaskBuilder waitFor(Callable<Boolean> predicate)
        {
            this.taskList.add(onComplete -> new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if (!predicate.call())
                        {
                            return;
                        }

                        this.cancel();
                        onComplete.run();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }.runTaskTimer(GemsEconomy.getInstance(), 0L, 1L));
            return this;
        }

        public void runTasks()
        {
            this.startNext();
        }

        private void startNext()
        {
            Task task = this.taskList.poll();
            if (task == null)
            {
                return;
            }

            task.start(this::startNext);
        }
    }
}
