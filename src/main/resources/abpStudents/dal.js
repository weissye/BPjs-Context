// ctx.populateContext([
//   ctx.Entity("abpData", "abp", {
//     t_seq: 0,
//     r_seq: 0,
//     t2r: [],
//     r2t: [],
//     send_next: 0,
//     received: [],
//     TO_BE_SEND: ['a', 'b', 'c'],
//     SEQ_MAX: 2,
//     CHN_SIZE: 2,
//     CHN_LOSS: true,
//     CHN_REORDERED: true
//   })
// ])
ctx.populateContext([
  ctx.Entity("abpData", "abpStudents", {
    step: 0,
    numTimeout: 0,
    numDataCorrupt: 0,
    numSeqCorrupt: 0,
    numCksmCorrupt: 0,
    numLost: 0,
    TO_BE_SEND: ['a', 'b', 'c'],

    })
])

// ctx.registerQuery('t_send', function (entity) {
//   return entity.t2r.length < entity.CHN_SIZE && entity.send_next < entity.TO_BE_SEND.length
// })
// ctx.registerQuery('t_ackOk', function (entity) {
//   return entity.r2t.length > 0 && entity.r2t[0] == (entity.t_seq + 1) % entity.SEQ_MAX
// })
// ctx.registerQuery('t_ackNok', function (entity) {
//   return entity.r2t.length > 0 && entity.r2t[0] != (entity.t_seq + 1) % entity.SEQ_MAX
// })
// ctx.registerQuery('r_recAck', function (entity) {
//   return entity.t2r.length > 0 && entity.t2r[0][0] == entity.r_seq && entity.r2t.length < entity.CHN_SIZE
// })
// ctx.registerQuery('r_recNak', function (entity) {
//   return entity.t2r.length > 0 && entity.t2r[0][0] != entity.r_seq && entity.r2t.length < entity.CHN_SIZE
// })
// ctx.registerQuery('t2r_loss', function (entity) {
//   return entity.t2r.length > 0 && entity.CHN_LOSS
// })
// ctx.registerQuery('r2t_loss', function (entity) {
//   return entity.r2t.length > 0 && entity.CHN_LOSS
// })
// ctx.registerQuery('t2r_reordered', function (entity) {
//   return entity.t2r.length > 0 && entity.CHN_REORDERED
// })
// ctx.registerQuery('r2t_reordered', function (entity) {
//   return entity.r2t.length > 0 && entity.CHN_REORDERED
// })

// ctx.registerQuery('T_SUCCESS', function (entity) {
//   return entity.received.toString() == entity.TO_BE_SEND.toString()
// })
// ctx.registerQuery('T_DUP_ERROR', function (entity) {
//   return entity.received.filter(x => x == 'a').length > 1 || entity.received.filter(x => x == 'b').length > 1 || entity.received.filter(x => x == 'c').length > 1
// })
// ctx.registerQuery('T_LOST_ERROR', function (entity) {
//   return (entity.received.includes(String('b')) || entity.received.includes(String('c'))) && !entity.received.includes(String('a'))
// })


ctx.registerEffect('doNothing', function () {
  e = ctx.getEntityById('abpData')
  bp.log.info("Do Nothing, e={0}", e);
  ctx.updateEntity(e)
})

ctx.registerEffect('timeOut', function (e) {
  e = ctx.getEntityById('abpData')
  e.numTimeOut +=1
  ctx.updateEntity(e)
})
ctx.registerEffect('dataCorrupt', function (e) {
  e = ctx.getEntityById('abpData')
  e.numDataCorrupt +=1
  ctx.updateEntity(e)
})
ctx.registerEffect('seqCorrupt', function (e) {
  e = ctx.getEntityById('abpData')
  e.numSeqCorrupt +=1
  ctx.updateEntity(e)
})
ctx.registerEffect('cksmCorrupt', function (e) {
  e = ctx.getEntityById('abpData')
  e.numCksmCorrupt +=1
  ctx.updateEntity(e)
})
ctx.registerEffect('lost', function (e) {
  e = ctx.getEntityById('abpData')
  e.numLost +=1
  ctx.updateEntity(e)
})


