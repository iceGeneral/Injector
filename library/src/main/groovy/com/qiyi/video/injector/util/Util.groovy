package com.qiyi.video.injector.util

import com.qiyi.video.injector.TrackTarget
import groovy.json.JsonSlurper

public class Util {

    public static List<TrackTarget> json2TrackTarget(String json) {
        def jsonList = new JsonSlurper().parseText(json)
        List<TrackTarget> trackTargets = new ArrayList<>()
        jsonList.each {
            it ->
                def jsonInst = it.inst
                TrackTarget.Inst inst = new TrackTarget.Inst(jsonInst.owner, jsonInst.methodName, jsonInst.methodDesc, jsonInst.argIndexes)
                TrackTarget trackTarget = new TrackTarget(it.className, it.methodName, it.methodDesc, inst)
                trackTargets.add(trackTarget)
        }
        return trackTargets
    }

}
