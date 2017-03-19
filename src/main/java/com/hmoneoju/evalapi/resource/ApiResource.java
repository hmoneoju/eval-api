package com.hmoneoju.evalapi.resource;

import com.hmoneoju.evalapi.model.Operation;
import com.hmoneoju.evalapi.service.RetryableServiceOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping (value = "/api")
public class ApiResource {

    @Autowired
    @Qualifier("eval")
    private RetryableServiceOperation evalService;

    @RequestMapping(value="/**", method = RequestMethod.POST )
    @ResponseBody
    public Operation resolve(@RequestParam(value="expression") String expression,
                             @RequestHeader HttpHeaders headers) {
        return (Operation) evalService.execute(expression, headers);
    }

}
