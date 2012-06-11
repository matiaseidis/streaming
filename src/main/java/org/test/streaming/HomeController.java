package org.test.streaming;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	public Logger logger = Logger.getLogger(getClass());

	@RequestMapping(value="/", method = RequestMethod.GET)
	public ModelAndView home(ModelAndView m){

		m.setViewName("index");
		return m;
	} 	
}
