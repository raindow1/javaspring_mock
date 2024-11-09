package com.example.Controller;


import com.example.Model.RequestDTO;
import com.example.Model.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class MainController {

    private Logger log = LoggerFactory.getLogger(MainController.class);
    ObjectMapper mapper = new ObjectMapper();

    @PostMapping(
            value = "/info/postBalances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Object postBalances(@RequestBody RequestDTO requestDTO){
        try{
            String clientId = requestDTO.getClientId();
            char firstDigit = clientId.charAt(0);
            BigDecimal maxLimit;
            String RqUID = requestDTO.getRqUID();
            String currency;
            int decimal_range;

            // Условие на выбор макс. лимита
            if(firstDigit == '8'){
                maxLimit = new BigDecimal(2000);
            } else if (firstDigit == '9') {
                maxLimit = new BigDecimal(1000);
            } else{
                maxLimit = new BigDecimal(10000);
            }

            // Условие на выбор валюты
            if(firstDigit == '8'){
                currency = "US";
            } else if (firstDigit == '9') {
                currency = "EU";
            } else{
                currency = "RUB";
            }

            // Верхняя граница рандомного числа balance(соответствует MaxLimit)
            decimal_range = maxLimit.intValue();

            // Рандомный выбор баланса(меньше макс. лимита)
            BigDecimal max_value = new BigDecimal(decimal_range);
            BigDecimal temp_random_val = new BigDecimal(Math.random());
            BigDecimal rand_decimal = temp_random_val.multiply(max_value);
            BigDecimal random_currency = rand_decimal.setScale(2, BigDecimal.ROUND_DOWN);

            ResponseDTO responseDTO = new ResponseDTO();

            responseDTO.setRqUID(RqUID);
            responseDTO.setClientId(clientId);
            responseDTO.setAccount(requestDTO.getAccount());
            responseDTO.setCurrency(currency);
            responseDTO.setBalance(random_currency);
            responseDTO.setMaxLimit(maxLimit);

            // Логи
            log.error("********** RequestDTO **********" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));
            log.error("********** ResponseDTO **********" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO));

            return responseDTO;


        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}