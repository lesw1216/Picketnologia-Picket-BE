package com.picketlogia.picket.websocket;

import com.picketlogia.picket.api.seat.service.SeatHoldService;
import com.picketlogia.picket.websocket.model.SeatSelectionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SeatHoldService seatHoldService;

    // 서버가 메세지 받는 주소(Send)
    //order/seats/${eventIdx}/{select}
    @MessageMapping("/seats/{roundId}/{datetime}/select")
    public void selectSeat(@DestinationVariable Long roundId, @DestinationVariable String datetime, SeatSelectionMessage message) {
        // 1. Redis에 좌석 상태 업데이트
        seatHoldService.updateSeatStatus(roundId, datetime, message.getSeatName(), message.getAction());

        // 2. 토픽을 구독하는 클라이언트들에게 변경된 좌석 정보 전달
        // 클라이언트는 /topic/seats/{roundId}를 구독해야함ㅇㅇ
        // 보내는 주소(subscribe(/topic/seats/${eventIdx})
        messagingTemplate.convertAndSend("/topic/seats/" + roundId + "/" + datetime, message);
    }

    // 서버가 메세지 받는 주소(Send)
    //order/seats/${eventIdx}/{select}
    @MessageMapping("/seats/{roundTimeIdx}")
    public void selectSeatV2(@DestinationVariable Long roundTimeIdx,
                             SeatSelectionMessage message) {

        String action = message.getAction();

        // 좌석을 해제하면 redis에서 제거
        if (action.equals("deselect")) {
            seatHoldService.deleteSeatStatus(roundTimeIdx, message.getSeatIdx());
            seatHoldService.deleteSeparateSeat(roundTimeIdx, message.getSeatIdx());
            messagingTemplate.convertAndSend("/topic/seats/" + roundTimeIdx, message);
            return;
        }

        // 1. Redis에 좌석 상태 업데이트
//        seatStatusService.updateSeatStatus(roundId, datetime, message.getSeatName(), message.getAction());
        seatHoldService.updateSeatStatusV2(roundTimeIdx, message.getSeatIdx(), message.getAction());
        seatHoldService.saveSeparateSeat(roundTimeIdx, message.getSeatIdx());

        // 2. 토픽을 구독하는 클라이언트들에게 변경된 좌석 정보 전달
        // 클라이언트는 /topic/seats/{roundId}를 구독해야함ㅇㅇ
        // 보내는 주소(subscribe(/topic/seats/${eventIdx})
        messagingTemplate.convertAndSend("/topic/seats/" + roundTimeIdx, message);
    }
}