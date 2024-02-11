package org.murraybridgebunyips.bunyipslib.tasks.bases

import org.murraybridgebunyips.bunyipslib.BunyipsSubsystem

/**
 * A task, or command is an action that can be performed by a robot. This has been designed
 * to reflect closely the command-based programming style used in FRC, while still being
 * reflective of the past nature of how the Task system was implemented in BunyipsLib.
 * @author Lucas Bubner, 2024
 */
abstract class Task(timeoutSeconds: Double) : RobotTask {
    private var overrideOnConflict: Boolean? = null

    fun shouldOverrideOnConflict(): Boolean? {
        return overrideOnConflict
    }

    fun addDependency(dependencySubsystem: BunyipsSubsystem) {
        dependencySubsystem.addDependencyFromTask(this.hashCode())
        if (overrideOnConflict == null)
            overrideOnConflict = false
    }

    constructor(
        timeoutSeconds: Double,
        dependencySubsystem: BunyipsSubsystem,
        shouldOverrideConflictingTasks: Boolean
    ) : this(timeoutSeconds) {
        addDependency(dependencySubsystem)
        overrideOnConflict = shouldOverrideConflictingTasks
    }

    /**
     * Maximum timeout (sec) of the task. If set to 0 this will serve as an indefinite task, and
     * will only finish when isTaskFinished() returns true.
     */
    var timeout: Double = timeoutSeconds

    @Volatile
    var taskFinished = false
        private set

    private var startTime = 0L
    private var finisherFired = false

    /**
     * Define code to run once, when the task is started.
     */
    abstract override fun init()

    /**
     * To run as an activeLoop during this task's duration.
     */
    abstract fun periodic()

    /**
     * Should be called by your polling loop to run the task and manage all state properly.
     */
    final override fun run() {
        if (startTime == 0L) {
            init()
            startTime = System.nanoTime()
            // Must poll finished on the first iteration to ensure that the task does not overrun
            pollFinished()
        }
        // Here we check the taskFinished condition but don't poll updateFinishedState(), to ensure that the task is only
        // updated with latest finish information at the user's discretion
        if (taskFinished && !finisherFired) {
            onFinish()
            finisherFired = true
        }
        periodic()
    }

    /**
     * Finalising function to run once the task is finished.
     */
    abstract override fun onFinish()

    /**
     * Return a boolean to this method to add custom criteria if a task should be considered finished.
     * @return bool expression indicating whether the task is finished or not, timeout and OpMode state are handled automatically.
     */
    abstract fun isTaskFinished(): Boolean

    /**
     * Query (but not update) the finished state of the task.
     */
    override fun isFinished(): Boolean {
        return taskFinished && finisherFired
    }

    /**
     * Update and query the state of the task if it is finished.
     */
    override fun pollFinished(): Boolean {
        // Early return
        if (taskFinished) return finisherFired

        // Finish on user defined task finished condition, or by timeout
        taskFinished =
            (timeout != 0.0 && startTime != 0L && System.nanoTime() > startTime + timeout)
                    || isTaskFinished()

        // run() will handle firing the finisher, in which case we can return true and the polling loop can stop
        return taskFinished && finisherFired
    }

    /**
     * Reset a task to an uninitialised and unfinished state.
     */
    fun reset() {
        startTime = 0L
        taskFinished = false
        finisherFired = false
    }

    /**
     * Force a task to finish immediately.
     */
    fun finishNow() {
        taskFinished = true
    }

    /**
     * Force a task to finish immediately, and fire the onFinish() method without waiting
     * for the next polling loop. This method is useful when your task is about to die and
     * needs to finish up immediately.
     */
    fun forceFinish() {
        taskFinished = true
        finisherFired = true
        onFinish()
    }

    /**
     * @return Whether the task is currently running (calls to run() should be made)
     */
    val isRunning: Boolean
        get() = startTime != 0L && !isFinished()

    val deltaTime: Double
        get() {
            if (startTime == 0L)
                return 0.0
            return (System.nanoTime() - startTime) / NANOS_IN_SECONDS
        }

    companion object {
        const val NANOS_IN_SECONDS = 1000000000.0
    }
}