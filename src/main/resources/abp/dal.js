ctx.populateContext([
  ctx.Entity("abpData", "abp", {
    t_seq: 0,
    r_seq: 0,
    t2r: [],
    r2t: [],
    send_next: 0,
    received: [],
    TO_BE_SEND: [],
    // TO_BE_SEND: ['A', 'B', 'C'],
    SEQ_MAX: 2,
    CHN_SIZE: 3,
    CHN_LOSS: true,
    CHN_REORDERED: true
  })
  // ctx.Entity("abpData", "abp", {t_seq:0, r_seq:0, t2r:[], r2t:[], send_next:0, received:[], TO_BE_SEND:['a', 'b', 'c'], SEQ_MAX:2, CHN_SIZE:2, CHN_LOSS:false, CHN_REORDERED:false})
])

ctx.registerQuery('t_send', function (entity) {
  return entity.t2r.length < entity.CHN_SIZE && entity.send_next < entity.TO_BE_SEND.length
})
ctx.registerQuery('t_ackOk', function (entity) {
  return entity.r2t.length > 0 && entity.r2t[0] == (entity.t_seq + 1) % entity.SEQ_MAX
})
ctx.registerQuery('t_ackNok', function (entity) {
  return entity.r2t.length > 0 && entity.r2t[0] != (entity.t_seq + 1) % entity.SEQ_MAX
})
ctx.registerQuery('r_recAck', function (entity) {
  return entity.t2r.length > 0 && entity.t2r[0][0] == entity.r_seq && entity.r2t.length < entity.CHN_SIZE
})
ctx.registerQuery('r_recNak', function (entity) {
  return entity.t2r.length > 0 && entity.t2r[0][0] != entity.r_seq && entity.r2t.length < entity.CHN_SIZE
})
ctx.registerQuery('t2r_loss', function (entity) {
  return entity.t2r.length > 0 && entity.CHN_LOSS
})
ctx.registerQuery('r2t_loss', function (entity) {
  return entity.r2t.length > 0 && entity.CHN_LOSS
})
ctx.registerQuery('t2r_reorder', function (entity) {
  return entity.t2r.length > 0 && entity.CHN_REORDERED
})
ctx.registerQuery('r2t_reorder', function (entity) {
  return entity.r2t.length > 0 && entity.CHN_REORDERED
})

ctx.registerQuery('T_SUCCESS', function (entity) {
  return entity.send_next==entity.TO_BE_SEND.length && entity.received.toString() == entity.TO_BE_SEND.toString()
})
ctx.registerQuery('T_FAIL', function (entity) {
  return entity.send_next==entity.TO_BE_SEND.length && entity.received.toString() != entity.TO_BE_SEND.toString()
})
// ctx.registerQuery('T_DUP_ERROR', function (entity) {
//   return entity.received.filter(x => x == 'a').length > 1 || entity.received.filter(x => x == 'b').length > 1 || entity.received.filter(x => x == 'c').length > 1
// })
// ctx.registerQuery('T_LOST_ERROR', function (entity) {
//   return (entity.received.includes(String('b')) || entity.received.includes(String('c'))) && !entity.received.includes(String('a'))
// })


ctx.registerEffect('send', function () {
  e = ctx.getEntityById('abpData')
  var t = [e.t_seq, e.TO_BE_SEND[e.send_next]]
  e.t2r.push(t)
  // bp.log.info("Effect for send, e={0}", e);
  ctx.updateEntity(e)
})

ctx.registerEffect('ackOk', function (e) {
  e = ctx.getEntityById('abpData')
  e.r2t.shift()
  e.t_seq = (e.t_seq + 1) % e.SEQ_MAX
  e.send_next += 1
  // bp.log.info("Effect for ackOk, e={0}", e);
  ctx.updateEntity(e)
})

ctx.registerEffect('ackNok', function (e) {
  e = ctx.getEntityById('abpData')
  e.r2t.shift()
  // bp.log.info("Effect for ackNok, e={0}", e);
  ctx.updateEntity(e)
})


ctx.registerEffect('recAck', function (e) {
  e = ctx.getEntityById('abpData')
  x = e.t2r.shift()
  payload = x[1]
  e.r_seq = (e.r_seq + 1) % e.SEQ_MAX
  e.r2t.push(e.r_seq)
  e.received.push(payload)
  // bp.log.info("Effect for recAck, e={0}", e);
  ctx.updateEntity(e)
})

ctx.registerEffect('recNak', function (e) {
  e = ctx.getEntityById('abpData')
  e.t2r.shift()
  e.r2t.push(e.r_seq)
  // bp.log.info("Effect for recNak, e={0}", e);
  ctx.updateEntity(e)
})

ctx.registerEffect('t2rLoss', function (e) {
  e = ctx.getEntityById('abpData')
  e.t2r.shift()
  // bp.log.info("Effect for t2rLoss, e={0}", e);
  ctx.updateEntity(e)
})
ctx.registerEffect('r2tLoss', function (e) {
  e = ctx.getEntityById('abpData')
  e.r2t.shift()
  // bp.log.info("Effect for r2tLoss, e={0}", e);
  ctx.updateEntity(e)
})
ctx.registerEffect('t2rReorder', function (e) {
  e = ctx.getEntityById('abpData')
  e.t2r.reverse()
  // bp.log.info("Effect for t2rReorder, e={0}", e);
  ctx.updateEntity(e)
})
ctx.registerEffect('r2tReorder', function (e) {
  e = ctx.getEntityById('abpData')
  e.r2t.reverse()
  // bp.log.info("Effect for r2tReorder, e={0}", e);
  ctx.updateEntity(e)
})

ctx.registerEffect('dataToBeSend', function (eventData) {
  e = ctx.getEntityById('abpData')
  e.TO_BE_SEND.push(eventData.info)
   // bp.log.info("Effect for dataToBeSend, ee.data={0}", e);
  ctx.updateEntity(e)
})
ctx.registerEffect('doT2rLost', function () {
  e = ctx.getEntityById('abpData')
  // bp.log.info("Effect for doT2rLost, e.data={0}", e);
  ctx.updateEntity(e)
})
ctx.registerEffect('doR2tLost', function () {
  e = ctx.getEntityById('abpData')
  // bp.log.info("Effect for doR2tLost, e.data={0}", e.data);
  ctx.updateEntity(e)
})
ctx.registerEffect('doT2rReorder', function () {
  e = ctx.getEntityById('abpData')
  // bp.log.info("Effect for doT2rReorder, e.data={0}", e.data);
  ctx.updateEntity(e)
})
ctx.registerEffect('doR2tReorder', function () {
  e = ctx.getEntityById('abpData')
  // bp.log.info("Effect for doR2tReorder, e.data={0}", e);
  ctx.updateEntity(e)
})


