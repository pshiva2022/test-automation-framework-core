package com.dummycompany.test.framework.core.utils;

import com.google.gson.JsonObject;

import com.dummycompany.test.framework.core.context.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class SVTUtils {
    private static final Logger LOG = LogManager.getLogger(RunnerUtil.class);
    public static final boolean isSvtProfile = Props.getEnvOrPropertyValue("runnerProfile", "junit").equalsIgnoreCase("svt");
    public static final boolean isSingleTest = Boolean.parseBoolean(Props.getEnvOrPropertyValue("singleTest", "false"));

    public static void startEventTimer(World world, int eventId){
        if(isSvtProfile && !isSingleTest) {
            world.scenarioContext.put("startTime", (new Date()).getTime());
            world.scenarioContext.put("eventId", eventId);
            world.scenarioContext.put("eventStatus", "started");
        }
    }

    public static void stopEventTimer(World world, String status) {
        if(isSvtProfile && !isSingleTest) {
            String event, transTime, buildId;
            int eventId = world.getIntFromScenarioContext("eventId"), code;
            double timeTaken = ((new Date()).getTime() - world.getLongFromScenarioContext("startTime")) / 1000d;
            if (status.equalsIgnoreCase("pass")) {
                code = 200;
                event = "completed";
            } else {
                code = 500;
                event = "failed";
            }
            transTime = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'").format(new Date());
            buildId = Props.getEnvOrPropertyValue("BuildID");

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Event", event);
            jsonObject.addProperty("Res_Code", code);
            jsonObject.addProperty("Res_Status", status);
            jsonObject.addProperty("Res_Length", 1000);
            jsonObject.addProperty("Res_Time", timeTaken);
            jsonObject.addProperty("Corr_ID", buildId);
            jsonObject.addProperty("EP_ID", eventId);
            jsonObject.addProperty("Tran_Time", transTime);


            world.scenarioContext.put("eventStatus", "completed");
        }
    }

    public static void delayExecutionForRPM(int serialNumber, int RPM, World world){
        long execDelay = Long.parseLong(world.scenarioContext.get("execDelay").toString());
        int delayInSeconds = Math.max((((serialNumber / RPM) * 60) - Integer.parseInt("" + execDelay / 1000)), 0) + new Random().nextInt(60);
        world.log("RPM: " + RPM + ". ExecDelay: " + execDelay + ". CommonSteps-Delay TIMESTAMP: "+ getFormattedDate("fullDateTimeNoMS") + " DELAY: "+delayInSeconds);
        LOG.info("CommonSteps-Delay TIMESTAMP: "+ getFormattedDate("fullDateTimeNoMS")+"  DELAY: "+delayInSeconds);
        if(delayInSeconds>0)
            for(int i = 0; i <= delayInSeconds; ++i) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            }
        world.log("Timestamp after delayExecutionForRPM: " + getFormattedDate("fullDateTimeNoMS"));
    }

    private static String getFormattedDate(String format) {
        DateFormat dateFormat = null;
        switch(format) {
            case "fullDateTime":
                dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.sss\'Z\'");
                break;
            case "fullDateTimeNoMS":
                dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
                break;
            case "fullDateTimeNoTZ":
                dateFormat = new SimpleDateFormat("yyyy-MM-dd\' \'HH:mm:ss");
                break;
            case "onlyDate":
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                break;
        }
        Date date = new Date();
        return Objects.requireNonNull(dateFormat).format(date);
    }
}
