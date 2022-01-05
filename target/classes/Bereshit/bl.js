//-----------------------------------------------
//bp.log.setLevel('Off')

const AnyStrOn = bp.EventSet('AnyStrOn', e => e.name.startsWith('strOn'))
const AnyThrusterOn = bp.EventSet('AnyThrusterOn', e => e.name.startsWith('thrusterOn'))
const AnyOpenUpstreamValves = bp.EventSet('AnyOpenUpstreamValvesAnyOpenUpstreamValves', e => e.name.startsWith('openUpstream'))
const AnyImuCalib = bp.EventSet('AnyImuCalib', e => e.name.startsWith('imuCalib'))

function hardwareEvent(name, data, isStart) {
  if (!data) data = {}
  data.start = isStart
  return Event(name, data)
}

function hardwareAction(name, data, cond) {
  if (cond && cond())
    return
  bp.log.info('hardwareAction ' + name+' '+data.no)
  sync({ request: hardwareEvent(name, data, true) })
  sync({ waitFor: hardwareEvent(name, data, false) })
}

ctx.bthread('Init', 'init', function (entity) {
  bp.log.info('Init ' + entity.state)

  //Check IMU, if "off", turn "on". if not calibrate, calibrate.
  // for (imu in entity.imuStatus) {
  //   bp.log.info('imu ' + eImu)
  //   // hardwareAction('imuOn', { no: imu }, () => entity.imuStatus[imu].equals('on'))
  //   hardwareAction('imuOn', { no: eImu },  false)
  //  }
  sync({ request: Event('StartImuOn') })

  // for (str in entity.strStatus) {
  //   hardwareAction('strOn', { no: str }, () => entity.strStatus[str].equals('off'))
  //
  //   // if (entity.strStatus[str].equals('off'))   // maybee doesn't required
  //   //   sync({ request: Event('strOn', { no: str }) })
  // }
  for (thruster in entity.thrusterStatus) {
    if (entity.thrusterStatus[thruster].equals('off'))   // maybee doesn't required
      sync({ request: Event('thrusterOn', { no: thruster }) })
    sync({ request: Event('openUpstreamValves', { no: thruster }) })
  }

  sync({ request: Event('powerOnLulav') })
  sync({ request: Event('powerOnRFLandingSensor') })

  let controlMonitor = function (device, landingLimit) {
    bthread('Control monitor ' + device + ' ' + landingLimit, function () {
      while (true) {
        //Do Monitoring of 'device'
        sync({ waitFor: bp.all })
      }
    })
  }
  controlMonitor('termal', entity.landinLimitSec)
  controlMonitor('baterryCharge', entity.landinLimitSec)
  controlMonitor('batteryDOD', entity.landinLimitSec)

  if (entity.initCommStatus.equals('off') || entity.initCommStatus.equals('initCommEmer'))
    sync({ request: Event('activeInitComm') })

  sync({ request: Event('doBitImsAndPld') })


  // till here - calibration must be finished
  //gnc

  sync({ request: Event('initEnded') })

})

// ctx.bthread('imu Turn On', `turnImu`, function (imuData) {
//   while(true) {
//     sync({waitFor: Event('St`artImuOn')})
//     bp.log.info('imu ' + imuData.id)
//     // hardwareAction('imuOn', { no: imu }, () => entity.imuStatus[imu].equals('on'))
//     hardwareAction('imuOn', {no: imuData.id}, false)
//   }
// })


ctx.bthread('imu Turn On', 'imu', function (imuid) {
  while (true) {
    sync({ waitFor: hardwareEvent('imuOn', { no: imuid.no }, true) })
    bp.log.info('in imuOn ' + imuid)
    // completed turn on imu[x] (triggers a ctx effect)
    sync({ request: hardwareEvent('imuOn', { no: imuid.no }, false) })

    //TODO check if calibration is initiated. if not - initiate it
  }
})

ctx.bthread('doImuCalib', 'uncalibrated imu', function (entity) {
  while (true) {
    //Do imu[x] calibration
    //reset calibration timeoute for imu[x]
    sync({ request: Event('imuCalibrated', { no: entity.no }) })
  }
})

ctx.bthread('str Turn On', 'str', function (strData) {
  while (true) {
    sync({waitFor: hardwareEvent('strOn', {no: strData.id}, true)})
    bp.log.info('in imuOn ' + entity.data.no)
    // completed turn on str[x] (triggers a ctx effect)
    sync({request: hardwareEvent('strOn', {no: strData.id}, false)})
  }


  //   e = sync({ waitFor: AnyStrOn })
  //   //Do turn on str[x]
  //   sync({ request: Event('strIsOn', { no: e.data.no }) })
  // }
})

bthread('thruster Turn On', function () {
  while (true) {
    e = sync({ waitFor: AnyThrusterOn })
    //Do turn on thruster[x]
    sync({ request: Event('thrusterIsOn', { no: e.data.no }) })
  }

})

bthread('open upstream thruster', function (entity) {
  while (true) {
    e = sync({ waitFor: [Event('openUpstreamValves')] })
    entity.thrusterStatus[e.data.no] = 'on'
    //Do open upstream valves for thruster[x]
  }
})

bthread('power on lulav', function () {
  while (true) {
    e = sync({ waitFor: [Event('powerOnLulav')] })
    //Do power on lulav landing sesnsor
    sync({ request: Event('lulaStatusOn') })
  }
})
bthread('power on RF landing sensor', function () {
  while (true) {
    e = sync({ waitFor: [Event('powerOnRFLandingSensor')] })
    //Do power on RF landing sesnsor
    sync({ request: Event('RFLandingSensorOn') })
  }
})
bthread('Active init comm', function () {
  while (true) {
    e = sync({ waitFor: [Event('activeInitComm')] })
    //initiate communication system
    sync({ request: Event('commInit') })
  }
})

bthread('Do bit Ims and Payload', function () {
  while (true) {
    e = sync({ waitFor: [Event('doBitImsAndPld')] })
    doBit = function (device) {
      bthread('Bit ' + device, function () {
        //Do Bit of 'device'
        e = sync({ request: Event('powerOn' + device) })
        e = sync({ request: Event('operate' + device) })
        e = sync({ request: Event('powerOff' + device) })
      })
    }
    doBit('Ims')  //Do bit to IMS
    doBit('Payload')   //Do bit to Payload
  }
})
ctx.bthread('End landing state', 'init ended', function () {
  while (true) {
    sync({ waitFor: Event('initEnded') })
    if (use_accepting_states) {
      // AcceptingState.Continuing()
      AcceptingState.Stopping()
    }
  }
})




