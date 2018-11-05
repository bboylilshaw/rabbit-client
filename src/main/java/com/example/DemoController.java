package com.example;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author Jason Xiao
 */
@RestController
@Slf4j
public class DemoController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    private AsyncRabbitTemplate asyncRabbitTemplate;

    @Autowired
    private TaskExecutor taskExecutor;

    @PostMapping(value = "/send")
    public ResponseEntity send() {
        RequestParam req = new RequestParam(1L, "Jason Xiao");
        rabbitTemplate.convertAndSend(req);
//        for (long i = 0; i < 100; i++) {
//            RequestParam param = new RequestParam();
//            param.setId(i);
//            taskExecutor.execute(() -> {
//                log.info("Sending message: {}", param.toString());
//                Object response = rabbitTemplate.convertSendAndReceive(param);
//                log.info("Get result: {}", response);
//            });
//        }
        return ResponseEntity.ok("OK");
    }

//    @PostMapping(value = "/async")
//    public ResponseEntity sendAsync(@RequestBody RequestParam param) throws ExecutionException, InterruptedException {
//        log.info("Sending message: {}", param.toString());
//        ListenableFuture<Object> responseFuture = asyncRabbitTemplate.convertSendAndReceive(param);
//        log.info("Waiting for result");
//        Object response = responseFuture.get();
//        log.info("Get result: {}", response);
//        return ResponseEntity.ok(response);
//    }


}
