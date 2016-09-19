package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author Jason Xiao
 */
@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    private final RabbitTemplate rabbitTemplate;
    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final TaskExecutor taskExecutor;

    @Autowired
    public DemoController(RabbitTemplate rabbitTemplate,
                          AsyncRabbitTemplate asyncRabbitTemplate,
                          TaskExecutor taskExecutor) {
        this.rabbitTemplate = rabbitTemplate;
        this.asyncRabbitTemplate = asyncRabbitTemplate;
        this.taskExecutor = taskExecutor;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity send(@RequestBody Object data) {
        for (int i = 0; i < 100; i++) {
            RequestParam param = new RequestParam();
            param.setId(i);
            taskExecutor.execute(() -> {
                logger.info("Sending message: {}", param.toString());
                Object response = rabbitTemplate.convertSendAndReceive(param);
                logger.info("Get result: {}", response);
            });
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/async", method = RequestMethod.POST)
    public ResponseEntity sendAsync(@RequestBody RequestParam param) throws ExecutionException, InterruptedException {
        logger.info("Sending message: {}", param.toString());
        ListenableFuture<Object> responseFuture = asyncRabbitTemplate.convertSendAndReceive(param);
        logger.info("Waiting for result");
        Object response = responseFuture.get();
        logger.info("Get result: {}", response);
        return ResponseEntity.ok(response);
    }


}
