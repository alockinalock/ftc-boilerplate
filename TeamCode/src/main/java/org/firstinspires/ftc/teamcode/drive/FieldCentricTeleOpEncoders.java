package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

// Annotations in Java are preceeded by an @ symbol.
// They are meant to provide metadata on a class or method (depending on what they appear above).
//
// In FTC programming, the @TeleOp annotation is a necessity. It communicates to the REV Control Hub:
//    - What name to display for the Java file in the Driver Station application (name field)
//    - What group to put the file into, for organization purposes, in the Driver Station application (group field)
// Most of the time, the only field you need is the name field. The group field is utilized here for further organization.
@TeleOp(name = "Field Centric TeleOp w/ Encoders", group = "Driver Control Period")
public class FieldCentricTeleOpEncoders extends OpMode {

    // DcMotorEx is the type we use for any motor hardware.
    DcMotorEx frontLeft, frontRight, backLeft, backRight;

    // From Wikipedia: An inertial measurement unit (IMU) is an electronic device that measures and reports a body's specific force, angular rate, and sometimes the orientation of the body.
    // In our case, we need the built in IMU in the REV Control Hubs to field centric control.
    // The type is simply what kind of IMU is installed in the REV Control Hub. Most of the time, it's BHI260IMU.
    BHI260IMU imu;

    // A timer, which begins from program start. Displayed to Driver Station application.
    private ElapsedTime runtime = new ElapsedTime();

    // The init function is a necesity of any TeleOp class. 
    // It runs once at start.
    public void init() {

    }

    // The loop function is a necessity of any TeleOp class. 
    // It repeatedly runs any code placed inside it, continously until the program has been halted.
    //
    // Keen-eyed programmers may also have noticed the Java annotation: @Override.
    // It overrides the function with the same name in a superclass.
    @Override
    public void loop() {

    }
}