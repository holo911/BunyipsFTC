package org.firstinspires.ftc.teamcode.common;


import android.util.Size;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Latest wrapper to support the v8.2+ SDK's included libraries for Camera operation.
 * Allows TFOD and AprilTag processors to be used in OpModes.
 * Vuforia is not supported, as we don't have any effective uses for it, and this feature is
 * getting phased out by the SDK.
 *
 * @author Lucas Bubner, 2023
 */
// Using Java as opposed to Kotlin as null safety is not a major concern
// due to the initialisation routine of the WebcamName device.
public class Vision extends BunyipsComponent {
    // Arrays to store the data from the processors
    private final List<AprilTagData> aprilTagData = new ArrayList<>();
    private final List<TfodData> tfodData = new ArrayList<>();
    private final WebcamName webcam;
    private TfodProcessor tfod = null;
    private AprilTagProcessor aprilTag = null;
    private VisionPortal visionPortal = null;

    public Vision(@NonNull BunyipsOpMode opMode, WebcamName webcam) {
        super(opMode);
        this.webcam = webcam;
    }

    /**
     * Builds the VisionPortal after the VisionPortal has been constructed.
     *
     * @param builder Processor-rich builder pattern for the VisionPortal
     * @return VisionPortalImpl
     */
    private VisionPortal constructVisionPortal(VisionPortal.Builder builder) {
        return builder
                .setCamera(webcam)
                .setCameraResolution(new Size(1280, 720))
                .enableCameraMonitoring(true)
                .setAutoStopLiveView(true)
                // Set any additional VisionPortal settings here
                .build();
    }

    /**
     * Initialises the Vision class with the specified processors.
     * This method should only be called once per OpMode.
     * Processors will be STOPPED by default, you must call start() after initialising.
     *
     * @param processors TFOD and/or AprilTag
     */
    public void init(Processors... processors) {
        List<VisionProcessor> initialisedProcessors = new ArrayList<>();

        for (Processors processor : processors) {
            switch (processor) {
                case TFOD:
                    tfod = new TfodProcessor.Builder()
                            // Specify custom TFOD settings here
                            .build();
                    initialisedProcessors.add(tfod);
                    break;
                case APRILTAG:
                    aprilTag = new AprilTagProcessor.Builder()
                            // Logitech C920
                            .setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
                            // Specify custom AprilTag settings here
                            .build();
                    initialisedProcessors.add(aprilTag);
                    break;
            }
        }

        // Initialise the VisionPortal with our newly created processors
        VisionPortal.Builder builder = new VisionPortal.Builder();
        for (VisionProcessor processor : initialisedProcessors) {
            builder.addProcessor(processor);
        }

        visionPortal = constructVisionPortal(builder);

        // Disable the vision processors by default. The OpMode must call start() to enable them.
        for (VisionProcessor processor : initialisedProcessors) {
            visionPortal.setProcessorEnabled(processor, false);
        }

        // Disable live view by default
        visionPortal.stopLiveView();
    }

