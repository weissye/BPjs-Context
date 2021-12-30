//-----------------------------------------------
bp.log.setLevel("Off");

  var AnyImuOn = bp.EventSet("AnyImuOn", e => e.name.startsWith("imuOn"))
  var AnyStrOn = bp.EventSet("AnyStrOn", e => e.name.startsWith("strOn"))
  var AnyThrusterOn = bp.EventSet("AnyThrusterOn", e => e.name.startsWith("thrusterOn"))
  var AnyOpenUpstreamValves = bp.EventSet("AnyOpenUpstreamValvesAnyOpenUpstreamValves", e => e.name.startsWith("openUpstream"))
  var AnyImuCalib = bp.EventSet("AnyImuCalib", e => e.name.startsWith("imuCalib"))

  ctx.bthread('Init', 'init', function (entity) {

      bp.log.info("Init "+entity.state)

      //Check IMU, if "off", turn "on". if not calibrate, calibrate.
      for ( imu in entity.imuStatus) {
        bp.log.info("imu "+imu)
        if (entity.imuStatus[imu].equals("off") )  // maybee doesn't requiered
          sync({request: Event('imuOn', {no:imu})})
      }

      for ( str in entity.strStatus) {
        if (entity.strStatus[str].equals("off"))   // maybee doesn't requiered
          sync({request: Event('strOn', {no:str})})
      }
      for ( thruster in entity.thrusterStatus) {
        if (entity.thrusterStatus[thruster].equals("off"))   // maybee doesn't requiered
          sync({request: Event('thrusterOn', {no:thruster})})
        sync({request: Event('openUpstreamValves', {no:thruster})})
      }

      sync({request: Event('powerOnLulav')})
      sync({request: Event('powerOnRFLandingSensor')})

      controlMonitor = function(device, landingLimit)
      {
         bthread( 'Control monitor ' + device + ' ' + landingLimit , function()
         {
              while (true){
                //Do Monitoring of 'device'
                sync({waitFor: bp.all})
              }
        })
      }
      controlMonitor('termal', entity.landinLimitSec);
      controlMonitor('baterryCharge', entity.landinLimitSec);
      controlMonitor('batteryDOD', entity.landinLimitSec);

      if (entity.initCommStatus.equals('off') || entity.initCommStatus.equals('initCommEmer'))
        sync({request: Event('activeInitComm')})

      sync({request: Event('doBitImsAndPld')})

      e = sync({request: Event('initEnded')})

  })

  bthread('imu Turn On', function () {
    while(true){
      e = sync({waitFor: AnyImuOn})
        bp.log.info("in imuOn "+e.data.no)
        //Do turn on imu[x]
      sync({request: Event('imuTurnedOn',{no:e.data.no})})

    }
  })

  ctx.bthread('doImuCalib', 'imu Calib req', function (entity) {
    while(true){
      //Do imu[x] calibration
      //reset calibration timeoute for imu[x]
      sync({request:Event('imuCalibrated', {no:entity.imuCurrentNo})})
    }
  })

  bthread('str Turn On', function (entity) {
    while(true){
      e = sync({waitFor: AnyStrOn})
      //Do turn on str[x]
      sync({request: Event('strIsOn', {no:e.data.no})})
    }
  })

  bthread('thruster Turn On', function () {
    while(true){
      e = sync({waitFor: AnyThrusterOn})
      //Do turn on thruster[x]
      sync({request: Event("thrusterIsOn", {no:e.data.no})})
    }

  })

  bthread('open upstream thruster', function (entity) {
    while(true){
      e = sync({waitFor: [Event('openUpstreamValves')]})
      entity.thrusterStatus[e.data.no] = "on"
      //Do open upstream valves for thruster[x]
    }
  })

    bthread('power on lulav', function () {
      while(true){
        e = sync({waitFor: [Event('powerOnLulav')]})
        //Do power on lulav landing sesnsor
        sync({request: Event("lulaStatusOn")})
      }
    })
    bthread('power on RF landing sensor', function () {
      while(true){
        e = sync({waitFor: [Event('powerOnRFLandingSensor')]})
        //Do power on RF landing sesnsor
        sync({request: Event("RFLandingSensorOn")})
      }
    })
    bthread('Active init comm', function () {
      while(true){
        e = sync({waitFor: [Event('activeInitComm')]})
        //initiate communication system
        sync({request: Event('commInit')})
      }
    })

    bthread('Do bit Ims and Payload', function () {
      while(true){
        e = sync({waitFor: [Event('doBitImsAndPld')]})
        doBit = function(device)
        {
           bthread( 'Bit ' + device , function()
           {
                  //Do Bit of 'device'
                  e = sync({request: Event('powerOn'+device)})
                  e = sync({request: Event('operate'+device)})
                  e = sync({request: Event('powerOff'+device)})
          })
        }
        doBit('Ims');  //Do bit to IMS
        doBit('Payload');   //Do bit to Payload
      }
    })
    ctx.bthread('End landing state', 'init ended', function()
    {
       while (true){
           sync({waitFor: Event('initEnded')})
           if (use_accepting_states) {
             // AcceptingState.Continuing()
             AcceptingState.Stopping()
           }
       }
    })




