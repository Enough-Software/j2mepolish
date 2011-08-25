package com.nutiteq.core;

import com.mgmaps.utils.AsyncRunner;
import com.mgmaps.utils.Queue;
import com.nutiteq.task.LocalTask;
import com.nutiteq.task.TasksRunner;
import com.nutiteq.task.TasksRunnerImpl;

//TODO jaanus : evaluate this implementation
public class MappingCore {
  private TasksRunner tasksRunner;
  private AsyncRunner asyncCalls;

  private static MappingCore instance;

  private MappingCore() {

  }

  public static MappingCore getInstance() {
    if (instance == null) {
      instance = new MappingCore();
    }

    return instance;
  }

  public TasksRunner getTasksRunner() {
    if (tasksRunner == null) {
      tasksRunner = new TasksRunnerImpl(new Queue());
    }

    return tasksRunner;
  }

  public void runAsync(final LocalTask task) {
    final AsyncRunner runner = new AsyncRunner(task);
    runner.start();
  }

  public static void clean() {
    getInstance().getTasksRunner().quit();
    instance = null;
  }

  public void setTasksRunner(final TasksRunner nextRunner) {
    tasksRunner = nextRunner;
  }
}
