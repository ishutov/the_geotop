package com.geotop.geotopproject.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class ViewController {

    @RequestMapping(value={"/index", "/"}, method=RequestMethod.GET)
    public String index() {
        return "index";
    }

}
