package org.stajistics.management.beans;

import static org.stajistics.Util.assertNotNull;

import org.stajistics.task.TaskService;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultTaskServiceMXBean implements TaskServiceMXBean {

    private final TaskService taskService;

    public DefaultTaskServiceMXBean(final TaskService taskService) {
        assertNotNull(taskService, "taskService");
        this.taskService = taskService;
    }

    @Override
    public boolean getRunning() {
        return taskService.isRunning();
    }

}
