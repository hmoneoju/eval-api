package com.hmoneoju.evalapi.resource;

import com.hmoneoju.evalapi.model.Operation;
import com.hmoneoju.evalapi.service.RetryableServiceOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping (value = "/api")
public class ApiResource {

    @Autowired
    @Qualifier("eval")
    private RetryableServiceOperation evalService;

    @RequestMapping(value="/**", method = RequestMethod.POST )
    @ResponseBody
    public Operation resolve(HttpServletRequest request) {
        return (Operation) evalService.execute(request);
    }

}
