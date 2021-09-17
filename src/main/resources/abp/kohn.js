var event1, event2
var eventList = ['send', 'ackOk', 'ackNok', 'recAck', 'recNak', 'r2tLoss', 't2rLoss', 'r2tReorder',
    't2rReorder', 'success', 'dup_error', 'lostError']
var exitRule = ['success', 'dup_error', 'lostError']
bthread('GenerateKohnCTD', function () {
    for ( event1 of eventList) {
        for (event2 of eventList ){

            f = function(eventA, eventB) {
                bthread('k' + eventA + eventB, function () {

                    sync({waitFor: bp.Event(eventA)})
                    sync({waitFor: bp.Event(eventB)})
                    bp.log.info("--------------2 event1 and event2" + eventA + " " + eventB);
                    if (eventA in exitRule)
                        sync(block(bp.all))
                    var goal = 'Goal' + eventA + eventB
                    // sync({request: bp.Event(goal), block: bp.Event(goal).negate()})
                    sync({request: bp.Event(goal)})
                })
            }
            f(event1, event2)
        }
    }
})

