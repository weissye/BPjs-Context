bthread('StartSimulator', function (entity) {
  // sync({request: Event('startSimulator'), block: [bp.all]})
  sync({request: Event('startSimulator')})
})
bthread('DataToBeSend', function (entity) {
    sync({request: Event('dataToBeSend', {data:"A"}), block: Event("success")})
    sync({request: Event('dataToBeSend', {data:"B"})})
    sync({request: Event('dataToBeSend', {data:"C"})})
})
bthread('DoT2rLost', function (entity) {
  while (true) {
    sync({request: Event('doT2rLost')}, 10000)
  }
})
bthread('DoR2rLost', function (entity) {
  while (true) {
    sync({request: Event('doR2tLost')}, 10000)
  }
})
bthread('DoT2rReorder', function (entity) {
  while (true) {
    sync({request: Event('doT2rReorder')}, 10000)
  }
})
bthread('DoR2tReorder', function (entity) {
  while (true) {
    sync({request: Event('doR2tReorder')}, 10000)
  }
})
