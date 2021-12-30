  //----------------------------------------
  ctx.populateContext([
    ctx.Entity("bereshitLander", "bereshit", {
        state: "Landing",
        prevState: "PostSeperation",
        imuStatus: ["off","off","off"],
        thrusterStatus: ["off","off"],
        strStatus: ["off","off"],
        imuCalibFlag: ["no","no","no"],
        valveStatus: ["open","open","close"],
        lulavStatus: "on",
        rfLandingSensorStatus: "on",
        landinLimitSec: 100,
       initCommStatus: "initCommEmer",
        imsStatus: "off",
        payloadStatus: "off",
        subState: "init",
        prevSunState: "none",
        requieredInertial3Axis: [1,2,3],
        requieredInertial3AxisTolerance:[0.1, 0.1, 0.1],
        PONR: false,
        initState: true,
        imuCurrentNo: 0,
    })
  ])

  ctx.registerQuery('init', function (entity) {
    return entity.state  == "Landing" && entity.initState
  })

  ctx.registerQuery('imu Calib req', function (entity) {
    return entity.state  == "Landing" && entity.initState && entity.imuStatus[entity.imuCurrentNo].equals("on") && entity.imuCalibFlag[entity.imuCurrentNo].equals("no")
  })

  ctx.registerQuery('init ended', function (entity) {
    return entity.state  == "Landing" && !entity.initState //&& entity.subState.equals("init")
  })

  ctx.registerQuery('oriantation', function (entity) {
    return entity.state.equals("Landing") && GNCcheckOrientation()
  })


 ctx.registerEffect('initEnded', function () {
    e = ctx.getEntityById('bereshitLander')
    e.initState = false
    ctx.updateEntity(e)
  })

  ctx.registerEffect('imuTurnedOn', function (data) {
    bp.log.info("imuIsOn "+data.no)
    e = ctx.getEntityById('bereshitLander')
    e.imuStatus[data.no] = "on"
    e.imuCurrentNo = data.no                    //save current imu as context
    ctx.updateEntity(e)
  })

  ctx.registerEffect('imuCalibrated', function (data) {
    bp.log.info("imuCalibrated "+data.no)
    e = ctx.getEntityById('bereshitLander')
    e.imuCalibFlag[data.no] = "yes"
    ctx.updateEntity(e)
  })

  ctx.registerEffect('strIsOn', function (data) {
    bp.log.info("strIsOn "+data.no)
    e = ctx.getEntityById('bereshitLander')
    e.strStatus[data.no] = "on"
    ctx.updateEntity(e)
  })

  ctx.registerEffect('lulaStatusOn', function () {
    bp.log.info("lulaStatusOn")
    e = ctx.getEntityById('bereshitLander')
    e.lulavStatus = "on"
    ctx.updateEntity(e)
  })
  ctx.registerEffect('RFLandingSensorOn', function () {
    bp.log.info("RFLandingSensorOn")
    e = ctx.getEntityById('bereshitLander')
    e.rfLandingSensorStatus = "on"
    ctx.updateEntity(e)
  })
  ctx.registerEffect('commInit', function () {
    bp.log.info("commInit")
    e = ctx.getEntityById('bereshitLander')
    e.initCommStatus = "Init_Comm"
    ctx.updateEntity(e)
  })

  ctx.registerEffect('thrusterIsOn', function (data) {
    bp.log.info("thrusterIsOn "+data.no)
    e = ctx.getEntityById('bereshitLander')
      e.thrusterStatus[data.no] = "on"
    ctx.updateEntity(e)
  })

 ctx.registerEffect('orientation', function () {
    e = ctx.getEntityById('bereshitLander')
    e.initState = false
    ctx.updateEntity(e)
  })

function GNCcheckOrientation() {
  e = ctx.getEntityById('bereshitLander')
  if (true)

  return true;
}
