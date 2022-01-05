let entities = [
  ctx.Entity('beresheetLander', 'beresheet', {
    state: 'Landing',
    prevState: 'PostSeparation',
    // imuStatus: ['off', 'off', 'off'],
    thrusterStatus: ['off', 'off'],
    // strStatus: ['off', 'off'],
    // imuCalibFlag: ['no', 'no', 'no'],
    valveStatus: ['open', 'open', 'close'],
    lulavStatus: 'on',
    rfLandingSensorStatus: 'on',
    landinLimitSec: 100,
    initCommStatus: 'initCommEmer',
    imsStatus: 'off',
    payloadStatus: 'off',
    subState: 'init',
    prevSunState: 'none',
    requiredInertial3Axis: { x: 1, y: 2, z: 3 },
    requiredInertial3AxisTolerance: { x: 0.1, y: 0.1, z: 0.1 },
    PONR: false,
    initState: true
    // imuCurrentNo: 0
  })
]

for (let i = 0; i < 3; i++) {
  entities.push(ctx.Entity('imu' + i, 'imu', { no: i, isOn: false, isCalibrated: false }))
}

for (let i = 0; i < 2; i++) {
  entities.push(ctx.Entity('str' + i, 'str', { no: i, isOn: false }))
}

ctx.populateContext(entities)

// ctx.registerQuery('turnImu', function (entity) {
//   return entity.type == 'imu' && !entity.isOn
// })


ctx.registerQuery('imu', function (entity) {
  return entity.type == 'imu'
})

ctx.registerQuery('str', function (entity) {
  return entity.type == 'str'
})

ctx.registerQuery('init', function (entity) {
  return entity.type == 'beresheet' && entity.state == 'Landing' && entity.initState //&& entity2.type == 'imu'
})

ctx.registerQuery('uncalibrated imu', function (entity) {
  return entity.type == 'imu' && !entity.isCalibrated == true
})

ctx.registerQuery('init ended', function (entity) {
  return entity.type == 'beresheet' && entity.state == 'Landing' && !entity.initState //&& entity.subState.equals("init")
})

ctx.registerQuery('oriantation', function (entity) {
  return entity.state == 'Landing' && GNCcheckOrientation()
})


ctx.registerEffect('initEnded', function (data) {
  let e = ctx.getEntityById('beresheetLander')
  e.initState = false
  ctx.updateEntity(e)
})

ctx.registerEffect('imuOn', function (data) {
  if (!data.started) {
    bp.log.info('imuIsOn ' + data.no)
    let e = ctx.getEntityById('imu'+data.no)
    e.isOn = true
    ctx.updateEntity(e)
  }

})

ctx.registerEffect('imuCalibrated', function (data) {
  if (!data.started) {
    bp.log.info('imuCalibrated ' + data.no)
    let e = ctx.getEntityById('imu' + data.no)
    e.isCalibrated = true
    ctx.updateEntity(e)
  }
})

ctx.registerEffect('strIsOn', function (data) {
  bp.log.info('strIsOn ' + data.no)
  let e = ctx.getEntityById('beresheetLander')
  e.strStatus[data.no] = 'on'
  ctx.updateEntity(e)
})

ctx.registerEffect('lulaStatusOn', function (data) {
  bp.log.info('lulaStatusOn')
  let e = ctx.getEntityById('beresheetLander')
  e.lulavStatus = 'on'
  ctx.updateEntity(e)
})
ctx.registerEffect('RFLandingSensorOn', function (data) {
  bp.log.info('RFLandingSensorOn')
  let e = ctx.getEntityById('beresheetLander')
  e.rfLandingSensorStatus = 'on'
  ctx.updateEntity(e)
})
ctx.registerEffect('commInit', function (data) {
  bp.log.info('commInit')
  let e = ctx.getEntityById('beresheetLander')
  e.initCommStatus = 'Init_Comm'
  ctx.updateEntity(e)
})

ctx.registerEffect('thrusterIsOn', function (data) {
  bp.log.info('thrusterIsOn ' + data.no)
  let e = ctx.getEntityById('beresheetLander')
  e.thrusterStatus[data.no] = 'on'
  ctx.updateEntity(e)
})

ctx.registerEffect('orientation', function (data) {
  let e = ctx.getEntityById('beresheetLander')
  e.initState = false
  ctx.updateEntity(e)
})

function GNCcheckOrientation() {
  let e = ctx.getEntityById('beresheetLander')
  if (true)

    return true
}