    /**
     * Add a custom VisionProcessor that is not AprilTag or TFOD.
     * This method should be called with the VisionPortal already initialised with init().
     * !! This method may be expensive to call as it will reconstruct the VisionPortal.
     */
    public void addCustomProcessor(VisionProcessor processor) {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised!");
        }
        if (processor instanceof TfodProcessor || processor instanceof AprilTagProcessor) {
            throw new IllegalArgumentException("Cannot add TFOD or AprilTag processors with this method!");
        }
        // Reconstruct the VisionPortal with the new processor
        VisionPortal.Builder builder = new VisionPortal.Builder();
        if (tfod != null) {
            builder.addProcessor(tfod);
        }
        if (aprilTag != null) {
            builder.addProcessor(aprilTag);
        }
        builder.addProcessor(processor);
        visionPortal = constructVisionPortal(builder);
    }

    /**
     * Start or stop a custom VisionProcessor that is not AprilTag or TFOD. (Level 2)
     */
    public void setCustomProcessorState(VisionProcessor processor, boolean state) {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised!");
        }
        if (processor instanceof TfodProcessor || processor instanceof AprilTagProcessor) {
            throw new IllegalArgumentException("Cannot start TFOD or AprilTag processors with this method!");
        }
        visionPortal.setProcessorEnabled(processor, state);
    }

    /**
     * Start desired processors. This method must be called before trying to extract data from
     * the cameras, and must be already initialised with the init() method.
     *
     * @param processors TFOD and/or AprilTag
     */
    public void start(Processors... processors) {
        // Resume the stream if it was previously stopped or is not running
        if (visionPortal.getCameraState() == VisionPortal.CameraState.CAMERA_DEVICE_READY ||
                visionPortal.getCameraState() == VisionPortal.CameraState.STOPPING_STREAM) {
            // Note if the camera state is STOPPING_STREAM, it will block the thread until the
            // stream is resumed. This is a documented operation in the SDK.
            visionPortal.resumeStreaming();
        }
        for (Processors processor : processors) {
            switch (processor) {
                case TFOD:
                    if (tfod == null) {
                        throw new IllegalStateException("TFOD processor is not initialised!");
                    }
                    visionPortal.setProcessorEnabled(tfod, true);
                    break;
                case APRILTAG:
                    if (aprilTag == null) {
                        throw new IllegalStateException("AprilTag processor is not initialised!");
                    }
                    visionPortal.setProcessorEnabled(aprilTag, true);
                    break;
            }
        }
    }

    /**
     * Stop desired processors (Level 2).
     * <p>
     * This method should be called when hardware resources no longer
     * need to be allocated to operating the cameras, and should have the option to be re-enabled
     * with start().
     * <p>
     * Note: The VisionPortal is automatically closed at the end of the OpMode's run time, calling
     * stop() or terminate() is not required at the end of an OpMode.
     * <p>
     * Additionally passing stopPortal as true will pause the Camera Stream (Level 3). Pausing
     * the camera stream will automatically disable any running processors. Note this may
     * take some additional time to resume the stream if start() is called again. If you don't plan
     * on using the camera stream again, it is recommended to call terminate() instead.
     *
     * @param stopPortal Whether to pause the Camera Stream (Level 3)
     * @param processors TFOD and/or AprilTag
     */
    public void stop(boolean stopPortal, Processors... processors) {
        if (stopPortal) {
            // Pause the processor, this will also auto-close any VisionProcessors
            visionPortal.stopStreaming();
            return;
        }
        // Disable processors without pausing the stream
        for (Processors processor : processors) {
            switch (processor) {
                case TFOD:
                    if (tfod == null) {
                        throw new IllegalStateException("TFOD processor is not initialised!");
                    }
                    visionPortal.setProcessorEnabled(tfod, false);
                    break;
                case APRILTAG:
                    if (aprilTag == null) {
                        throw new IllegalStateException("AprilTag processor is not initialised!");
                    }
                    visionPortal.setProcessorEnabled(aprilTag, false);
                    break;
            }
        }
    }

    /**
     * Terminate all VisionPortal resources (Level 4).
     * <p>
     * Use this method when you are completely done with the VisionPortal and want to free up
     * all available resources. This method will automatically disable all processors and close
     * the VisionPortal, and cannot be undone without calling init() again.
     * <p>
     * It is strongly discouraged to reinitialise the VisionPortal in the same OpMode, as this
     * takes significant time and may cause the OpMode to hang or become unresponsive. Instead,
     * use the start() and stop() methods to enable/disable the VisionPortal.
     */
    public void terminate() {
        visionPortal.close();
    }

    /**
     * Get the current status of the camera attached to the VisionPortal.
     */
    public VisionPortal.CameraState getStatus() {
        return visionPortal.getCameraState();
    }

    /**
     * Get the current Frames Per Second of the VisionPortal.
     */
    public double getFps() {
        return visionPortal.getFps();
    }

    /**
     * Start or stop the live camera view (Level 1).
     * When initialised, live view is disabled by default.
     */
    public void setLiveView(boolean enabled) {
        if (enabled) {
            visionPortal.resumeLiveView();
        } else {
            visionPortal.stopLiveView();
        }
    }

    /**
     * Tick the camera stream and extract data from the processors.
     * This data is stored in the instance and can be accessed with the getters.
     */
    public void tick() {
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            // Camera must be initialised and streaming before we can extract data
            return;
        }

        // For every processor, check if it is enabled and extract data if it is
        for (Processors processor : Processors.values()) {
            switch (processor) {
                case TFOD:
                    if (tfod != null && visionPortal.getProcessorEnabled(tfod)) {
                        interpretTfod();
                    }
                    break;
                case APRILTAG:
                    if (aprilTag != null && visionPortal.getProcessorEnabled(aprilTag)) {
                        interpretAprilTag();
                    }
                    break;
            }
        }
    }

    private void interpretAprilTag() {
        List<AprilTagDetection> detections = aprilTag.getFreshDetections();
        if (detections == null) {
            return;
        }
        aprilTagData.clear();
        for (AprilTagDetection detection : detections) {
            aprilTagData.add(new AprilTagData(
                    detection.id,
                    detection.hamming,
                    detection.decisionMargin,
                    detection.center,
                    detection.corners,
                    detection.metadata != null ? detection.metadata.name : null,
                    detection.metadata != null ? detection.metadata.tagsize : null,
                    detection.metadata != null ? detection.metadata.fieldPosition : null,
                    detection.metadata != null ? detection.metadata.fieldOrientation : null,
                    detection.metadata != null ? detection.metadata.distanceUnit : null,
                    detection.ftcPose != null ? detection.ftcPose.x : null,
                    detection.ftcPose != null ? detection.ftcPose.y : null,
                    detection.ftcPose != null ? detection.ftcPose.z : null,
                    detection.ftcPose != null ? detection.ftcPose.pitch : null,
                    detection.ftcPose != null ? detection.ftcPose.roll : null,
                    detection.ftcPose != null ? detection.ftcPose.yaw : null,
                    detection.ftcPose != null ? detection.ftcPose.range : null,
                    detection.ftcPose != null ? detection.ftcPose.bearing : null,
                    detection.ftcPose != null ? detection.ftcPose.elevation : null,
                    detection.rawPose,
                    detection.frameAcquisitionNanoTime
            ));
        }
    }

    private void interpretTfod() {
        List<Recognition> recognitions = tfod.getFreshRecognitions();
        if (recognitions == null) {
            return;
        }
        tfodData.clear();
        for (Recognition recognition : recognitions) {
            tfodData.add(new TfodData(
                    recognition.getLabel(),
                    recognition.getConfidence(),
                    recognition.getLeft(),
                    recognition.getTop(),
                    recognition.getRight(),
                    recognition.getBottom(),
                    recognition.getWidth(),
                    recognition.getHeight(),
                    recognition.getImageWidth(),
                    recognition.getImageHeight(),
                    recognition.estimateAngleToObject(AngleUnit.DEGREES),
                    recognition.estimateAngleToObject(AngleUnit.RADIANS)
            ));
        }
    }

    /**
     * Primary getter for all TFOD data.
     *
     * @return List of all TFOD objects and their data
     */
    public List<TfodData> getTfodData() {
        return this.tfodData;
    }

    /**
     * Primary getter for all AprilTag data.
     *
     * @return List of all AprilTag objects and their data
     */
    public List<AprilTagData> getAprilTagData() {
        return this.aprilTagData;
    }

    public enum Processors {
        /**
         * Caution! Using TFOD and using OpModes with high load may cause a watchdog timeout.
         * Ensure to test for this, as system memory may deplete and cause unexpected behaviour.
         */
        TFOD,
        APRILTAG
    }
}