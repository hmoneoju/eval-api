package com.hmoneoju.evalapi.resource;

import com.hmoneoju.evalapi.model.Operation;
import com.hmoneoju.evalapi.service.EvalOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping (value = "/api")
public class ApiResource {

    @Autowired
    private EvalOperation evalOperation;

    @RequestMapping(value="/**", method = RequestMethod.POST )
    @ResponseBody
    public Operation resolve(@RequestParam(value="expression") String expression,
                             @RequestHeader HttpHeaders headers) {
        return evalOperation.execute(expression, headers);
    }

}
